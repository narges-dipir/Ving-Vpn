package com.abrnoc.application.presentation.screens.signUp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.abrnoc.application.presentation.ui.theme.Lavender8
import com.abrnoc.application.presentation.ui.theme.Sky0
import com.abrnoc.application.presentation.ui.theme.Sky1
import com.abrnoc.application.presentation.ui.theme.Violate0
import com.abrnoc.application.presentation.utiles.longToast
import com.abrnoc.application.presentation.viewModel.AuthenticationViewModel
import com.abrnoc.application.presentation.viewModel.event.SendCodeEvent

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EmailSignUp(
    navController: NavController?,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var text by rememberSaveable {
        mutableStateOf("")
    }
    var showDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = ApplicationTheme.colors.uiBackground),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
//        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
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
                        contentDescription = "mail icon"
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
                    focusedBorderColor = Lavender8,
                    unfocusedLabelColor = Violate0,
                    textColor = ApplicationTheme.colors.textPrimary
                ),
                modifier = Modifier.fillMaxWidth(0.9f),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                )

            )

            Spacer(modifier = Modifier.padding(10.dp))
            ButtonGradient(
                gradientColors = listOf(Blue0, Blue1),
                cornerRadius = 30.dp,
                nameButton = "Next",
                roundedCornerShape = RoundedCornerShape(30.dp)
            ) {
                viewModel.onEvent(SendCodeEvent.EmailQuery(text))

                if (viewModel.state.isValid) {
                    if (!viewModel.state.isValid) {
                        longToast(context, " The email Is Not Valid, Try Again!")
                    } else if (viewModel.state.isAlreadyRegistered) {
                        navController?.navigate(Navigation.EmailSignInScreen.route)
                    } else if (!viewModel.state.isAlreadyRegistered and viewModel.state.isValid) {
                        longToast(context, "The Verification Code Has Been Send To Your Email")
                        navController?.navigate(Navigation.PasswordScreen.route + "/${text}")
                    } else {
                        longToast(context, viewModel.state.message)
                    }
                }
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

//        }

    }
}


@Composable
@Preview
private fun Preview() {
    AbrnocApplicationTheme {
        EmailSignUp(null)
    }
}