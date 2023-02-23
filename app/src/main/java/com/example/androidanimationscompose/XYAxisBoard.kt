package com.example.androidanimationscompose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import kotlin.math.abs
import kotlin.math.min


@Composable
fun XYAxisBoard(modifier: Modifier = Modifier) {
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }
    var step by remember { mutableStateOf(0f) }
    var divisionLength by remember { mutableStateOf(0f) }

    Box(modifier = modifier.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            width = placeable.width.toFloat()
            height = placeable.height.toFloat()
            step = min(width, height) / 50f
            divisionLength = step / 2f
            placeable.placeRelative(0, 0)
        }
    }) {

        val origin = Point(width, height)
        val newOrigin = origin / 2f

        Canvas(modifier = Modifier.fillMaxSize()) {
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

            var i = 0f
            while (abs(i) < (height / 2f)) {
                drawLine(
                    color = Color.Red,
                    start = Offset((width / 2) - divisionLength, newOrigin.y - i),
                    end = Offset((width / 2f) + divisionLength, newOrigin.y - i)
                )
                drawLine(
                    color = Color.Red,
                    start = Offset((width / 2) - divisionLength, newOrigin.y + i),
                    end = Offset((width / 2f) + divisionLength, newOrigin.y + i)
                )
                i += step
            }

            var j = 0f
            while (j < (width / 2)) {
                drawLine(
                    color = Color.Red,
                    start = Offset(newOrigin.x - j, (height / 2) - divisionLength),
                    end = Offset(newOrigin.x - j, (height / 2) + divisionLength)
                )
                drawLine(
                    color = Color.Red,
                    start = Offset(newOrigin.x + j, (height / 2) - divisionLength),
                    end = Offset(newOrigin.x + j, (height / 2) + divisionLength)
                )
                j += step
            }
        }
    }
}