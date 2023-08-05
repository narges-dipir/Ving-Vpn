package com.abrnoc.application.bottomNavigation

import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.util.lerp
import com.abrnoc.application.ui.UiConstants.BottomNavLabelTransformOrigin
import com.abrnoc.application.ui.UiConstants.TextIconSpacing

@Composable
fun BottomNavigationItem(
    @DrawableRes icon: Int,
    text: String,
    selected: Boolean,
    onSelected: () -> Unit,
    animSpec: AnimationSpec<Float>,
    modifier: Modifier = Modifier,
) {
    val animationProgress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animSpec,
        label = "",
    )
    BottomNavigationItemLayout(
        icon = icon,
        text = text,
        animationProgress = animationProgress,
        selected = selected,
        modifier = Modifier.selectable(selected = selected, onClick = onSelected).wrapContentSize()
    )
}

@Composable
fun BottomNavigationItemLayout(
    @DrawableRes icon: Int,
    text: String,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float,
    modifier: Modifier = Modifier,
    selected: Boolean,
) {
    Box() {
        Column {
            Box(
                modifier = Modifier.layoutId("icon")
                    .padding(horizontal = TextIconSpacing),
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = "bottom navigation icon",
                )
            }
            val scale = lerp(0.6f, 1f, animationProgress)
            Box(
                modifier = Modifier.layoutId("text")
                    .padding(horizontal = TextIconSpacing)
                    .graphicsLayer {
                        alpha = animationProgress
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = BottomNavLabelTransformOrigin
                    },
            ) {
                Text(text = text)
            }
        }
//
    }
}
