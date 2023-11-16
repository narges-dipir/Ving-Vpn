package com.abrnoc.application.presentation.bottomNavigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.abrnoc.application.presentation.navigation.AppNavHost
import com.abrnoc.application.presentation.navigation.Navigation
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import com.abrnoc.application.presentation.ui.theme.Blue1
import com.abrnoc.application.presentation.ui.theme.Violate0
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.systemuicontroller.SystemUiController
import io.nekohasekai.sagernet.aidl.TrafficStats
import io.nekohasekai.sagernet.bg.BaseService

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class, ExperimentalMaterialApi::class
)
@Composable
fun RootScreen(
    defaultRoute: String,
    connect: ActivityResultLauncher<Void?>,
    connectionState: MutableState<BaseService.State>,
    trafficState: MutableState<TrafficStats>
) {
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = false,
    )
    val bottomSheetNavigator = remember(modalSheetState) {
        BottomSheetNavigator(modalSheetState)
    }  //rememberBottomSheetNavigator()
    val navController = rememberNavController(bottomSheetNavigator)
    val currentBackStackEntryAsState by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntryAsState?.destination
    val context = LocalContext.current


    val isShowBottomBar = true
//        when (currentDestination?.route) {
//        Navigation.MainConnectionScreen.route, Navigation.PurchasePlanScreen.route, null -> true
//        else -> false
//    }
    val darkMode = isSystemInDarkTheme()
    if (currentDestination?.route == Navigation.MainConnectionScreen.route) {
        BackHandler {
            (context as? Activity)?.finish()
        }
    }

    // SetupSystemUi(rememberSystemUiController(), MaterialTheme.colorScheme.background)
    println("the default color is : ${MaterialTheme.colorScheme.background}")
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {

        },
        bottomBar = {
            if (defaultRoute != Navigation.LandingScreen.route) {
                NavigationBar(
                    containerColor = ApplicationTheme.colors.uiBackground
                ) {
                    BottomBarDestination.values().forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painterResource(id = item.unFilledIcon),
                                    contentDescription = stringResource(id = item.title)
                                )
                            },
                            label = { androidx.compose.material3.Text(stringResource(id = item.title)) },
                            selected = item.route == currentDestination,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Blue1,
                                indicatorColor = Violate0
                            ),
                            onClick = {
                                if (item.route != currentDestination) {
                                    println(" the rout is ${item.route.route}")
                                    //  navController.popAll()
                                    navController.navigate(item.route.route)
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        Surface(modifier = Modifier.padding(it)) {
            // Text(text = "hey hey")
            AppNavHost(
                defaultRoute = defaultRoute,
                connect = connect,
                connectionState = connectionState,
                trafficState = trafficState,
                navController = navController

            )
        }
    }
}

@Composable
fun SetupSystemUi(
    systemUiController: SystemUiController,
    systemBarColor: Color
) {
    SideEffect {
        systemUiController.setSystemBarsColor(color = systemBarColor)
    }
}