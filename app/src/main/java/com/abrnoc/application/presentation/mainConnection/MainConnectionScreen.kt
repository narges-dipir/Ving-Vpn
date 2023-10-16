package com.abrnoc.application.presentation.mainConnection

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.abrnoc.application.R
import com.abrnoc.application.presentation.connection.BaseService
import com.abrnoc.application.presentation.mainConnection.components.BottomNavigationItem
import com.abrnoc.application.presentation.mainConnection.components.ConnectionItem
import com.abrnoc.application.presentation.mainConnection.navigation.MainNavigation
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import com.abrnoc.application.presentation.ui.theme.Blue1
import com.abrnoc.application.presentation.ui.theme.Neutral3
import com.abrnoc.application.presentation.viewModel.DefaultConfigViewModel
import com.abrnoc.application.presentation.viewModel.event.ProxyEvent
import com.abrnoc.application.repository.model.DefaultConfig
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.popAll
import dev.olshevski.navigation.reimagined.rememberNavController


private var checked = false
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainConnectionScreen(
    navControle: NavController?,
    configViewModel: DefaultConfigViewModel = hiltViewModel(),
    connect: ActivityResultLauncher<Void?>,
    state: BaseService.State
) {
    val localConnect = connect
    val configState = configViewModel.configState
    val navController = rememberNavController<MainNavigation>(
        startDestination = MainNavigation.HomeScreen
    )
    val currentProxy = remember {
        mutableStateOf(DefaultConfig())
//
    }
    NavBackHandler(navController)
    val context = LocalContext.current
    val isBackStackEmpty by remember {
        derivedStateOf {
            navController.backstack.entries.size == 1
        }
    }
    val currentDestination by remember {
        derivedStateOf {
            navController.backstack.entries.first().destination
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ApplicationTheme.colors.uiBackground,
        bottomBar = {
            NavigationBar(
                containerColor = ApplicationTheme.colors.uiBackground
            ) {
                BottomNavigationItem.values().forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(id = item.icon),
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                        selected = item.route == currentDestination,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Blue1,
                            indicatorColor = Neutral3
                        ),
                        onClick = {
                            if (item.route != currentDestination) {
                                navController.popAll()
                                navController.navigate(item.route)
                            }
                        }
                    )
                }
            }
        }
    ) {

        Column(modifier = Modifier.padding(it)) {
            Backdrop(modifier = Modifier, onClick = {
                configViewModel.onClickConnect(localConnect)
            },
                context = context,
                state = state)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                configViewModel.getAllConfigs()
                items(configViewModel.configState.configs!!.size) { i ->
                    val config = configViewModel.configState.configs!![i]
                    ConnectionItem(config, onClick = {
                        currentProxy.value = config
                        configViewModel.onEvent(
                            ProxyEvent.ConfigEvent(
                                defaultConfig = currentProxy.value,
                                context
                            )
                        )
                    })
                }
            }
        }
    }
}

fun changeState(
    state: BaseService.State,
    previousState: BaseService.State,
    animate: Boolean,
    context: Context,
) {
//    when (state) {
//        BaseService.State.Connecting -> println(" $$$$ its connecting ") // / changeState(iconConnecting, animate)
//        BaseService.State.Connected -> println(" $$$$ its connected ") // /changeState(iconConnected, animate)
//        BaseService.State.Stopping -> println(" $$$$ its Stopped ...  ")
//        else -> println(" $$$$ its confused ")
//    }
    checked = state == BaseService.State.Connected
    val description = context.getText(if (state.canStop) R.string.stop else R.string.connect)
//    contentDescription = description
//    TooltipCompat.setTooltipText(this, description)
    val enabled = state.canStop || state == BaseService.State.Stopped
//    isEnabled = enabled
//    if (Build.VERSION.SDK_INT >= 24) pointerIcon = PointerIcon.getSystemIcon(
//        context,
//        if (enabled) PointerIcon.TYPE_HAND else PointerIcon.TYPE_WAIT
//    )
}
@Preview
@Composable
private fun Preview() {
    AbrnocApplicationTheme {
//        MainConnectionScreen(null)
    }
}
