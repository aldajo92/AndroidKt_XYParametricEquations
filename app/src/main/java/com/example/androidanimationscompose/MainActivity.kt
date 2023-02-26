package com.example.androidanimationscompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.example.androidanimationscompose.ui.theme.AndroidAnimationsComposeTheme
import kotlin.math.min
import kotlin.math.pow


/* TODO:
* Support DarkMode
* Add resolution for x, y steps in the plane (Done)
* Add input text for equations.
* Add slider for parameter
* Add configuration section
*  */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidAnimationsComposeTheme {
                val resolution = 50f

                var width by remember { mutableStateOf(0f) }
                var height by remember { mutableStateOf(0f) }
                var step by remember { mutableStateOf(0f) }

                Box(
                    Modifier
                        .fillMaxSize()
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            layout(placeable.width, placeable.height) {
                                width = placeable.width.toFloat()
                                height = placeable.height.toFloat()
                                step = min(width, height) / resolution
                                placeable.placeRelative(0, 0)
                            }
                        }
                ) {
                    val screenBottomRightCorner = Offset(width, height)
                    val newOriginOffset = screenBottomRightCorner / 2f
                    XYAxisBoard(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        pointOrigin = newOriginOffset,
                        width = width,
                        height = height,
                        step = step
                    )
                    AnimatedCircleV2(
                        modifier = Modifier.fillMaxSize(),
                        pointOrigin = newOriginOffset,
                        step = step
                    )
                }
            }
        }
    }
}

@Composable
fun BoxScope.AnimatedCircleV2(
    modifier: Modifier = Modifier,
    circleColor: Color = Color.Blue,
    lineColor: Color = Color.Blue,
    textColor: Color = Color.Black,
    pointOrigin: Offset,
    step: Float,
) {

    val tParameterStart = 0f
    val tParameterEnd = 5f

    var isRunning by remember { mutableStateOf(false) }
    val pathEffect = remember {
        PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    var tParameter by remember { mutableStateOf(tParameterStart) }
    val tAnimation2 = remember { Animatable(tParameter, Float.VectorConverter) }
    val animationSpec = remember {
        InfiniteRepeatableSpec<Float>(
            tween(durationMillis = 5000, easing = LinearEasing)
        )
    }
    LaunchedEffect(isRunning) {
        if (isRunning) {
            tAnimation2.snapTo(tParameterStart)
            tAnimation2.animateTo(
                targetValue = tParameterEnd,
                animationSpec = animationSpec
            ) {
                tParameter = this.value
            }
        }
    }

    val circleInitialCenter = Point(10f, 10f)
    val pDirector = Point(-1f, 1f)

    val circleCenter = rectLine(circleInitialCenter, pDirector, tParameter)
        .invertYaxis()
        .translate(pointOrigin.x / step, pointOrigin.y / step)

    val circleCenterOffset = circleCenter.toOffset(step)

    if (step > 0) Canvas(modifier = modifier.fillMaxSize()) {
        drawCircle(
            color = circleColor,
            radius = 50f,
            center = circleCenterOffset,
            style = Stroke(5f)
        )
        drawLine(
            color = lineColor,
            start = Offset(pointOrigin.x, circleCenterOffset.y),
            end = circleCenterOffset,
            pathEffect = pathEffect
        )
        drawLine(
            color = lineColor,
            start = Offset(circleCenterOffset.x, pointOrigin.y),
            end = circleCenterOffset,
            pathEffect = pathEffect
        )
    }
    Text(
        modifier = Modifier.padding(10.dp),
        text = "t: ${String.format("%.2f", tParameter)}",
        color = textColor
    )
    Button(
        modifier = Modifier
            .padding(10.dp)
            .align(Alignment.TopEnd),
        onClick = {
            isRunning = !isRunning
        }
    ) {
        Text(if (isRunning) "Stop Animation" else "Start Animation")
    }
}


fun rectLine(p0: Point, pointDirector: Point, t: Float): Point = p0 + (pointDirector * t)
fun parabola(t: Float): Point = Point(t * 80f, (t * 10f).pow(2))
