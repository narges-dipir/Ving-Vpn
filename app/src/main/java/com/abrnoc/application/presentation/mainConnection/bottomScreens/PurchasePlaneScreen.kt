package com.abrnoc.application.presentation.mainConnection.bottomScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.abrnoc.application.presentation.navigation.Navigation
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import com.abrnoc.application.presentation.ui.theme.Ocean11

@Composable
fun PurchaseScreen(navController: NavController?) {
    val brush = Brush.verticalGradient(ApplicationTheme.colors.welcomeGradiant)
    val data = listOf(
        ShoppingItem(
            plan = "1-Month Plan",
            pricePerMonth = "3.99",
            off = "0",
            save = "",
            isBestDeal = false
        ),
        ShoppingItem(
            plan = "1-Year Plan",
            pricePerMonth = "2.99",
            off = "40",
            save = "120",
            isBestDeal = false
        ),
        ShoppingItem(
            plan = "2-Year Plan",
            pricePerMonth = "1.99",
            off = "50",
            save = "180",
            isBestDeal = true
        )
    )
    Column(
        modifier = Modifier
            .background(brush = brush)
            .paint(
                painter = painterResource(id = R.drawable.halow_icon),
                contentScale = ContentScale.Crop
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text(
            text = "Premium Plan",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            maxLines = 1,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,) {
            FeatureTikText(text = "Secure")
            FeatureTikText(text = "High-speed VPN")
            FeatureTikText(text = "Customer Support")
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn() {
            items(data.size) {i ->
                val item = data[i]
                ShoppingItem(onClick = { /*TODO*/ }, item = item)

            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 32.dp),
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
                    text = "Get A Plane",
                    color = Ocean11,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}

@Composable
fun FeatureTikText(text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Image(painter = painterResource(id = R.drawable.tike_icon),
            contentDescription = "feature $text",
            modifier = Modifier.padding(4.dp))
        Text(text = text,
            color = Color.White,
            maxLines = 1,
            textAlign = TextAlign.Center
            )
    }
}

@Preview
@Composable
private fun Preview() {
    AbrnocApplicationTheme {
        PurchaseScreen(null)
    }
}