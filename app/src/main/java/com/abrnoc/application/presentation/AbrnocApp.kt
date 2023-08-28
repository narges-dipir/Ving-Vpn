package com.abrnoc.application.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.navigation
import com.abrnoc.application.presentation.navigation.MainDestinations
import com.abrnoc.application.presentation.navigation.rememberMainNavController
import com.abrnoc.application.presentation.bottomNavigation.HomeSection
import com.abrnoc.application.presentation.bottomNavigation.addHomeGraph
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme

@Composable
fun AbrnocApp() {
    AbrnocApplicationTheme {
        val mainNavController = rememberMainNavController()
        NavHost(
            navController = mainNavController.navController,
            startDestination = MainDestinations.HOME_ROUTE
        ) {
            mainNavGraph(
                upPress = mainNavController::upPress,
                onNavigateToRoute = mainNavController::navigateToBottomBarRoute
            )
        }
    }
}

private fun NavGraphBuilder.mainNavGraph(
    upPress: () -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
    navigation(
        route = MainDestinations.HOME_ROUTE,
        startDestination = HomeSection.LANDING.route
    ) {
        addHomeGraph(onNavigateToRoute)
    }
}