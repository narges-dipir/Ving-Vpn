package com.abrnoc.application.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abrnoc.application.presentation.components.CustomSurface
import com.abrnoc.application.presentation.extensions.fadeInDiagonalGradientBorder
import com.abrnoc.application.presentation.extensions.offsetGradientBackground
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import io.nekohasekai.sagernet.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Landing(
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {

        Landing()
}

@Composable
private fun Landing(
    modifier: Modifier = Modifier
) {
    val brush = Brush.linearGradient(
        colors = ApplicationTheme.colors.iconGradiant,
        start = Offset.Zero,
        end = Offset(x = 260f, y = 0f)
        )
    val gradiantBackground = Modifier.offsetGradientBackground(
        ApplicationTheme.colors.iconGradiant,
        2660f,
        0f
    )
    val shape = Modifier.fadeInDiagonalGradientBorder(
        showBorder = false,
        colors = ApplicationTheme.colors.iconGradiant,
        shape = MaterialTheme.shapes.large
    )
    CustomSurface(
        modifier = modifier.fillMaxSize(),

        ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.main_icon), contentDescription = "main icon",
                modifier = Modifier.padding(top = 120.dp),
                alignment = Alignment.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "keepitOn VPN",
                style = MaterialTheme.typography.h3,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                color = ApplicationTheme.colors.textPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "We have best server around the world with super high speed connection",
                style = MaterialTheme.typography.body1,
                color = ApplicationTheme.colors.textHelp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(120.dp))
            CustomSurface(
                shape = RoundedCornerShape(20.dp),
                elevation = 2.dp,
                modifier = Modifier.wrapContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .width(260.dp)
                        .height(48.dp)
                        .then(gradiantBackground)
                        .align(Alignment.CenterHorizontally)

                ) {
                    Text(
                        text = "Continue",
                        color = Color.White,
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(
                            start = 70.dp,
                            top = 8.dp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Text(
                text = "Skip For Now",
                color = ApplicationTheme.colors.textSecondry,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )

        }
    }
}

@Composable
@Preview
private fun Preview() {
    AbrnocApplicationTheme {
        Landing(onNavigateToRoute = {})
    }
}