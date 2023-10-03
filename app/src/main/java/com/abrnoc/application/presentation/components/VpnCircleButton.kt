package com.abrnoc.application.presentation.components

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abrnoc.application.R
import com.abrnoc.application.presentation.connection.BaseService
import com.abrnoc.application.presentation.connection.DataStore
import com.abrnoc.application.presentation.ui.theme.Neutral0
import com.abrnoc.application.presentation.ui.theme.Sky0
import com.google.android.material.progressindicator.BaseProgressIndicator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// private val iconStopping by lazy { AnimatedState(R.drawable.ic_service_stopping) }
private var checked = false
private var delayedAnimation: Job? = null
private lateinit var progress: BaseProgressIndicator<*>

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VpnConnectButton(
    outStrokeWidth: Float = 8f,
    thumbStrokeWidth: Float = 6f,
    onClick: () -> Unit,
    context: Context,
    state: BaseService.State,
) {
    var connected by remember {
        mutableStateOf(false)
    }
    var text by remember {
        mutableStateOf("Tap To Connect ")
    }
    var titleText by remember {
        mutableStateOf("You're not Connected")
    }

    var progress by remember {
        mutableStateOf(0)
    }

    var color by remember {
        mutableStateOf(Color(0xFFA30022))
    }

    val animateProgress by animateIntAsState(
        targetValue = progress,
        animationSpec = tween(1200)
    )

    val animateColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(1500)
    )

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        progress = 360
        delay(1300)
        progress = 0
    }

//    Surface {
    Column(
        Modifier
            .padding(10.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = titleText,
            color = Neutral0,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Surface(
            shape = CircleShape,
            color = Color.White,
            elevation = 3.dp,
            onClick = {
                onClick()
                changeState(state, DataStore.serviceState, context)
                scope.launch {
                    if (!connected) {
                        connected = true
                        color = Color.DarkGray
                        text = "Connecting.. "
                        progress = 300
                        delay(1500)
                        progress = 325
                        delay(5000)
                        progress = 360
                        text = "Connected"
                        color = Color(0xFF008B48)
                        titleText = "You are connected"
                    } else {
                        text = "Tap To Connect"
                        color = Color.DarkGray
                        progress = 0
                        connected = false
                    }
                }
            }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.background(Color.White),
            ) {
                Canvas(
                    Modifier
                        .size(50.dp)
                        .padding(10.dp)
                ) {
                    drawThumb(
                        color = animateColor,
                        thumbStrokeWidth = thumbStrokeWidth
                    )
                }

                Canvas(
                    Modifier
                        .size(150.dp)
                        .padding(10.dp)
                ) {
                    drawCircleProgressBar(
                        color = animateColor,
                        progressStrokeWidth = outStrokeWidth,
                        sweepAngle = animateProgress.toFloat()
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(15.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.wrapContentSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.tap_icon),
                contentDescription = "connection description text",
                modifier = Modifier.padding(12.dp)
            )

            Text(
                text = text,
                color = Neutral0,
                fontSize = 16.sp,
            )
        }
//            Text(
//                text = text,
//                style = MaterialTheme.typography.overline.copy(
//                    fontSize = 16.sp
//                )
//            )
    }
//    }
}

fun DrawScope.drawThumb(
    thumbStrokeWidth: Float,
    color: Color = Color(0xFFA30022),
) {
    drawArc(
        color = color,
        startAngle = -60f,
        sweepAngle = 300f,
        useCenter = false,
        topLeft = Offset.Zero,
        style = Stroke(
            width = thumbStrokeWidth,
            cap = StrokeCap.Round,
        )
    )

    drawLine(
        color = color,
        start = center,
        end = Offset(
            x = center.x,
            y = Offset.Zero.y - 5 // because of padding
        ),
        cap = StrokeCap.Round,
        strokeWidth = thumbStrokeWidth
    )
}

fun DrawScope.drawCircleProgressBar(
    color: Color = Color.DarkGray,
    progressStrokeWidth: Float,
    sweepAngle: Float,
) {
    drawArc(
        color = Sky0,
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = Offset.Zero,
        style = Stroke(
            width = progressStrokeWidth * 7,
            cap = StrokeCap.Round,
        )
    )

    drawArc(
        color = color,
        startAngle = -90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset.Zero,
        style = Stroke(
            width = progressStrokeWidth * 6,
            cap = StrokeCap.Round,
        )
    )
}

// @Preview
// @Composable
// private fun Preview() {
//    VpnConnectButton(onClick = {}, context = LocalContext.current, state = BaseService.State)
// }

fun changeState(
    state: BaseService.State,
    previousState: BaseService.State,
    context: Context,
) {
    when (state) {
        BaseService.State.Connecting -> println(" $$$$ its connecting ") // / changeState(iconConnecting, animate)
        BaseService.State.Connected -> println(" $$$$ its connected ") // /changeState(iconConnected, animate)
        BaseService.State.Stopping -> println(" $$$$ its Stopped ...  ")
        else -> println(" $$$$ its confused ")
    }
    checked = state == BaseService.State.Connected
    val description = context.getText(if (state.canStop) R.string.stop else R.string.connect)
//    contentDescription = description
//    TooltipCompat.setTooltipText(this, description)
    val enabled = state.canStop || state == BaseService.State.Stopped
//    isEnabled = enabled
//    if (Build.VERSION.SDK_INT >= 24) pointerIcon = PointerIcon.getSystemIcon(
//        context,
//        if (enabled) PointerIcon.TYPE_HAND else PointerIcon.TYPE_WAIT
//    )
}

private fun hideProgress() {
    delayedAnimation?.cancel()
}
