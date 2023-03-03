package com.aldajo92.xyparametricequations.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
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
    isDragEnabled: Boolean = true,
    parametricEquation: (Float) -> Point = { Point(it, it) }
) {
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }
    var step by remember { mutableStateOf(0f) }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }


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
            .pointerInput(Unit) {
                if (isDragEnabled) detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        val screenBottomRightCorner = Offset(width, height)
        // val newOriginOffset = Offset(50f, screenBottomRightCorner.y -50f) // TODO: Use this to include in configuration
        val dragOffset = Offset(offsetX, offsetY)
        val defaultOrigin = (screenBottomRightCorner / 2f) + dragOffset

        XYAxisBoard(
            modifier = Modifier.fillMaxSize(),
            pointOrigin = defaultOrigin,
            width = width,
            height = height,
            step = step,
            colorAxisX = MaterialTheme.colors.onBackground,
            colorAxisY = MaterialTheme.colors.onBackground
        )
        XYCircleComponent(
            modifier = Modifier.fillMaxSize(),
            pointOrigin = defaultOrigin,
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
    if (step == 0f) return

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
            start = Offset(0f, pointOrigin.y),
            end = Offset(width, pointOrigin.y)
        )
        drawLine(
            color = colorAxisY,
            start = Offset(pointOrigin.x, 0f),
            end = Offset(pointOrigin.x, height)
        )

        var divisionPosition: Float

        divisionPosition = 0f
        var j = 0
        var xStepPositive: Float
        while (abs(divisionPosition) <= width - pointOrigin.x) {
            divisionPosition = j * step
            xStepPositive = pointOrigin.x + divisionPosition
            drawLine(
                color = colorAxisX,
                start = Offset(xStepPositive, pointOrigin.y - divisionLength),
                end = Offset(xStepPositive, pointOrigin.y + divisionLength),
                strokeWidth = 2f
            )
            if (j % 5 == 0 && j > 0) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        j.toString(),
                        xStepPositive,
                        pointOrigin.y + divisionLength + textSizePixels,
                        textPaintX
                    )
                }
            }
            j++
        }

        divisionPosition = 0f
        j = 0
        var xStepNegative: Float
        while (abs(divisionPosition) <= pointOrigin.x) {
            divisionPosition = j * step
            xStepNegative = pointOrigin.x - divisionPosition
            drawLine(
                color = colorAxisX,
                start = Offset(xStepNegative, pointOrigin.y - divisionLength),
                end = Offset(xStepNegative, pointOrigin.y + divisionLength),
                strokeWidth = 2f
            )
            if (j % 5 == 0 && j > 0) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        (-j).toString(),
                        xStepNegative - (textSizePixels / 7f),
                        pointOrigin.y + divisionLength + textSizePixels,
                        textPaintX
                    )
                }
            }
            j++
        }


        divisionPosition = 0f
        var i = 0
        var yStepPositive: Float
        while (
            abs(divisionPosition) <= pointOrigin.y
        ) {
            divisionPosition = i * step
            yStepPositive = pointOrigin.y - divisionPosition
            drawLine(
                color = colorAxisY,
                start = Offset(pointOrigin.x - divisionLength, yStepPositive),
                end = Offset(pointOrigin.x + divisionLength, yStepPositive),
                strokeWidth = 2f
            )
            if (i % 5 == 0 && i > 0) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        i.toString(),
                        pointOrigin.x - divisionLength,
                        yStepPositive + 10f,
                        textPaintY
                    )
                }
            }
            i++
        }

        divisionPosition = 0f
        i = 0
        var yStepNegative: Float
        while (
            abs(divisionPosition) <= height - pointOrigin.y
        ) {
            divisionPosition = i * step
            yStepNegative = pointOrigin.y + divisionPosition
            drawLine(
                color = colorAxisY,
                start = Offset(pointOrigin.x - divisionLength, yStepNegative),
                end = Offset(pointOrigin.x + divisionLength, yStepNegative),
                strokeWidth = 2f
            )
            if (i % 5 == 0 && i > 0) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        (-i).toString(),
                        pointOrigin.x - divisionLength,
                        yStepNegative + 10f,
                        textPaintY
                    )
                }
            }
            i++
        }
    }
}
