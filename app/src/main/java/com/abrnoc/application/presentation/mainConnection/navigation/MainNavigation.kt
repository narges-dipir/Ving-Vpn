package com.abrnoc.application.presentation.mainConnection.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class MainNavigation : Parcelable {
    @Parcelize
    object HomeScreen : MainNavigation()

    @Parcelize
    object HelpScreen : MainNavigation()

    @Parcelize
    object ProfileScreen : MainNavigation()
}
