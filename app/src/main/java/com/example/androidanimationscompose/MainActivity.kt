package com.example.androidanimationscompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
* Support DarkMode (Done)
* Add resolution for x, y steps in the plane (Done)
* Add input text for equations.
* Add slider for parameter. (Done)
* Add configuration section.
* Add animation for parameter.
*  */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidAnimationsComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val resolution = 50f
                    var tParameter by remember { mutableStateOf(0f) }
                    Column(Modifier.fillMaxSize()) {
                        RenderXYBoardUI(
                            modifier = Modifier.weight(1f),
                            resolution = resolution,
                            tParameter = tParameter
                        )
                        SimpleContinuousSlider(
                            Modifier.padding(horizontal = 16.dp),
                            -10f..10f,
                        ) {
                            Log.d("MainActivity", "onValueChanged: $it")
                            tParameter = it
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleContinuousSlider(
    modifier: Modifier = Modifier,
    range: ClosedFloatingPointRange<Float> = 0f..100f,
    startValue: Float = (range.start + range.endInclusive) / 2f,
    onValueChanged: (Float) -> Unit = {}
) {
    var selection by remember { mutableStateOf(startValue) }

    Slider(
        modifier = modifier,
        value = selection,
        valueRange = range,
        onValueChange = {
            selection = it
            onValueChanged(it)
        }
    )
}

@Composable
fun RenderXYBoardUI(
    modifier: Modifier = Modifier,
    resolution: Float = 50f,
    tParameter: Float = 0f
) {
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }
    var step by remember { mutableStateOf(0f) }

    Box(
        modifier
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
            modifier = Modifier.fillMaxSize(),
            pointOrigin = newOriginOffset,
            width = width,
            height = height,
            step = step,
            colorAxisX = MaterialTheme.colors.onBackground,
            colorAxisY = MaterialTheme.colors.onBackground
        )
        AnimatedCircleV2(
            modifier = Modifier.fillMaxSize(),
            pointOrigin = newOriginOffset,
            step = step,
            circleColor = Color.Red,
            lineColor = MaterialTheme.colors.onBackground,
            textColor = MaterialTheme.colors.onBackground,
            tParameter = tParameter
        )
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
    tParameter: Float = 0f
) {

//    TODO: Pending to move it as a separate feature
//    val tParameterStart = 0f
//    val tParameterEnd = 5f

//    var isRunning by remember { mutableStateOf(false) }

//    var tParameter by remember { mutableStateOf(tParameterStart) }
//    val tAnimation2 = remember { Animatable(tParameter, Float.VectorConverter) }
//    val animationSpec = remember {
//        InfiniteRepeatableSpec<Float>(
//            tween(durationMillis = 5000, easing = LinearEasing)
//        )
//    }
//    LaunchedEffect(isRunning) {
//        if (isRunning) {
//            tAnimation2.snapTo(tParameterStart)
//            tAnimation2.animateTo(
//                targetValue = tParameterEnd,
//                animationSpec = animationSpec
//            ) {
//                tParameter = this.value
//            }
//        }
//    }

    val pathEffect = remember {
        PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
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
//    TODO: Pending to move it as a separate feature
//    Button(
//        modifier = Modifier
//            .padding(10.dp)
//            .align(Alignment.TopEnd),
//        onClick = {
//            isRunning = !isRunning
//        }
//    ) {
//        Text(if (isRunning) "Stop Animation" else "Start Animation")
//    }
}


fun rectLine(p0: Point, pointDirector: Point, t: Float): Point = p0 + (pointDirector * t)
fun parabola(t: Float): Point = Point(t * 80f, (t * 10f).pow(2))
