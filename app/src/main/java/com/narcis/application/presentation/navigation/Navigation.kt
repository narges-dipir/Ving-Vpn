package com.narcis.application.presentation.navigation

sealed class Navigation(val route: String) {
    object LandingScreen: Navigation("main_landing_screen")
    object WelcomeScreen: Navigation("main_welcome_screen")
    object EmailSignUpScreen: Navigation("email_signup_screen")
    object EmailSignInScreen: Navigation("email_signIn_screen")
    object PasswordScreen: Navigation("password_screen")
    object VerificationCodeScreen: Navigation("verification_code_screen")
    object MainConnectionScreen: Navigation("main_connection_screen")
    object PurchasePlanScreen: Navigation("purchase_plan_screen")

    object HelpScreen: Navigation("help_screen")
}