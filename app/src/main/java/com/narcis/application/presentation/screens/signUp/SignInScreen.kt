package com.narcis.application.presentation.screens.signUp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.nekohasekai.sagernet.R
import com.narcis.application.presentation.components.ButtonGradient
import com.narcis.application.presentation.navigation.Navigation
import com.narcis.application.presentation.screens.landing.AnimatedLogo
import com.narcis.application.presentation.ui.theme.narcisApplicationTheme
import com.narcis.application.presentation.ui.theme.ApplicationTheme
import com.narcis.application.presentation.ui.theme.Blue0
import com.narcis.application.presentation.ui.theme.Blue1
import com.narcis.application.presentation.ui.theme.Neutral2
import com.narcis.application.presentation.ui.theme.Neutral3
import com.narcis.application.presentation.ui.theme.Purple40
import com.narcis.application.presentation.ui.theme.Sky0
import com.narcis.application.presentation.ui.theme.Sky1
import com.narcis.application.presentation.utiles.Visibility
import com.narcis.application.presentation.utiles.VisibilityOff
import com.narcis.application.presentation.utiles.longToast
import com.narcis.application.presentation.viewModel.SignInViewModel
import com.narcis.application.presentation.viewModel.event.SignInEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailSignIn(
    navController: NavController?,
    viewModel: SignInViewModel = hiltViewModel()
) {
    var text by rememberSaveable {
        mutableStateOf("")
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    var password by rememberSaveable {
        mutableStateOf("")
    }
    var passwordHidden by rememberSaveable {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    var loadingVisibility by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()
    LaunchedEffect(key1 = state) {
        if (state.isRequestSend) {
            if (state.isLoading) {
                loadingVisibility = true
            } else if (state.isSuccessful) {
                loadingVisibility = false
                navController?.navigate(Navigation.MainConnectionScreen.route)
            } else if (state.error.isNotEmpty()) {
                loadingVisibility = false
                longToast(context, "Oops, Try Again!")
                navController?.navigate(Navigation.MainConnectionScreen.route) // add this to navigate without server
            } else {
                loadingVisibility = false
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = ApplicationTheme.colors.uiBackground),
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
            text = "Welcome Back",
            color = ApplicationTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            maxLines = 1,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Please sign in to access your account",
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
            OutlinedTextField(value = text,
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
                    onDone = { keyboardController?.hide() }
                )

            )

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                shape = RoundedCornerShape(30.dp),
                label = {
                    Text(
                        "Password",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium,
                    )
                },
                visualTransformation =
                if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                //  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = ApplicationTheme.colors.textPrimary,
                    unfocusedLabelColor = Neutral3,
                    placeholderColor = Color.White,
                    focusedBorderColor = Neutral2,
                    unfocusedBorderColor = Neutral2
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        val visibilityIcon =
                            if (passwordHidden) Visibility else VisibilityOff
                        // Please provide localized description for accessibility services
                        val description = if (passwordHidden) "Show password" else "Hide password"
                        Icon(imageVector = visibilityIcon, contentDescription = description)
                    }
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.key_icon),
                        contentDescription = "password icon",
                        tint = Neutral2
                    )
                },
                modifier = Modifier.fillMaxWidth(0.92f),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        // do something here
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
                viewModel.onEvent(
                    SignInEvent.SignInQuery(
                        email = text,
                        password = password
                    )
                )

            }
            if (loadingVisibility) {
                CircularProgressIndicator(color = Purple40)
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    narcisApplicationTheme {
        EmailSignIn(null)
    }
}