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

package com.abrnoc.application.presentation.connection.profile

import android.os.Bundle
import androidx.preference.EditTextPreference
import com.abrnoc.application.R
import com.abrnoc.application.connection.preference.EditTextPreferenceModifiers
import com.abrnoc.application.ftm.shadowsocksr.ShadowsocksRBean
import com.abrnoc.application.presentation.connection.DataStore
import com.abrnoc.application.presentation.connection.Key
import com.takisoft.preferencex.PreferenceFragmentCompat


class ShadowsocksRSettingsActivity : ProfileSettingsActivity<ShadowsocksRBean>() {

    override fun createEntity() = ShadowsocksRBean()

    override fun ShadowsocksRBean.init() {
        DataStore.profileName = name
        DataStore.serverAddress = serverAddress
        DataStore.serverPort = serverPort
        DataStore.serverPassword = password
        DataStore.serverMethod = method
        DataStore.serverProtocol = protocol
        DataStore.serverProtocolParam = protocolParam
        DataStore.serverObfs = obfs
        DataStore.serverObfsParam = obfsParam
    }

    override fun ShadowsocksRBean.serialize() {
        name = DataStore.profileName
        serverAddress = DataStore.serverAddress
        serverPort = DataStore.serverPort
        method = DataStore.serverMethod
        password = DataStore.serverPassword
        protocol = DataStore.serverProtocol
        protocolParam = DataStore.serverProtocolParam
        obfs = DataStore.serverObfs
        obfsParam = DataStore.serverObfsParam
    }

    override fun PreferenceFragmentCompat.createPreferences(
        savedInstanceState: Bundle?,
        rootKey: String?,
    ) {
        addPreferencesFromResource(R.xml.shadowsocksr_preferences)
        findPreference<EditTextPreference>(Key.SERVER_PORT)!!.apply {
            setOnBindEditTextListener(EditTextPreferenceModifiers.Port)
        }
        findPreference<EditTextPreference>(Key.SERVER_PASSWORD)!!.apply {
            summaryProvider = PasswordSummaryProvider
        }

    }

}