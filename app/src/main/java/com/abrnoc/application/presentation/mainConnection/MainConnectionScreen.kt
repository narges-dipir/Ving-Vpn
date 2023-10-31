package com.abrnoc.application.presentation.mainConnection

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.abrnoc.application.presentation.mainConnection.components.BottomNavigationItem
import com.abrnoc.application.presentation.mainConnection.components.ConnectionItem
import com.abrnoc.application.presentation.mainConnection.navigation.MainNavigation
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import com.abrnoc.application.presentation.ui.theme.Blue1
import com.abrnoc.application.presentation.ui.theme.Neutral3
import com.abrnoc.application.presentation.ui.theme.Purple40
import com.abrnoc.application.presentation.viewModel.DefaultConfigViewModel
import com.abrnoc.application.presentation.viewModel.event.ProxyEvent
import com.abrnoc.application.presentation.viewModel.model.DefaultConfig
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.popAll
import dev.olshevski.navigation.reimagined.rememberNavController
import io.nekohasekai.sagernet.aidl.TrafficStats
import io.nekohasekai.sagernet.bg.BaseService


private var checked = false

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainConnectionScreen(
    navControle: NavController?,
    configViewModel: DefaultConfigViewModel = hiltViewModel(),
    connect: ActivityResultLauncher<Void?>,
    state: MutableState<BaseService.State>,
    trafficState: MutableState<TrafficStats>
) {
    val localConnect = connect
    val configState by configViewModel.configState.collectAsState()
    val navController = rememberNavController<MainNavigation>(
        startDestination = MainNavigation.HomeScreen
    )
    val currentProxy = remember {
        mutableStateOf(DefaultConfig())
    }
    NavBackHandler(navController)
    val context = LocalContext.current
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = configState.isRefreshing
    )
    var loadingVisibility by remember { mutableStateOf(false) }
    var isConfigFetched by remember { mutableStateOf(false) }
    var configs by remember { mutableStateOf(listOf(DefaultConfig())) }
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

    LaunchedEffect(key1 = configState) {
        if (configState.isLoading) {
            loadingVisibility = true
        } else if (configState.configs!!.isNotEmpty()) {
            loadingVisibility = false
            configs = configState.configs!!
            isConfigFetched = true
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
            Backdrop(
                modifier = Modifier,
                onClick = {
                    configViewModel.onClickConnect(localConnect, context = context)
                },
                context = context,
                state = state,
                currentProxy,
                trafficState = trafficState
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (loadingVisibility) {
                CircularProgressIndicator(color = Purple40)
            }
            if (isConfigFetched) {

                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = {
                        configViewModel.onEvent(ProxyEvent.triggerRefresh)
                    },
                ) {
                LazyColumn {
                    if (!loadingVisibility) {
                        items(configs.size) { i ->
                            val config = configs[i]
                            ConnectionItem(config, onClick = {
                                currentProxy.value = config
                                configViewModel.onEvent(
                                    ProxyEvent.ConfigEvent(
                                        defaultConfig = currentProxy.value,
                                        context
                                    )
                                )
                                //auto connect and disconnet
                                configViewModel.onClickConnect(localConnect, context)

                            })
                        }
                    }
                }
            }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AbrnocApplicationTheme {
//        MainConnectionScreen(null)
    }
}
