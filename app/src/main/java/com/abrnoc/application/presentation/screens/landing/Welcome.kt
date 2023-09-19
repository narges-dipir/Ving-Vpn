package com.abrnoc.application.presentation.screens.landing

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.abrnoc.application.R
import com.abrnoc.application.presentation.navigation.Navigation
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import com.abrnoc.application.presentation.ui.theme.Ocean11
import com.abrnoc.application.presentation.ui.theme.Shadow0
import com.abrnoc.application.presentation.ui.theme.Shadow2

@Composable
fun Welcome(navController: NavController?) {
    val brush = Brush.verticalGradient(ApplicationTheme.colors.welcomeGradiant)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = brush)
            .paint(
                painter = painterResource(id = R.drawable.halow_icon),
                contentScale = ContentScale.Crop
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.8f))
        AnimatedLogo(
            modifier = Modifier
                .fillMaxWidth(.3f)
                .padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Welcome To keepitOn VPN!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                maxLines = 1,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Your online security and freedom are our priority. Enjoy private, unrestricted browsing with confidence.",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(64.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 12.dp,
                    end = 20.dp,
                    bottom = 12.dp
                ),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                onClick = { navController?.navigate(Navigation.EmailSignUpScreen.route) }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = "Lets Get Started", color = Ocean11)
                }
            }
        }


    }

}

@Composable
fun AnimatedLogo(modifier: Modifier = Modifier, colors: List<Color> = listOf(Shadow2, Shadow0)) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val brush = Brush.horizontalGradient(colors = colors)

    val animatedLogoScale by animateFloatAsState(
        targetValue = 2.5f,
        animationSpec = repeatable(
            iterations = 4,
            animation = tween(durationMillis = 200),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Box(
        modifier = Modifier
            .padding(16.dp)
            .size(120.dp)
            .background(
                brush = brush, alpha = 0.8f,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
//            .paint(painterResource(id = R.drawable.main_icon))
    ) {
        Image(
            painter = painterResource(id = R.drawable.main_icon),
            contentDescription = "main icon",
            modifier = modifier
                .graphicsLayer(
                    scaleX = animatedLogoScale,
                    scaleY = animatedLogoScale
                ),

        )
    }


}

@Preview
@Composable
private fun Preview() {
    AbrnocApplicationTheme {
        Welcome(null)
    }
}