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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidanimationscompose.ui.theme.AndroidAnimationsComposeTheme
import kotlin.math.pow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidAnimationsComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    XYAxisBoard(modifier = Modifier.fillMaxSize().background(Color.White))
                    AnimatedCircleV2(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}


@Preview
@Composable
fun AnimatedCircleV2(modifier: Modifier = Modifier) {
    var isRunning by remember { mutableStateOf(false) }

    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }


    val pathEffect = remember {
        PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    var tParameter by remember { mutableStateOf(-3f) }
    val tAnimation2 = remember { Animatable(tParameter, Float.VectorConverter) }
    val animationSpec = remember {
        InfiniteRepeatableSpec<Float>(
            tween(durationMillis = 5000, easing = LinearEasing)
        )
    }
    LaunchedEffect(isRunning) {
        if (isRunning) {
            tAnimation2.snapTo(-3f)
            tAnimation2.animateTo(
                targetValue = 5f,
                animationSpec = animationSpec
            ) {
                tParameter = this.value
            }
        }
    }

    Box(
        modifier = modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    width = placeable.width.toFloat()
                    height = placeable.height.toFloat()
                    placeable.placeRelative(0, 0)
                }
            }
    ) {
        val screenBottomRightCorner = Point(width, height)
        val newOrigin = screenBottomRightCorner / 2f

        val circleInitialCenter = Point(0f, 200f)
        val pDirector = Point(-100f, 100f)

        val circleCenter = rectLine(circleInitialCenter, pDirector, tParameter)
            .invertYaxis().translate(newOrigin)

//        val circleCenter = parabola(tParameter)
//            .invertYaxis().translate(newOrigin)

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.Blue,
                radius = size.minDimension / 20,
                center = circleCenter.toOffset(),
                style = Stroke(5f)
            )
            drawLine(
                color = Color.Blue,
                start = Offset(newOrigin.x, circleCenter.y),
                end = circleCenter.toOffset(),
                pathEffect = pathEffect
            )
            drawLine(
                color = Color.Blue,
                start = Offset(circleCenter.x, newOrigin.y),
                end = circleCenter.toOffset(),
                pathEffect = pathEffect
            )
        }
        Text(
            modifier = Modifier.padding(10.dp),
            text = "t: ${String.format("%.2f", tParameter)}"
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
}


fun rectLine(p0: Point, pointDirector: Point, t: Float): Point = p0 + (pointDirector * t)
fun parabola(t: Float): Point = Point(t*80f, (t*10f).pow(2))
