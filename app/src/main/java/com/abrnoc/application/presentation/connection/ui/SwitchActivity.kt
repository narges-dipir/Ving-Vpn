package com.abrnoc.application.presentation.connection.ui

import android.os.Bundle
import com.abrnoc.application.R
import com.abrnoc.application.connection.database.ProfileManager
import com.abrnoc.application.presentation.connection.DataStore
import com.abrnoc.application.presentation.connection.SagerNet
import com.abrnoc.application.presentation.connection.fragment.ConfigurationFragment
import com.abrnoc.application.presentation.connection.profile.ThemedActivity
import com.abrnoc.application.presentation.connection.runOnMainDispatcher

class SwitchActivity :
    ThemedActivity(R.layout.layout_empty),
    ConfigurationFragment.SelectCallback {

    override val isDialog = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_holder, ConfigurationFragment(true, null, R.string.action_switch))
            .commitAllowingStateLoss()
    }

    override fun returnProfile(profileId: Long) {
        val old = DataStore.selectedProxy
        DataStore.selectedProxy = profileId
        runOnMainDispatcher {
            ProfileManager.postUpdate(old)
            ProfileManager.postUpdate(profileId)
        }
        SagerNet.reloadService()
        finish()
    }
}
