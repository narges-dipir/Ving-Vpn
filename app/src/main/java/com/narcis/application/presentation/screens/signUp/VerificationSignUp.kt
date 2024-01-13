package com.narcis.application.presentation.screens.signUp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.narcis.application.presentation.components.ButtonGradient
import com.narcis.application.presentation.components.SmsCodeView
import com.narcis.application.presentation.navigation.Navigation
import com.narcis.application.presentation.screens.landing.AnimatedLogo
import com.narcis.application.presentation.ui.theme.narcisApplicationTheme
import com.narcis.application.presentation.ui.theme.ApplicationTheme
import com.narcis.application.presentation.ui.theme.Blue0
import com.narcis.application.presentation.ui.theme.Blue1
import com.narcis.application.presentation.ui.theme.Neutral3
import com.narcis.application.presentation.ui.theme.Neutral7
import com.narcis.application.presentation.ui.theme.Sky0
import com.narcis.application.presentation.ui.theme.Sky1
import com.narcis.application.presentation.utiles.longToast
import com.narcis.application.presentation.viewModel.VerificationCodeViewModel
import com.narcis.application.presentation.viewModel.event.SendVerificationEvent
import kotlinx.coroutines.delay

@Composable
fun VerificationSignUp(
    navController: NavController?,
    verificationCodeViewModel: VerificationCodeViewModel? = hiltViewModel()
) {
    val context = LocalContext.current
    val otpValue = remember { mutableStateOf("") }
    var resend by remember {
        mutableStateOf(false)
    }
    var timer by remember { mutableStateOf(120) }
    LaunchedEffect(key1 = timer) {
        if (timer > 0) {
            delay(1_000)
            timer -= 1
            resend = false
        } else {
            resend = true
        }
    }

    var smsCodeNumber by remember {
        mutableStateOf("")
    }
    var isNextBtnStatus by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = ApplicationTheme.colors.uiBackground),
        horizontalAlignment = Alignment.CenterHorizontally,

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
            text = "Verification Code",
            color = ApplicationTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            maxLines = 1,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Enter the verification code sent to ${verificationCodeViewModel?.email ?: "email" } to ensure secure account access.",
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
//            val otpValue = remember { mutableStateOf("") }
            Spacer(modifier = Modifier.height(32.dp))

            SmsCodeView(
                smsCodeLength = 6,
                textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Neutral7,
                    unfocusedLabelColor = Neutral3,
                    placeholderColor = Color.White,
                    focusedBorderColor = Blue0,
                    unfocusedBorderColor = Blue0,
                    backgroundColor = Sky0
                ),
                textStyle = MaterialTheme.typography.h6,
                smsFulled = {
                    smsCodeNumber = it
                    isNextBtnStatus = it.length == 4
                }
            )
//            PinInput(
//                cellModifier = Modifier.border(
//                    BorderStroke(2.dp, Neutral2),
//                    shape = RoundedCornerShape(10.dp)
//                ),
//                value = otpValue.value,
//                obscureText = null,
//                length = 6,
//                disableKeypad = false // Optional
//            ) {
//                otpValue.value = it
//            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Code is 6 digits without space",
                color = ApplicationTheme.colors.textSecondry,
                modifier = Modifier.padding(start = 32.dp),
                textAlign = TextAlign.Right,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.padding(10.dp))
            ButtonGradient(
                gradientColors = listOf(Blue0, Blue1),
                cornerRadius = 30.dp,
                nameButton = "Verify",
                roundedCornerShape = RoundedCornerShape(30.dp)
            ) {
                if (smsCodeNumber.length == 6) {
                    verificationCodeViewModel!!.onEvent(
                        SendVerificationEvent.SignInQuery(
                            email = verificationCodeViewModel.state.email,
                            password = verificationCodeViewModel.state.password,
                            code = smsCodeNumber
                        )
                    )
                    if (verificationCodeViewModel.state.isSuccessful) {
//                        val intent = Intent(context, ConnActivity::class.java)
//                        context.startActivity(intent)
                        navController?.navigate(Navigation.MainConnectionScreen.route)
                    } else {
                        if (verificationCodeViewModel.state.error.isNotBlank()) {
                            longToast(
                                context,
                                verificationCodeViewModel.state.error
                            )
                        } else {
                            longToast(
                                context,
                                "Oops! Try again.."
                            )
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "Resend Code in ${timer / 60} : ${timer % 60}",
                color = Blue1,
                modifier = Modifier
                    .padding(start = 32.dp)
                    .clickable {
                        if (resend) {
                            verificationCodeViewModel?.onEvent(
                                SendVerificationEvent.EmailQuery
                            )
                            timer = 120
                        } else {
                            longToast(
                                context,
                                "Wait for the count down"
                            )
                        }
                    },
                textAlign = TextAlign.Right,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    narcisApplicationTheme {
        VerificationSignUp(null, null)
    }
}