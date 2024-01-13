package com.narcis.application.presentation.screens.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.nekohasekai.sagernet.R
import com.narcis.application.presentation.navigation.Navigation
import com.narcis.application.presentation.ui.theme.narcisApplicationTheme
import com.narcis.application.presentation.ui.theme.ApplicationTheme
import com.narcis.application.presentation.ui.theme.Ocean11

@Composable
fun Landing(navController: NavController?) {
    val brush = Brush.verticalGradient(ApplicationTheme.colors.welcomeGradiant)
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .background(brush = brush)
            .paint(
                painter = painterResource(id = R.drawable.halow_icon),
                contentScale = ContentScale.Crop
            )
            .scrollable(scrollState, orientation = Orientation.Vertical)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.5f))
        Image(painter = painterResource(id = R.drawable.map_pins), contentDescription = "map image background")
        Spacer(modifier = Modifier.height(32.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Welcome To Ving VPN!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                maxLines = 1,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Experience secure browsing, unlock geo-restricted content, and stay anonymous across the virtual world. Upgrade to our Premium plan for even more global accessibility and enhanced features.",
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
                onClick = { navController?.navigate(Navigation.WelcomeScreen.route) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        text = "Connect Now",
                        color = Ocean11,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    narcisApplicationTheme {
        Landing(null)
    }
}
