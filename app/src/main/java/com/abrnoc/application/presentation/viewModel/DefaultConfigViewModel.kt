package com.abrnoc.application.presentation.viewModel

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matsuri.nya.utils.Util
import com.abrnoc.application.presentation.connection.SubscriptionFoundException
import com.abrnoc.application.presentation.connection.onMainDispatcher
import com.abrnoc.application.presentation.connection.runOnDefaultDispatcher
import com.abrnoc.application.presentation.viewModel.event.ProxyEvent
import com.abrnoc.application.presentation.viewModel.model.DefaultConfig
import com.abrnoc.application.presentation.viewModel.state.DefaultConfigState
import com.abrnoc.domain.common.Result
import com.abrnoc.domain.connection.GetDefaultConfigUseCase
import com.github.shadowsocks.plugin.ProfileManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.nekohasekai.sagernet.GroupType
import io.nekohasekai.sagernet.SagerNet
import io.nekohasekai.sagernet.SubscriptionType
import io.nekohasekai.sagernet.bg.BaseService
import io.nekohasekai.sagernet.bg.SagerConnection
import io.nekohasekai.sagernet.database.DataStore
import io.nekohasekai.sagernet.database.GroupManager
import io.nekohasekai.sagernet.database.ProxyEntity
import io.nekohasekai.sagernet.database.ProxyGroup
import io.nekohasekai.sagernet.database.SagerDatabase
import io.nekohasekai.sagernet.database.SubscriptionBean
import io.nekohasekai.sagernet.ftm.AbstractBean
import io.nekohasekai.sagernet.ftm.KryoConverters
import io.nekohasekai.sagernet.group.GroupUpdater
import io.nekohasekai.sagernet.group.RawUpdater
import io.nekohasekai.sagernet.ktx.readableMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DefaultConfigViewModel @Inject constructor(
    private val getDefaultConfigUseCase: GetDefaultConfigUseCase,
) : ViewModel() {
    private var _configState = MutableStateFlow(DefaultConfigState())
    val configState: StateFlow<DefaultConfigState> = _configState
    val connection = SagerConnection(true)

    //    lateinit var proxyGroup: ProxyGroup
    var selected = false
    var configurationIdList: MutableList<Long> = mutableListOf()
    val configurationList = HashMap<Long, ProxyEntity>()
    val select: Boolean = false
    val selectedItem: ProxyEntity? = null
    var groupList: ArrayList<ProxyGroup> = ArrayList()

    init {
        getAllConfigs()
//        reloadProfiles()
//        DataStore.profileCacheStore.registerChangeListener(this)
    }

    fun onEvent(event: ProxyEvent) {
        var selectedProfileIndex = -1
        when (event) {
            is ProxyEvent.ConfigEvent -> {
                val profileAccess = Mutex()
                val selectedProxy = event.defaultConfig.id ?: DataStore.selectedProxy
                val proxyEntity by lazy { SagerDatabase.proxyDao.getById(selectedProxy) }
                var update: Boolean
                var lastSelected: Long
                // (ConfigurationFragment.SelectCallback).returnProfile(selectedItem?.id ?: 1L)
                runOnDefaultDispatcher {
                    profileAccess.withLock {
                        update = DataStore.selectedProxy != event.defaultConfig.id
                        lastSelected = DataStore.selectedProxy
                        Timber.i("^^", "timber workes")
                        DataStore.selectedProxy = event.defaultConfig.id ?: 1L
//                    onMainDispatcher {
//                        selectedView.visibility = View.VISIBLE
//                    }
                    }
                }
            }
        }
    }

    private fun getAllConfigs() {
        viewModelScope.launch {
            getDefaultConfigUseCase(Unit).collect { result ->
                when (result) {
                    is Result.Error -> {
                        _configState.value = DefaultConfigState(
                            error = result.exception.message ?: "An unexpected error occured",
                        )
                    }

                    Result.Loading -> {
                        _configState.value = DefaultConfigState(isLoading = true)
                    }

                    is Result.Success -> {
                        var id = 1
                        SagerDatabase.proxyDao.reset()
                        SagerDatabase.proxyDao.deleteAllProxyEntities()
                        SagerDatabase.proxyDao.clearPrimaryKey()
                        result.data.forEach { config ->
                            try {
                                var url = ""
                                if (config.protocol == "SSH") {
                                    val stringBuilder = StringBuilder()
                                    stringBuilder.append("ssh://")
                                    stringBuilder.append("&address=").append(config.address)
                                    stringBuilder.append("&port=").append(config.port)
                                    stringBuilder.append("&username=").append(config.username)
                                    stringBuilder.append("&password=").append(config.password)
                                    url = stringBuilder.toString()
                                } else {
                                    url = config.url
                                }
                                val proxies = RawUpdater.parseRaw(url)
                                println(" the proxies are : $proxies")
                                if (proxies.isNullOrEmpty()) {
                                    onMainDispatcher {
                                        Timber.e("Error", "Proxy Not Found")
                                    }
                                } else {
                                    import(proxies)
                                }
                            } catch (e: SubscriptionFoundException) {
                                importSubscription(Uri.parse(e.link))
                            }
//                        reloadProfiles()
                        }
                        _configState.value = DefaultConfigState(
                            configs = result.data.map { it ->
                                DefaultConfig(
                                    (id++).toLong(),
                                    it.address,
                                    it.alpn,
                                    it.country,
                                    it.fingerprint,
                                    it.flag,
                                    it.password,
                                    it.port,
                                    it.protocol,
                                    it.security,
                                    it.sni,
                                    it.type,
                                    it.url
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    suspend fun import(proxies: List<AbstractBean>) {
        val targetId = DataStore.selectedGroupForImport()
        for (proxy in proxies) {
            ProfileManager.createProfile(targetId, proxy)
        }
        onMainDispatcher {
            DataStore.editingGroup = targetId
            Timber.i("proxies", proxies.size)
//            snackbar(
//                this@ConnActivity.resources.getQuantityString(
//                    R.plurals.added, proxies.size, proxies.size
//                )
//            ).show()
        }
    }

    private fun getItem(profileId: Long): ProxyEntity {
        var profile = configurationList[profileId]
        if (profile == null) {
            profile = ProfileManager.getProfile(profileId)
            if (profile != null) {
                configurationList[profileId] = profile
            }
        }
        return profile!!
    }

    private fun getItemAt(index: Int) = getItem(configurationIdList[index])
    suspend fun importSubscription(uri: Uri) {
        val group: ProxyGroup

        val url = uri.getQueryParameter("url")
        if (!url.isNullOrBlank()) {
            group = ProxyGroup(type = GroupType.SUBSCRIPTION)
            val subscription =
                SubscriptionBean()
            group.subscription = subscription

            // cleartext format
            subscription.link = url
            group.name = uri.getQueryParameter("name")

            val type = uri.getQueryParameter("type")
            when (type?.lowercase()) {
                "sip008" -> {
                    subscription.type = SubscriptionType.SIP008
                }
            }
        } else {
            val data = uri.encodedQuery.takeIf { !it.isNullOrBlank() } ?: return
            try {
                group = KryoConverters.deserialize(
                    ProxyGroup().apply { export = true },
                    Util.zlibDecompress(Util.b64Decode(data))
                ).apply {
                    export = false
                }
            } catch (e: Exception) {
                onMainDispatcher {
//                    alert(e.readableMessage).show()
                    Timber.i("vpns", e.readableMessage)
                }
                return
            }
        }

        val name = group.name.takeIf { !it.isNullOrBlank() } ?: group.subscription?.link
        ?: group.subscription?.token
        if (name.isNullOrBlank()) return

        group.name = group.name.takeIf { !it.isNullOrBlank() }
            ?: ("Subscription #" + System.currentTimeMillis())

        onMainDispatcher {
//            displayFragmentWithId(R.id.nav_group)
            finishImportSubscription(group)
        }
    }

    private suspend fun finishImportSubscription(subscription: ProxyGroup) {
        GroupManager.createGroup(subscription)
        GroupUpdater.startUpdate(subscription, true)
    }


    private fun changeState(
        state: BaseService.State,
        msg: String? = null,
        animate: Boolean = false,
    ) {
        DataStore.serviceState = state

        if (!DataStore.serviceState.connected) {
        }

        when (state) {
            BaseService.State.Stopped -> {
                runOnDefaultDispatcher {
                    // refresh view
                    ProfileManager.postUpdate(DataStore.currentProfile)
                }
            }

            else -> {}
        }
    }



    fun onClickConnect(connect: ActivityResultLauncher<Void?>) {
//         val connect = activityContext.registerForActivityResult(StartService()) {}
        if (DataStore.serviceState.canStop) SagerNet.stopService() else connect.launch(
            null
        )
//        connect.launch(null)
    }

}
