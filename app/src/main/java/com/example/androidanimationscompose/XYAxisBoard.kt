package com.example.androidanimationscompose

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.abs

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

    val divisionLength = step / 2f
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
