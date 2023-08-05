package com.abrnoc.application.bottomNavigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.abrnoc.application.R

enum class HomeSection(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val iconSelected: Int,
    val route: String,
) {
    HOME(
        R.string.home,
        icon = R.drawable.home_icon,
        iconSelected = R.drawable.home_icon_selected,
        route = "home",
        ),
    CONFIG_CUSTOM(
        R.string.custom_config,
        R.drawable.config_icon,
        R.drawable.config_icon_selected,
        route = "configCustom"
    ),
    PROFILE(
        R.string.profile,
        R.drawable.profile_icon,
        R.drawable.profile_icon_selected,
        route = "profile"
    )
}
