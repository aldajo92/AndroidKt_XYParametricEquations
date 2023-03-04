package com.aldajo92.xyparametricequations.ui

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
    minUnitsAxisScreen: Float = 50f,
    tParameter: Float = 0f,
    circleSizeInUnits: Float = 1.75f,
    isDragEnabled: Boolean = true,
    offsetOrigin: Offset = Offset.Zero,
    onOffsetChange: (Offset) -> Unit = {},
    onZoomChange: (Float) -> Unit = {},
    evaluateCircleInParametricEquation: (Float) -> Point = { Point(it, it) }
) {
    var localWidth by remember { mutableStateOf(0f) }
    var localHeight by remember { mutableStateOf(0f) }
    val pixelsPerUnits = min(localWidth, localHeight) / minUnitsAxisScreen

    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        onOffsetChange(offsetChange)
        onZoomChange(zoomChange)
    }

    Box(
        modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    localWidth = placeable.width.toFloat()
                    localHeight = placeable.height.toFloat()
                    placeable.placeRelative(0, 0)
                }
            }
            .pointerInput(Unit) {
                if (isDragEnabled) detectDragGestures { change, dragAmount ->
                    change.consume()
                    onOffsetChange(dragAmount)
                }
            }
            .transformable(state = state)
    ) {
        val screenBottomRightCorner = Offset(localWidth, localHeight)
        // val newOriginOffset = Offset(50f, screenBottomRightCorner.y -50f) // TODO: Use this to include in configuration
        val defaultOrigin = (screenBottomRightCorner / 2f) + offsetOrigin

        XYAxisBoard(
            modifier = Modifier.fillMaxSize(),
            pointOrigin = defaultOrigin,
            width = localWidth,
            height = localHeight,
            pixelsPerUnits = pixelsPerUnits,
            colorAxisX = MaterialTheme.colors.onBackground,
            colorAxisY = MaterialTheme.colors.onBackground
        )
        XYCircleComponent(
            modifier = Modifier.fillMaxSize(),
            pointOrigin = defaultOrigin,
            pixelsPerUnits = pixelsPerUnits,
            circleColor = Color.Red,
            lineColor = MaterialTheme.colors.onBackground,
            tParameter = tParameter,
            circleSize = circleSizeInUnits * pixelsPerUnits,
            parametricEquation = evaluateCircleInParametricEquation
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
    pixelsPerUnits: Float,
    colorAxisX: Color = Color.Blue,
    colorAxisY: Color = Color.Blue
) {
    if (pixelsPerUnits < 0f) throw Exception("Value for step must be positive. Current value is $pixelsPerUnits")
    if (pixelsPerUnits == 0f) return

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
            divisionPosition = j * pixelsPerUnits
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
            divisionPosition = j * pixelsPerUnits
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
            divisionPosition = i * pixelsPerUnits
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
            divisionPosition = i * pixelsPerUnits
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
