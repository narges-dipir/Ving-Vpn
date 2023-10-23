package com.abrnoc.application.presentation.screens.signUp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.abrnoc.application.R
import com.abrnoc.application.presentation.components.ButtonGradient
import com.abrnoc.application.presentation.navigation.Navigation
import com.abrnoc.application.presentation.screens.landing.AnimatedLogo
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import com.abrnoc.application.presentation.ui.theme.Blue0
import com.abrnoc.application.presentation.ui.theme.Blue1
import com.abrnoc.application.presentation.ui.theme.Neutral2
import com.abrnoc.application.presentation.ui.theme.Neutral3
import com.abrnoc.application.presentation.ui.theme.Purple40
import com.abrnoc.application.presentation.ui.theme.Sky0
import com.abrnoc.application.presentation.ui.theme.Sky1
import com.abrnoc.application.presentation.viewModel.AuthenticationViewModel
import com.abrnoc.application.presentation.viewModel.event.SendCodeEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EmailSignUpScreen(
    navController: NavController?,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {
    var text by rememberSaveable {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()
    var loadingVisibility by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    LaunchedEffect(key1 = state) {
        if (state.isValid) {
            loadingVisibility = false
            if (loadingVisibility) {
            } else {
                if (state.isAlreadyRegistered) {
                    navController?.navigate(Navigation.EmailSignInScreen.route)
                    viewModel.onEvent(SendCodeEvent.ClearEvent)
                } else {
                    navController?.navigate(Navigation.PasswordScreen.route + "/${text}")
                    viewModel.onEvent(SendCodeEvent.ClearEvent)
                }
            }
        } else {
            loadingVisibility = false
            scope.launch {
                if (state.message.isNotBlank()) {
                    snackBarHostState.showSnackbar(state.message)
                }
            }
        }
    }



    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ApplicationTheme.colors.uiBackground,
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
//            .background(color = ApplicationTheme.colors.uiBackground)
            .padding(it),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(modifier = Modifier.height(32.dp))
        AnimatedLogo(
            modifier = Modifier
                .fillMaxWidth(.3f)
                .padding(bottom = 8.dp),
            colors = listOf(Sky1, Sky0)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Create Your Account",
            color = ApplicationTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            maxLines = 1,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = "create your free account now and experience the true potential of VPN",
            color = ApplicationTheme.colors.textSecondry,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            fontSize = 16.sp
        )
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                },
                shape = RoundedCornerShape(30.dp),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.mail_icon),
                        contentDescription = "mail icon",
                        tint = Neutral2
                    )
                },
                singleLine = true,
                label = {
                    Text(
                        text = "Email",
                        color = ApplicationTheme.colors.textSecondry,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                placeholder = { Text(text = "Enter your email address") },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = ApplicationTheme.colors.textPrimary,
                    unfocusedLabelColor = Neutral3,
                    placeholderColor = Color.White,
                    focusedBorderColor = Neutral2,
                    unfocusedBorderColor = Neutral2

                ),
                modifier = Modifier.fillMaxWidth(0.9f),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() },
                    onNext = { keyboardController?.hide()
                        loadingVisibility = true
                        viewModel.onEvent(SendCodeEvent.EmailQuery(text.trim()))
                             }
                )

            )

            Spacer(modifier = Modifier.padding(10.dp))

            ButtonGradient(
                gradientColors = listOf(Blue0, Blue1),
                cornerRadius = 30.dp,
                nameButton = "Next",
                roundedCornerShape = RoundedCornerShape(30.dp)
            ) {
                loadingVisibility = true
                viewModel.onEvent(SendCodeEvent.EmailQuery(text.trim()))
            }
            if (loadingVisibility) {
                CircularProgressIndicator(color = Purple40)
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Text(text = "OR", letterSpacing = 1.sp, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.padding(10.dp))
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClick = {
                },
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 12.dp,
                    end = 20.dp,
                    bottom = 12.dp
                ),
                border = BorderStroke(2.dp, Color.LightGray),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(backgroundColor = ApplicationTheme.colors.uiBackground)
            ) {
                Box(Modifier.fillMaxWidth()) {
                    Image(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .clip(CircleShape),
                        painter = painterResource(id = R.drawable.google_icon),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        text = "Continue with Google",
                        color = ApplicationTheme.colors.textSecondry
                    )
                }
            }

        }

        }

    }
}


@Composable
@Preview
private fun Preview() {
    AbrnocApplicationTheme {
        EmailSignUpScreen(null)
    }
}