/******************************************************************************
 *                                                                            *
 * Copyright (C) 2021 by nekohasekai <contact-sagernet@sekai.icu>             *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 *  (at your option) any later version.                                       *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program. If not, see <http://www.gnu.org/licenses/>.       *
 *                                                                            *
 ******************************************************************************/

package io.nekohasekai.sagernet.group

import android.net.Uri
import com.abrnoc.application.R
import com.abrnoc.application.connection.neko.USER_AGENT
import com.abrnoc.application.presentation.connection.Logs
import com.abrnoc.application.presentation.connection.applyDefaultValues
import com.abrnoc.application.presentation.connection.filterIsInstance
import com.abrnoc.application.presentation.connection.getStr
import io.nekohasekai.sagernet.ExtraType
import io.nekohasekai.sagernet.database.DataStore
import io.nekohasekai.sagernet.database.GroupManager
import io.nekohasekai.sagernet.database.ProxyEntity
import io.nekohasekai.sagernet.database.ProxyGroup
import io.nekohasekai.sagernet.database.SagerDatabase
import io.nekohasekai.sagernet.database.SubscriptionBean
import io.nekohasekai.sagernet.ftm.shadowsocks.parseShadowsocks
import io.nekohasekai.sagernet.ktx.app
import libcore.Libcore
import org.json.JSONObject

object SIP008Updater : GroupUpdater() {

    override suspend fun doUpdate(
        proxyGroup: ProxyGroup,
        subscription: SubscriptionBean,
        userInterface: GroupManager.Interface?,
        byUser: Boolean
    ) {

        val link = subscription.link
        val sip008Response: JSONObject
        if (link.startsWith("content://")) {
            val contentText = app.contentResolver.openInputStream(Uri.parse(link))
                ?.bufferedReader()
                ?.readText()

            sip008Response = contentText?.let { JSONObject(contentText) }
                ?: error(app.getString(R.string.no_proxies_found_in_subscription))
        } else {

            val response = Libcore.newHttpClient().apply {
                modernTLS()
                trySocks5(DataStore.socksPort)
            }.newRequest().apply {
                setURL(subscription.link)
                setUserAgent(subscription.customUserAgent.takeIf { it.isNotBlank() } ?: USER_AGENT)
            }.execute()

            sip008Response = JSONObject(response.contentString)
        }

        subscription.bytesUsed = sip008Response.optLong("bytesUsed", -1)
        subscription.bytesRemaining = sip008Response.optLong("bytesRemaining", -1)
        subscription.applyDefaultValues()

        val servers = sip008Response.getJSONArray("servers").filterIsInstance<JSONObject>()

        var profiles = mutableListOf<io.nekohasekai.sagernet.ftm.AbstractBean>()

        for (profile in servers) {
            val bean = profile.parseShadowsocks()
            appendExtraInfo(profile, bean)
            profiles.add(bean.applyDefaultValues())
        }

        if (subscription.forceResolve) forceResolve(profiles, proxyGroup.id)

        val exists = SagerDatabase.proxyDao.getByGroup(proxyGroup.id)
        val duplicate = ArrayList<String>()
        if (subscription.deduplication) {
            Logs.d("Before deduplication: ${profiles.size}")
            val uniqueProfiles = LinkedHashSet<io.nekohasekai.sagernet.ftm.AbstractBean>()
            val uniqueNames = HashMap<io.nekohasekai.sagernet.ftm.AbstractBean, String>()
            for (proxy in profiles) {
                if (!uniqueProfiles.add(proxy)) {
                    val index = uniqueProfiles.indexOf(proxy)
                    if (uniqueNames.containsKey(proxy)) {
                        val name = uniqueNames[proxy]!!.replace(" ($index)", "")
                        if (name.isNotBlank()) {
                            duplicate.add("$name ($index)")
                            uniqueNames[proxy] = ""
                        }
                    }
                    duplicate.add(proxy.displayName() + " ($index)")
                } else {
                    uniqueNames[proxy] = proxy.displayName()
                }
            }
            uniqueProfiles.retainAll(uniqueNames.keys)
            profiles = uniqueProfiles.toMutableList()
        }

        Logs.d("New profiles: ${profiles.size}")

        val profileMap = profiles.associateBy { it.profileId }
        val toDelete = ArrayList<ProxyEntity>()
        val toReplace = exists.mapNotNull { entity ->
            val profileId = entity.requireBean().profileId
            if (profileMap.contains(profileId)) profileId to entity else let {
                toDelete.add(entity)
                null
            }
        }.toMap()

        Logs.d("toDelete profiles: ${toDelete.size}")
        Logs.d("toReplace profiles: ${toReplace.size}")

        val toUpdate = ArrayList<ProxyEntity>()
        val added = mutableListOf<String>()
        val updated = mutableMapOf<String, String>()
        val deleted = toDelete.map { it.displayName() }

        var userOrder = 1L
        var changed = toDelete.size
        for ((profileId, bean) in profileMap.entries) {
            val name = bean.displayName()
            if (toReplace.contains(profileId)) {
                val entity = toReplace[profileId]!!
                val existsBean = entity.requireBean()
                existsBean.applyFeatureSettings(bean)
                when {
                    existsBean != bean -> {
                        changed++
                        entity.putBean(bean)
                        toUpdate.add(entity)
                        updated[entity.displayName()] = name

                        Logs.d("Updated profile: [$profileId] $name")
                    }
                    entity.userOrder != userOrder -> {
                        entity.putBean(bean)
                        toUpdate.add(entity)
                        entity.userOrder = userOrder

                        Logs.d("Reordered profile: [$profileId] $name")
                    }
                    else -> {
                        Logs.d("Ignored profile: [$profileId] $name")
                    }
                }
            } else {
                changed++
                SagerDatabase.proxyDao.addProxy(
                    ProxyEntity(
                    groupId = proxyGroup.id, userOrder = userOrder
                ).apply {
                    putBean(bean)
                })
                added.add(name)
                Logs.d("Inserted profile: $name")
            }
            userOrder++
        }

        SagerDatabase.proxyDao.updateProxy(toUpdate).also {
            Logs.d("Updated profiles: $it")
        }

        SagerDatabase.proxyDao.deleteProxy(toDelete).also {
            Logs.d("Deleted profiles: $it")
        }

        val existCount = SagerDatabase.proxyDao.countByGroup(proxyGroup.id).toInt()

        if (existCount != profileMap.size) {
            Logs.e("Exist profiles: $existCount, new profiles: ${profileMap.size}")
        }

        subscription.lastUpdated = (System.currentTimeMillis() / 1000).toInt()
        SagerDatabase.groupDao.updateGroup(proxyGroup)
        finishUpdate(proxyGroup)

        userInterface?.onUpdateSuccess(
            proxyGroup, changed, added, updated, deleted, duplicate, byUser
        )
    }

    fun appendExtraInfo(profile: JSONObject, bean: io.nekohasekai.sagernet.ftm.AbstractBean) {
        bean.extraType = ExtraType.SIP008
        bean.profileId = profile.getStr("id")
    }

}