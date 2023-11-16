package com.abrnoc.application.presentation

import android.content.Intent
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.preference.PreferenceDataStore
import com.abrnoc.application.presentation.bottomNavigation.RootScreen
import com.abrnoc.application.presentation.connection.VpnRequestActivity
import com.abrnoc.application.presentation.connection.runOnDefaultDispatcher
import com.abrnoc.application.presentation.mainConnection.MainConnectionScreen
import com.abrnoc.application.presentation.navigation.Navigation
import com.abrnoc.application.presentation.screens.landing.Landing
import com.abrnoc.application.presentation.screens.landing.Welcome
import com.abrnoc.application.presentation.screens.purchase.PurchaseScreen
import com.abrnoc.application.presentation.screens.signUp.EmailSignIn
import com.abrnoc.application.presentation.screens.signUp.EmailSignUpScreen
import com.abrnoc.application.presentation.screens.signUp.PasswordSignUp
import com.abrnoc.application.presentation.screens.signUp.VerificationSignUp
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.utiles.setupCachePolicy
import com.abrnoc.domain.auth.CheckSignedInUseCase
import com.abrnoc.domain.common.Result
import com.github.shadowsocks.plugin.ProfileManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.nekohasekai.sagernet.Key
import io.nekohasekai.sagernet.R
import io.nekohasekai.sagernet.SagerNet
import io.nekohasekai.sagernet.aidl.AppStats
import io.nekohasekai.sagernet.aidl.ISagerNetService
import io.nekohasekai.sagernet.aidl.TrafficStats
import io.nekohasekai.sagernet.bg.BaseService
import io.nekohasekai.sagernet.bg.SagerConnection
import io.nekohasekai.sagernet.database.DataStore
import io.nekohasekai.sagernet.database.GroupManager
import io.nekohasekai.sagernet.database.ProxyGroup
import io.nekohasekai.sagernet.database.preference.OnPreferenceDataStoreChangeListener
import io.nekohasekai.sagernet.ftm.PluginEntry
import io.nekohasekai.sagernet.group.GroupInterfaceAdapter
import io.nekohasekai.sagernet.group.GroupUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity :
    ComponentActivity(),
    SagerConnection.Callback,
    OnPreferenceDataStoreChangeListener {

    @Inject
    lateinit var checkSignedInUseCase: CheckSignedInUseCase
    private val connectionState = mutableStateOf(BaseService.State.Idle)
    private val trafficState = mutableStateOf(TrafficStats())

    val connection = SagerConnection(true)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        createDatFile()
        setupCachePolicy(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val connect = registerForActivityResult(VpnRequestActivity.StartService()) {
            if (it) Toast.makeText(this, R.string.vpn_permission_denied, Toast.LENGTH_LONG).show()
        }
        changeState(BaseService.State.Idle)
        connection.disconnect(this)
        connection.connect(this, this)
        DataStore.configurationStore.registerChangeListener(this)
        GroupManager.userInterface = GroupInterfaceAdapter(this)


        if (intent?.action == Intent.ACTION_VIEW) {
            onNewIntent(intent)
        }
        lifecycleScope.launch(Dispatchers.Main) {
            val result = checkSignedInUseCase(Unit)
            when (result) {
                is Result.Error -> {
                    Log.i("show", "shwo error")
                }

                Result.Loading -> {
                    Log.i("show", "show Loading")
                }

                is Result.Success -> {
                    setContent {
                        AbrnocApplicationTheme {
                            RootScreen(
                                defaultRoute = if (result.data) {Navigation.MainConnectionScreen.route} else {Navigation.LandingScreen.route},
                                connect = connect,
                                connectionState = connectionState,
                                trafficState = trafficState
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun LoginApplication(
        defaultRoute: String,
        connect: ActivityResultLauncher<Void?>,
        connectionState: MutableState<BaseService.State>,
        trafficState: MutableState<TrafficStats>
    ) {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = defaultRoute,
            builder = {
                composable(
                    route = Navigation.LandingScreen.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                            animationSpec = tween(700)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    content = { Landing(navController = navController) },
                )
                composable(Navigation.WelcomeScreen.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                            animationSpec = tween(700)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    }, content = { Welcome(navController) })
                composable(
                    Navigation.EmailSignUpScreen.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                            animationSpec = tween(700)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    content = { EmailSignUpScreen(navController = navController) }
                )
                composable(
                    Navigation.PasswordScreen.route + "/{email}",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                            animationSpec = tween(700)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    content = { PasswordSignUp(navController = navController) }
                )
                composable(
                    Navigation.VerificationCodeScreen.route + "/{email}" + "/{password}",
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                            animationSpec = tween(700)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    content = { VerificationSignUp(navController) }
                )
                composable(
                    Navigation.MainConnectionScreen.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                            animationSpec = tween(700)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    content = {
                        MainConnectionScreen(
                            navControle = navController,
                            connect = connect,
                            state = connectionState,
                            trafficState = trafficState
                        )
                    }
                )
                composable(
                    Navigation.EmailSignInScreen.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                            animationSpec = tween(700)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    content = { EmailSignIn(navController = navController) }
                )
                composable(
                    Navigation.PurchasePlanScreen.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(700)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                            animationSpec = tween(700)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Companion.Right,
                            animationSpec = tween(700)
                        )
                    },
                    content = { PurchaseScreen(navController = navController) }
                )
            }
        )

    }

    fun ruleCreated() {
//        navigation.menu.findItem(R.id.nav_route).isChecked = true
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_holder, RouteFragment())
//            .commitAllowingStateLoss()
        if (DataStore.serviceState.started) {
            snackbar(getString(R.string.restart)).setAction(R.string.apply) {
                SagerNet.reloadService()
            }.show()
        }
    }

    private fun changeState(
        state: BaseService.State,
        msg: String? = null,
        animate: Boolean = false,
    ) {
        connectionState.value = state
        DataStore.serviceState = state

        if (!DataStore.serviceState.connected) {
            statsUpdated(emptyList())
        }

//        binding.fab.changeState(state, DataStore.serviceState, animate)
//        binding.stats.changeState(state)
        if (msg != null) {
            // snackbar(getString(R.string.vpn_error, msg)).show()
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

    override fun stateChanged(state: BaseService.State, profileName: String?, msg: String?) {
        changeState(state, msg, true)
    }

    override fun onServiceConnected(service: ISagerNetService) = changeState(
        try {
            BaseService.State.values()[service.state]
        } catch (_: RemoteException) {
            BaseService.State.Idle
        }
    )

    override fun onPreferenceDataStoreChanged(store: PreferenceDataStore, key: String) {
        when (key) {
            Key.SERVICE_MODE -> onBinderDied()
            Key.PROXY_APPS, Key.BYPASS_MODE, Key.INDIVIDUAL -> {
                if (DataStore.serviceState.canStop) {
//                    snackbar(getString(R.string.restart)).setAction(R.string.apply) {
//                        SagerNet.reloadService()
//                    }.show()
                }
            }
        }
    }

    override fun onServiceDisconnected() = changeState(BaseService.State.Idle)
    override fun onBinderDied() {
        connection.disconnect(this)
        connection.connect(this, this)
    }

    fun snackbar(@StringRes resId: Int): Snackbar = snackbar("").setText(resId)
    fun snackbar(text: CharSequence): Snackbar = snackbarInternal(text).apply {
        view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).apply {
            maxLines = 10
        }
    }

    private suspend fun finishImportSubscription(subscription: ProxyGroup) {
        GroupManager.createGroup(subscription)
        GroupUpdater.startUpdate(subscription, true)
    }

    internal open fun snackbarInternal(text: CharSequence): Snackbar = throw NotImplementedError()

    fun urlTest(): Int {
        if (!DataStore.serviceState.connected || connection.service == null) {
            error("not started")
        }
        return connection.service!!.urlTest()
    }

    private fun createDatFile() {

        val fileName = "geoip.dat" // Your desired .dat file name
        val inputStream: InputStream = resources.openRawResource(R.raw.geoip)
        val directoryPAth = "/storage/emulated/0/Android/data/com.abrnoc.application"
        val directory = File(directoryPAth, "files")
        directory.mkdirs()
        val file = File(directory, fileName)

        try {
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        connection.bandwidthTimeout = 1000
    }

    override fun onStop() {
        connection.bandwidthTimeout = 0
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        GroupManager.userInterface = null
        DataStore.configurationStore.unregisterChangeListener(this)
        connection.disconnect(this)
    }

    override fun trafficUpdated(profileId: Long, stats: TrafficStats, isCurrent: Boolean) {
        if (profileId == 0L) return

        if (isCurrent) {
            trafficState.value = stats
//            stats.txRateProxy, stats.rxRateProxy
        }

        runOnDefaultDispatcher {
            ProfileManager.postTrafficUpdated(profileId, stats)
        }
    }

    override fun statsUpdated(stats: List<AppStats>) {
//        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_holder)
//        if (fragment is TrafficFragment) {
//            fragment.emitStats(stats)
//        }
//        (supportFragmentManager.findFragmentById(R.id.fragment_holder) as? TrafficFragment)?.emitStats(
//            stats
//        )
    }

    override fun missingPlugin(profileName: String, pluginName: String) {
        val pluginEntity = PluginEntry.find(pluginName)

        // unknown exe or neko plugin
        if (pluginEntity == null) {
            snackbar(getString(R.string.plugin_unknown, pluginName)).show()
            return
        }
    }
}
