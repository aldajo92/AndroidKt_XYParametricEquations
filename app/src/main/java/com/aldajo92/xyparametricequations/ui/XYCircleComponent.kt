package com.aldajo92.xyparametricequations.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import com.aldajo92.xyparametricequations.domain.Point
import com.aldajo92.xyparametricequations.domain.invertYaxis
import com.aldajo92.xyparametricequations.domain.toOffset
import com.aldajo92.xyparametricequations.domain.translate

@Composable
fun XYCircleComponent(
    modifier: Modifier = Modifier,
    circleColor: Color = Color.Blue,
    lineColor: Color = Color.Blue,
    circleSize : Float = 40f,
    pointOrigin: Offset,
    step: Float,
    tParameter: Float = 0f,
    parametricEquation: (Float) -> Point = { Point(it, it) }
) {

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
