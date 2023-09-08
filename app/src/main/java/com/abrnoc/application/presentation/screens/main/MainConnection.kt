package com.abrnoc.application.presentation.screens.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abrnoc.application.presentation.components.CircularProgress
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.ui.theme.Blue1
import com.abrnoc.application.presentation.ui.theme.Neutral2

@Composable
fun MainConnection(navController: NavController?) {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        val progress by animateFloatAsState(targetValue = 90F, label = "")
        CircularProgress(
            modifier = Modifier.size(120.dp),
            progress = progress,
            progressMAx = 100f,
            progressBarColor = Blue1,
            progressBarWidth = 30.dp,
            backgroundProgressBarColor = Neutral2,
            backgroundProgressBarWidth = 15.dp,
            roundBorder = true,
            startAngle = progress / 2
        )
    }
}

@Preview
@Composable
private fun Preview() {
    AbrnocApplicationTheme {
        MainConnection(null)
    }
}