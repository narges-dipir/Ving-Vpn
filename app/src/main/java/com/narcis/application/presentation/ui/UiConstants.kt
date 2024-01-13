package com.narcis.application.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.dp

object UiConstants {
    val TextIconSpacing = 2.dp
    val BottomNavLabelTransformOrigin = TransformOrigin(0f, 0.5f)
    val BottomNavHeight = 60.dp
    val BottomNavIndicatorShape = RoundedCornerShape(percent = 50)
    val BottomNavigationItemPadding = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
}