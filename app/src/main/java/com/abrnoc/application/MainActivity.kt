package com.abrnoc.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abrnoc.application.presentation.navigation.Navigation
import com.abrnoc.application.presentation.screens.landing.Landing
import com.abrnoc.application.presentation.screens.landing.Welcome
import com.abrnoc.application.presentation.screens.main.MainConnection
import com.abrnoc.application.presentation.screens.signUp.EmailSignIn
import com.abrnoc.application.presentation.screens.signUp.EmailSignUp
import com.abrnoc.application.presentation.screens.signUp.PasswordSignUp
import com.abrnoc.application.presentation.screens.signUp.VerificationSignUp
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AbrnocApplicationTheme {
                LoginApplication()
            }
        }
    }

    @Composable
    fun LoginApplication() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = Navigation.LandingScreen.route,
            builder = {
                composable(Navigation.LandingScreen.route, content = { Landing(navController = navController) })
                composable(Navigation.WelcomeScreen.route, content = { Welcome(navController) })
                composable(Navigation.EmailSignUpScreen.route, content = { EmailSignUp(navController = navController)})
                composable(Navigation.PasswordScreen.route, content = { PasswordSignUp(navController = navController)})
                composable(Navigation.VerificationCodeScreen.route, content = {VerificationSignUp(navController)})
                composable(Navigation.MainConnectionScreen.route, content = { MainConnection(navController = navController)})
                composable(Navigation.EmailSignInScreen.route, content = {EmailSignIn(navController = navController)})
            })
    }
}
