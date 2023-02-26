package com.example.androidanimationscompose

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.abs

@Composable
fun XYAxisBoard(
    modifier: Modifier = Modifier,
    pointOrigin: Offset,
    width: Float,
    height: Float,
    step: Float,
) {

    if (step < 0f) throw Exception("Value for step must be positive. Current value is $step")
    val divisionLength = step / 2f
    val textPaint = Paint().apply {
        textSize = 30f
        color = android.graphics.Color.BLUE
        textAlign = Paint.Align.RIGHT
    }

    Canvas(modifier = modifier) {
        drawLine(
            color = Color.Blue,
            start = Offset(width / 2, 0f),
            end = Offset(width / 2, height)
        )
        drawLine(
            color = Color.Blue,
            start = Offset(0f, height / 2),
            end = Offset(width, height / 2)
        )

        var i = 0
        var divisionPosition = 0f
        while (abs(divisionPosition) < (height / 2f)) {
            divisionPosition = i * step
            drawLine(
                color = Color.Red,
                start = Offset((width / 2) - divisionLength, pointOrigin.y - divisionPosition),
                end = Offset((width / 2f) + divisionLength, pointOrigin.y - divisionPosition)
            )
            drawLine(
                color = Color.Red,
                start = Offset((width / 2) - divisionLength, pointOrigin.y + divisionPosition),
                end = Offset((width / 2f) + divisionLength, pointOrigin.y + divisionPosition)
            )
            if (i % 5 == 0 && i > 0) {
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        i.toString(),
                        pointOrigin.x - divisionLength,
                        pointOrigin.y - divisionPosition + 10f,
                        textPaint
                    )
                }
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        (-i).toString(),
                        pointOrigin.x - divisionLength,
                        pointOrigin.y + divisionPosition + 10f,
                        textPaint
                    )
                }
            }
            i++
        }

        var j = 0f
        while (j < (width / 2)) {
            drawLine(
                color = Color.Red,
                start = Offset(pointOrigin.x - j, (height / 2) - divisionLength),
                end = Offset(pointOrigin.x - j, (height / 2) + divisionLength)
            )
            drawLine(
                color = Color.Red,
                start = Offset(pointOrigin.x + j, (height / 2) - divisionLength),
                end = Offset(pointOrigin.x + j, (height / 2) + divisionLength)
            )
            j += step
        }
    }
}
