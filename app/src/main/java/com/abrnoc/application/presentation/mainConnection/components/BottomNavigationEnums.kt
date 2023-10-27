package com.abrnoc.application.presentation.mainConnection.components

import androidx.annotation.DrawableRes
import io.nekohasekai.sagernet.R
import com.abrnoc.application.presentation.mainConnection.navigation.MainNavigation

enum class BottomNavigationItem(val route: MainNavigation, @DrawableRes val icon: Int, val title: String) {

    Home(
        route = MainNavigation.HomeScreen,
        icon = R.drawable.home_icon,
        title = "Home"
    ),
    Help(
        route = MainNavigation.HelpScreen,
        icon = R.drawable.help_icon,
        title = "Help"
    ),
    Profile(
        route = MainNavigation.ProfileScreen,
        icon = R.drawable.profile_icon,
        title = "Profile"
    )

}