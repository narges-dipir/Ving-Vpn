package com.abrnoc.application.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PizzaSliceView() {
    var sliceColor by remember { mutableStateOf(Color.Red) }

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f
        val sliceAngle = (2 * PI) / 8 // Calculate the angle for each slice
        val sliceGap = 0f // Adjust the gap between slices

        val path = Path()

        // Draw the slices
        for (i in 0 until 8) { // You can adjust the number of slices
            val startAngle = sliceAngle * i.toFloat()
            val endAngle = sliceAngle * (i + 1).toFloat()

            val startX = center.x +  radius * cos(startAngle)
            val startY = center.y + radius * sin(startAngle)

            val endX = center.x + radius * cos(endAngle)
            val endY = center.y + radius * sin(endAngle)

            path.moveTo(center.x, center.y)
            path.lineTo(startX.toFloat(), startY.toFloat())
            path.lineTo(endX.toFloat(), endY.toFloat())
            path.close()
        }

        // Draw the center circle
        drawCircle(
            color = sliceColor,
            center = center,
            radius = 30.dp.toPx()
        )

        // Create space between slices (Draw a smaller circle over the slices)
        drawCircle(
            color = Color.White, // Background color to create the gap
            center = center,
            radius = radius/2f - 4f,
        )

        // Draw the path (slices)
        drawPath(path, color = sliceColor)
    }
}

@Preview
@Composable
private fun preview() {
    AbrnocApplicationTheme {
        PizzaSliceView()
    }
}