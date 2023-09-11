package com.abrnoc.application.presentation.connection

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.RemoteException
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.preference.PreferenceDataStore
import com.abrnoc.application.R
import com.abrnoc.application.connection.database.GroupManager
import com.abrnoc.application.connection.database.ProfileManager
import com.abrnoc.application.connection.group.GroupUpdater
import com.abrnoc.application.connection.group.RawUpdater
import com.abrnoc.application.connection.neko.Util
import com.abrnoc.application.databinding.ActivityConnBinding
import com.abrnoc.application.ftm.AbstractBean
import com.abrnoc.application.ftm.KryoConverters
import com.abrnoc.application.presentation.connection.fragment.ConfigurationFragment
import com.abrnoc.application.presentation.connection.fragment.ToolbarFragment
import com.abrnoc.application.presentation.connection.profile.ThemedActivity
import com.abrnoc.application.presentation.viewModel.DefaultConfigViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.nekohasekai.sagernet.aidl.ISagerNetService
import io.nekohasekai.sagernet.group.GroupInterfaceAdapter
import io.nekohasekai.sagernet.ktx.SubscriptionFoundException
import io.nekohasekai.sagernet.ktx.alert
import io.nekohasekai.sagernet.widget.ListHolderListener

@AndroidEntryPoint
class ConnActivity : ThemedActivity(), SagerConnection.Callback,
    OnPreferenceDataStoreChangeListener {
    lateinit var binding: ActivityConnBinding
    private val defaultConfigsViewModel: DefaultConfigViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        defaultConfigsViewModel.defaultConfigFlow.observe(this) { state ->
            state?.let {
                it.configs?.forEach { config ->
                        val text = config.url
                    if (text.isBlank()) {
                        println(getString(R.string.clipboard_empty))
                    } else runOnDefaultDispatcher {
                        try {
                            val proxies = RawUpdater.parseRaw(text)
                            if (proxies.isNullOrEmpty()) onMainDispatcher {
                                println(getString(R.string.no_proxies_found_in_clipboard))
                            } else import(proxies)
                        } catch (e: SubscriptionFoundException) {
                            importSubscription(Uri.parse(e.link))
                        } catch (e: Exception) {
                            Logs.w(e)

                            onMainDispatcher {
                                snackbar(e.readableMessage).show()
                            }
                        }
                    }
                }
            }
        }
        binding = ActivityConnBinding.inflate(layoutInflater)
        binding.fab.initProgress(binding.fabProgress)
        binding.fab.setOnClickListener {
            if (DataStore.serviceState.canStop) SagerNet.stopService() else connect.launch(
                null
            )
        }
        binding.stats.setOnClickListener { if (DataStore.serviceState.connected) binding.stats.testConnection() }
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.coordinator, ListHolderListener)
        changeState(BaseService.State.Idle)
        connection.connect(this, this)
        DataStore.configurationStore.registerChangeListener(this)
        GroupManager.userInterface = GroupInterfaceAdapter(this)

        if (intent?.action == Intent.ACTION_VIEW) {
            onNewIntent(intent)
        }
        if (savedInstanceState == null) {
            displayFragmentWithId()
        }

    }
    suspend fun import(proxies: List<AbstractBean>) {
        val targetId = DataStore.selectedGroupForImport()
        for (proxy in proxies) {
            ProfileManager.createProfile(targetId, proxy)
        }
        onMainDispatcher {
            DataStore.editingGroup = targetId
            snackbar(
                this@ConnActivity.resources.getQuantityString(
                    R.plurals.added, proxies.size, proxies.size
                )
            ).show()
        }

    }
    override fun onPreferenceDataStoreChanged(store: PreferenceDataStore, key: String) {
        when (key) {
            Key.SERVICE_MODE -> onBinderDied()
            Key.PROXY_APPS, Key.BYPASS_MODE, Key.INDIVIDUAL -> {
                if (DataStore.serviceState.canStop) {
                    snackbar(getString(R.string.restart)).setAction(R.string.apply) {
                        SagerNet.reloadService()
                    }.show()
                }
            }
        }

    }

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        changeState(state, msg, true)
    }

    val connection = SagerConnection(true)
    override fun onServiceConnected(service: ISagerNetService) {
        try {
            BaseService.State.values()[service.state]
        } catch (_: RemoteException) {
            BaseService.State.Idle
        }
    }

    private fun changeState(
        state: BaseService.State,
        msg: String? = null,
        animate: Boolean = false,
    ) {
        DataStore.serviceState = state

        if (!DataStore.serviceState.connected) {
            statsUpdated(emptyList())
        }

        binding.fab.changeState(state, DataStore.serviceState, animate)
        binding.stats.changeState(state)
        if (msg != null) snackbar(getString(R.string.vpn_error, msg)).show()

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

    private val connect = registerForActivityResult(VpnRequestActivity.StartService()) {
        if (it) snackbar(R.string.vpn_permission_denied).show()
    }

    fun urlTest(): Int {
        if (!DataStore.serviceState.connected || connection.service == null) {
            error("not started")
        }
        return connection.service!!.urlTest()
    }

    suspend fun importSubscription(uri: Uri) {
        val group: ProxyGroup

        val url = uri.getQueryParameter("url")
        if (!url.isNullOrBlank()) {
            group = ProxyGroup(type = GroupType.SUBSCRIPTION)
            val subscription = SubscriptionBean()
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
                    ProxyGroup().apply { export = true }, Util.zlibDecompress(Util.b64Decode(data))
                ).apply {
                    export = false
                }
            } catch (e: Exception) {
                onMainDispatcher {
                    alert(e.readableMessage).show()
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

            MaterialAlertDialogBuilder(this@ConnActivity).setTitle(R.string.subscription_import)
                .setMessage(getString(R.string.subscription_import_message, name))
                .setPositiveButton(R.string.yes) { _, _ ->
                    runOnDefaultDispatcher {
                        finishImportSubscription(group)
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()

        }

    }
    suspend fun importProfile(uri: Uri) {
        val profile = try {
            io.nekohasekai.sagernet.ktx.parseProxies(uri.toString()).getOrNull(0) ?: error(getString(R.string.no_proxies_found))
        } catch (e: Exception) {
            onMainDispatcher {
                alert(e.readableMessage).show()
            }
            return
        }

        onMainDispatcher {
            MaterialAlertDialogBuilder(this@ConnActivity).setTitle(R.string.profile_import)
                .setMessage(getString(R.string.profile_import_message, profile.displayName()))
                .setPositiveButton(R.string.yes) { _, _ ->
                    runOnDefaultDispatcher {
                        finishImportProfile(profile)
                    }
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }

    }

    private suspend fun finishImportProfile(profile: AbstractBean) {
        val targetId = DataStore.selectedGroupForImport()

        ProfileManager.createProfile(targetId, profile)

        onMainDispatcher {
//            displayFragmentWithId(R.id.nav_configuration)

            snackbar(resources.getQuantityString(R.plurals.added, 1, 1)).show()
        }
    }





    private suspend fun finishImportSubscription(subscription: ProxyGroup) {
        GroupManager.createGroup(subscription)
        GroupUpdater.startUpdate(subscription, true)
    }

    fun displayFragmentWithId(): Boolean {
        displayFragment(ConfigurationFragment())

    return true
}
    fun displayFragment(fragment: ToolbarFragment) {
        if (fragment is ConfigurationFragment) {
            binding.stats.allowShow = true
            binding.fab.show()
        } else if (!DataStore.showBottomBar) {
            binding.stats.allowShow = false
            binding.stats.performHide()
            binding.fab.hide()
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_holder, fragment)
            .commitAllowingStateLoss()
//        binding.drawerLayout.closeDrawers()
    }
}