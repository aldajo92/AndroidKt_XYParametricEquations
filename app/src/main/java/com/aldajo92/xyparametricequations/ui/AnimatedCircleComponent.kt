package com.aldajo92.xyparametricequations.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.aldajo92.xyparametricequations.domain.Point
import com.aldajo92.xyparametricequations.domain.invertYaxis
import com.aldajo92.xyparametricequations.domain.toOffset
import com.aldajo92.xyparametricequations.domain.translate

@Composable
fun AnimatedCircleComponent(
    modifier: Modifier = Modifier,
    circleColor: Color = Color.Blue,
    lineColor: Color = Color.Blue,
    textColor: Color = Color.Black,
    circleSize : Float = 40f,
    pointOrigin: Offset,
    step: Float,
    tParameter: Float = 0f,
    parametricEquation: (Float) -> Point = { Point(it, it) }
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

    val circleCenter = parametricEquation(tParameter)

    val circleCenterOffset = circleCenter
        .invertYaxis()
        .toOffset(step)
        .let {
            if (it == Offset.Unspecified) Offset(0f, 0f) else it
        }
        .translate(pointOrigin)

    if (step > 0) Canvas(modifier = modifier.fillMaxSize()) {
        drawCircle(
            color = circleColor,
            radius = circleSize,
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
