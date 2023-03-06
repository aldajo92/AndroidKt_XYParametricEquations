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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import com.aldajo92.xyparametricequations.domain.Point
import com.aldajo92.xyparametricequations.domain.invertYaxis
import com.aldajo92.xyparametricequations.domain.toOffset
import com.aldajo92.xyparametricequations.domain.translate
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
    showPath: Boolean = false,
    maxPathPoints: Int = 20,
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
        var currentCirclePositionOffset: Offset? by remember { mutableStateOf(null) }
        var currentCirclePoint: Point? by remember { mutableStateOf(null) }

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
        ) { circlePointPosition, circlePositionOffset ->
            currentCirclePoint = circlePointPosition
            currentCirclePositionOffset = circlePositionOffset
        }
        XYPathComponent(
            modifier = Modifier.fillMaxSize(),
            pointOrigin = defaultOrigin,
            pixelsPerUnits = pixelsPerUnits,
            newPoint = currentCirclePoint,
            showPath = showPath,
            maxPathPoints = maxPathPoints
        )
        topContent?.let { it() }
    }
}

// TODO: Consider use coroutines to draw the path: https://stackoverflow.com/questions/64116377/how-to-call-kotlin-coroutine-in-composable-function-callbacks
@Composable
fun XYPathComponent(
    modifier: Modifier = Modifier,
    pathColor: Color = Color.Green,
    pointOrigin: Offset,
    pixelsPerUnits: Float,
    showPath: Boolean = false,
    maxPathPoints: Int = 20,
    newPoint: Point? = null,
) {
    val pointsOffsetWithoutTranslation = remember { mutableListOf<Point>() }

    LaunchedEffect(showPath) {
        if (!showPath) {
            pointsOffsetWithoutTranslation.clear()
        }
    }

    LaunchedEffect(newPoint) {
        if (newPoint != null && showPath) {
            pointsOffsetWithoutTranslation.add(
                newPoint.invertYaxis()
            )
        } else {
            pointsOffsetWithoutTranslation.clear()
        }

        if (pointsOffsetWithoutTranslation.size > maxPathPoints) {
            pointsOffsetWithoutTranslation.removeAt(0)
        }
    }

    if (pointsOffsetWithoutTranslation.isNotEmpty()) Canvas(modifier = modifier.fillMaxSize()) {
        pointsOffsetWithoutTranslation.forEachIndexed { index, point ->
            if (index > 0) {
                val pointRespectToOriginPrevious = pointsOffsetWithoutTranslation[index - 1]
                    .toOffset(pixelsPerUnits)
                    .translate(pointOrigin)
                val pointRespectToOrigin = point
                    .toOffset(pixelsPerUnits)
                    .translate(pointOrigin)
                drawLine(
                    color = pathColor,
                    start = pointRespectToOriginPrevious,
                    end = pointRespectToOrigin,
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )
            }
        }
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
    if (pixelsPerUnits < 0f) throw Exception("Value for pixelsPerUnits must be positive. Current value is $pixelsPerUnits")
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

        // TODO: Try to find a better formula
        val linesToShow: Int = (width / (2 * pixelsPerUnits)).let {
            if (it < 50) 1 else (it / (25)).toInt()
        }

        while (abs(divisionPosition) <= width - pointOrigin.x) {
            divisionPosition = j * pixelsPerUnits * linesToShow
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
                        (j * linesToShow).toString(),
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
            divisionPosition = j * pixelsPerUnits * linesToShow
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
                        (-j * linesToShow).toString(),
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
            divisionPosition = i * pixelsPerUnits * linesToShow
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
                        (i * linesToShow).toString(),
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
            divisionPosition = i * pixelsPerUnits * linesToShow
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
                        (-i * linesToShow).toString(),
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
