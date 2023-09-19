package com.abrnoc.application.presentation.mainConnection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.abrnoc.application.R
import com.abrnoc.application.presentation.components.VpnConnectButton
import com.abrnoc.application.presentation.mainConnection.components.BottomArcShape
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme

@Composable
fun MainConnectionScreen2() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ApplicationTheme.colors.uiBackground)
            .verticalScroll(rememberScrollState())

    ) {

        Backdrop(modifier = Modifier, onClick = {})
        val iconSize = 150.dp
        Spacer(modifier = Modifier)
//        LazyColumn(modifier = Modifier.constrainAs(locations) {
//            top.linkTo(title.bottom, 8.dp)
//            linkTo(startGuideline, endGuideline)
//        }) {
//
//        }

    }
}

@Composable
fun Backdrop(modifier: Modifier,  onClick: () -> Unit) {
    Card(
        elevation = 16.dp,
        shape = BottomArcShape(arcHeight = (120.dp).dpToPx()),
        modifier = modifier
            .height(480.dp)
            .background(
                color = Color.Transparent
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.map_backdrop),
            contentDescription = "backdrop photo of a world map",
//            contentScale = ContentScale.Crop,
            modifier = modifier
                .fillMaxWidth()
                .background(brush = Brush.verticalGradient(ApplicationTheme.colors.welcomeGradiant))

        )
        Box(
            modifier = Modifier
                .background(color = Color.Transparent)

                .wrapContentSize()
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            VpnConnectButton(onClick = onClick)
        }
    }
}

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Preview
@Composable
private fun Preview() {
    AbrnocApplicationTheme {
        MainConnectionScreen2()
    }
}