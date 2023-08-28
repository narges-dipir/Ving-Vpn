package com.abrnoc.application.presentation.screens.signUp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abrnoc.application.presentation.components.ButtonGradient
import com.abrnoc.application.presentation.components.CustomOtpContainer
import com.abrnoc.application.presentation.screens.landing.AnimatedLogo
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import com.abrnoc.application.presentation.ui.theme.Blue0
import com.abrnoc.application.presentation.ui.theme.Blue1
import com.abrnoc.application.presentation.ui.theme.Sky0
import com.abrnoc.application.presentation.ui.theme.Sky1

@Composable
fun VerificationSignUp() {
    val columnState = rememberScrollState()
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
            text = "Enter the verification code sent to user@mail.com to ensure secure account access.",
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
            val otpValue = remember { mutableStateOf("") }
            Spacer(modifier = Modifier.height(32.dp))
            CustomOtpContainer(value = otpValue.value, onValueChange = {
                otpValue.value = it
            }
            )
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

            }
            Spacer(modifier = Modifier.padding(10.dp))
            Text(
                text = "Resend Code in 01:32",
                color = Blue1,
                modifier = Modifier.padding(start = 32.dp),
                textAlign = TextAlign.Right,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    AbrnocApplicationTheme {
        VerificationSignUp()
    }
}