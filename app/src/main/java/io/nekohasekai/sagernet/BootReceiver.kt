package io.nekohasekai.sagernet

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.abrnoc.application.connection.bg.SubscriptionUpdater
import com.abrnoc.application.presentation.connection.DataStore
import com.abrnoc.application.presentation.connection.SagerNet
import com.abrnoc.application.presentation.connection.app
import com.abrnoc.application.presentation.connection.runOnDefaultDispatcher

class BootReceiver : BroadcastReceiver() {
    companion object {
        private val componentName by lazy { ComponentName(app, io.nekohasekai.sagernet.BootReceiver::class.java) }
        var enabled: Boolean
            get() = app.packageManager.getComponentEnabledSetting(io.nekohasekai.sagernet.BootReceiver.Companion.componentName) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            set(value) = app.packageManager.setComponentEnabledSetting(
                io.nekohasekai.sagernet.BootReceiver.Companion.componentName,
                if (value) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                },
                PackageManager.DONT_KILL_APP
            )
    }

    override fun onReceive(context: Context, intent: Intent) {
        runOnDefaultDispatcher {
            SubscriptionUpdater.reconfigureUpdater()
        }

        if (!DataStore.persistAcrossReboot) { // sanity check
            io.nekohasekai.sagernet.BootReceiver.Companion.enabled = false
            return
        }

        val doStart = when (intent.action) {
            Intent.ACTION_LOCKED_BOOT_COMPLETED -> false // DataStore.directBootAware
            else -> Build.VERSION.SDK_INT < 24 || SagerNet.user.isUserUnlocked
        } && DataStore.selectedProxy > 0

        if (doStart) SagerNet.startService()
    }
}
