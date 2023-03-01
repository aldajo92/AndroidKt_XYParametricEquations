package com.aldajo92.xyparametricequations.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layout
import com.aldajo92.xyparametricequations.domain.Point
import kotlin.math.abs
import kotlin.math.min

@Composable
fun XYMainUI(
    modifier: Modifier = Modifier,
    topContent: (@Composable BoxScope.() -> Unit)? = null,
    resolution: Float = 50f,
    tParameter: Float = 0f,
    circleSize: Float = 40f,
    parametricEquation: (Float) -> Point = { Point(it, it) }
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
        XYCircleComponent(
            modifier = Modifier.fillMaxSize(),
            pointOrigin = newOriginOffset,
            step = step,
            circleColor = Color.Red,
            lineColor = MaterialTheme.colors.onBackground,
            tParameter = tParameter,
            circleSize = circleSize,
            parametricEquation = parametricEquation
        )
        topContent?.let { it() }
    }
}

@Composable
fun XYAxisBoard(
    modifier: Modifier = Modifier,
    pointOrigin: Offset,
    width: Float,
    height: Float,
    step: Float,
    colorAxisX: Color = Color.Blue,
    colorAxisY: Color = Color.Blue
) {

    if (step < 0f) throw Exception("Value for step must be positive. Current value is $step")

    val divisionLength = 10f
    val textSizePixels = 30f

    val textPaintX = Paint().apply {
        textSize = textSizePixels
        color = colorAxisX.toArgb()
        textAlign = Paint.Align.CENTER
    }
    val textPaintY = Paint().apply {
        textSize = textSizePixels
        color = colorAxisY.toArgb()
        textAlign = Paint.Align.RIGHT
    }

    Canvas(modifier = modifier) {
        drawLine(
            color = colorAxisX,
            start = Offset(0f, height / 2),
            end = Offset(width, height / 2)
        )
        drawLine(
            color = colorAxisY,
            start = Offset(width / 2, 0f),
            end = Offset(width / 2, height)
        )

        var divisionPosition: Float

        divisionPosition = 0f
        var j = 0
        while (abs(divisionPosition) < (width / 2)) {
            divisionPosition = j * step
            drawLine(
                color = colorAxisX,
                start = Offset(pointOrigin.x - divisionPosition, (height / 2) - divisionLength),
                end = Offset(pointOrigin.x - divisionPosition, (height / 2) + divisionLength),
                strokeWidth = 2f
            )
            drawLine(
                color = colorAxisX,
                start = Offset(pointOrigin.x + divisionPosition, (height / 2) - divisionLength),
                end = Offset(pointOrigin.x + divisionPosition, (height / 2) + divisionLength),
                strokeWidth = 2f
            )
            if (j % 5 == 0 && j > 0) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        j.toString(),
                        pointOrigin.x + divisionPosition,
                        pointOrigin.y + divisionLength + textSizePixels,
                        textPaintX
                    )
                }
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        (-j).toString(),
                        pointOrigin.x - divisionPosition - (textSizePixels / 7f),
                        pointOrigin.y + divisionLength + textSizePixels,
                        textPaintX
                    )
                }
            }
            j++
        }

        divisionPosition = 0f
        var i = 0
        while (abs(divisionPosition) < (height / 2f)) {
            divisionPosition = i * step
            drawLine(
                color = colorAxisY,
                start = Offset((width / 2) - divisionLength, pointOrigin.y - divisionPosition),
                end = Offset((width / 2f) + divisionLength, pointOrigin.y - divisionPosition),
                strokeWidth = 2f
            )
            drawLine(
                color = colorAxisY,
                start = Offset((width / 2) - divisionLength, pointOrigin.y + divisionPosition),
                end = Offset((width / 2f) + divisionLength, pointOrigin.y + divisionPosition),
                strokeWidth = 2f
            )
            if (i % 5 == 0 && i > 0) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        i.toString(),
                        pointOrigin.x - divisionLength,
                        pointOrigin.y - divisionPosition + 10f,
                        textPaintY
                    )
                }
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        (-i).toString(),
                        pointOrigin.x - divisionLength,
                        pointOrigin.y + divisionPosition + 10f,
                        textPaintY
                    )
                }
            }
            i++
        }
    }
}

