package com.narcis.application.presentation.bottomNavigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.narcis.application.presentation.navigation.Navigation
import io.nekohasekai.sagernet.R

enum class BottomBarDestination(
    val route: Navigation,
    @DrawableRes val unFilledIcon: Int,
    @DrawableRes val filledIcon: Int? = null,
    @StringRes val title: Int,
    @DrawableRes val darkModeIcon: Int? = null
) {

    Home(
        route = Navigation.MainConnectionScreen,
        unFilledIcon = R.drawable.home_icon,
        filledIcon = R.drawable.home_icon_filled,
        title = R.string.home,
        darkModeIcon = R.drawable.home_icon
    ),
    Help(
        route = Navigation.HelpScreen,
        unFilledIcon = R.drawable.help_icon,
        filledIcon = R.drawable.help_icon_filled,
        title = R.string.help
    ),
    Profile(
        route = Navigation.PurchasePlanScreen,
        unFilledIcon = R.drawable.profile_icon,
        filledIcon = R.drawable.profile_icon_filled,
        title = R.string.profile
    )

}