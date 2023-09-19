package com.abrnoc.application.presentation.mainConnection.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.abrnoc.application.R
import com.abrnoc.application.presentation.ui.theme.Neutral1
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DownloadButton(
    onClick: () -> Unit,
    strokeColor: Color,
    strokeSize: Dp,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()

    var isAnimating by remember { mutableStateOf(false) }
    val tapAnimation = remember { Animatable(1f) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(isAnimating) {
                detectTapGestures(
                    onPress = {
                        scope.launch {
                            tapAnimation.animateTo(0.8f)
                        }
                        tryAwaitRelease()
                        scope.launch {
                            tapAnimation.animateTo(1f)
                        }
                    },
                    onTap = {
                        isAnimating = !isAnimating
                        onClick()
                    },
                )
            }
            .graphicsLayer {
                alpha = tapAnimation.value
                scaleX = tapAnimation.value
                scaleY = tapAnimation.value
            },
    ) {
        RoundCircularProgressIndicator(
            progress = 1f,
            strokeWidth = strokeSize * 8,
            color = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        )
        RoundCircularProgressIndicator(
            progress = progress,
            strokeWidth = strokeSize * 6,
            color = strokeColor,
            modifier = Modifier.fillMaxSize()
        )

        val animationOneProgress = remember { Animatable(0f) }
        val animationTwoProgress = remember { Animatable(0f) }
        val animationThreeProgress = remember { Animatable(0f) }
        val animationFourProgress = remember { Animatable(0f) }
        val animationFiveProgress = remember { Animatable(0f) }
        val animationSixProgress = remember { Animatable(0f) }
        val animationSevenProgress = remember { Animatable(0f) }
        val animationDuration = 200

        LaunchedEffect(key1 = isAnimating) {
            if (!isAnimating) {
                animationOneProgress.snapTo(0f)
                animationTwoProgress.snapTo(0f)
                animationThreeProgress.snapTo(0f)
                animationFourProgress.snapTo(0f)
                animationFiveProgress.snapTo(0f)
                animationSixProgress.snapTo(0f)
                animationSevenProgress.snapTo(0f)
                return@LaunchedEffect
            }

            launch {
                animationOneProgress.animateTo(
                    1f,
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        delayMillis = 0,
                        easing = LinearEasing
                    )
                )
                animationTwoProgress.animateTo(
                    1f,
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        delayMillis = 0,
                        easing = CubicBezierEasing(0.34f, 1.8f, 0.64f, 1f),
                    )
                )
            }

            launch {
                animationThreeProgress.animateTo(
                    1f,
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        delayMillis = (animationDuration * 1.5f).roundToInt(),
                        easing = EaseOut
                    )
                )

                animationFourProgress.animateTo(
                    1f,
                    animationSpec = tween(
                        durationMillis = 600,
                        delayMillis = 0,
                        easing = LinearEasing
                    )
                )

                val spec = infiniteRepeatable<Float>(
                    tween(
                        durationMillis = 600,
                        delayMillis = 0,
                        easing = LinearEasing
                    ),
                    RepeatMode.Restart
                )

                animationFiveProgress.animateTo(
                    1f,
                    spec
                )
            }
        }

        LaunchedEffect(key1 = progress, key2 = animationFiveProgress.value) {
            if (progress != 1f) return@LaunchedEffect

            if (animationFiveProgress.value >= 0.9f) {
                animationFiveProgress.snapTo(0f)
            }

            if (animationFiveProgress.value == 0f) {
                animationSixProgress.animateTo(
                    1f,
                    animationSpec = tween(
                        durationMillis = 600,
                        delayMillis = 0,
                        easing = LinearEasing
                    )
                )
                animationSevenProgress.animateTo(
                    1f,
                    animationSpec = tween(
                        durationMillis = 600,
                        delayMillis = 0,
                        easing = LinearEasing
                    )
                )
            }
        }
        val downloadPath = remember {
            Path()
        }

        Image(
            painter = painterResource(id = R.drawable.onoff_icon),
            contentDescription = "connect - disconnect icon"
        )

        Canvas(modifier = Modifier.fillMaxSize(fraction = 0.5f)) {
            drawRect(color = Neutral1.copy(alpha = 0.2f))
            downloadPath.reset()

            val downloadLineHeight = size.height * (1f - animationOneProgress.value)
            val downloadLineY =
                (size.height - downloadLineHeight) / 2f * (1f - animationThreeProgress.value) - animationThreeProgress.value * (size.height * 1 / 2f - 4.dp.toPx())

            downloadPath.moveTo(size.width / 2f, downloadLineY)
            downloadPath.lineTo(size.width / 2f, downloadLineY)

            drawPath(
                color = strokeColor,
                path = downloadPath,
                style = Stroke(
                    width = (strokeSize * 2).toPx(),
                    cap = StrokeCap.Round,
                ),
            )
        }

//        Box (
//            contentAlignment = Alignment.TopCenter,
//            modifier = Modifier.align(Alignment.BottomCenter)
//                .fillMaxHeight(
//                    fraction = 0.2f + (0.1f * animationFourProgress.value) - (0.1f * animationSevenProgress.value)
//                )
//        ) {
//
//        }
    }

}