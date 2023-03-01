package com.aldajo92.xyparametricequations.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SimpleContinuousSlider(
    modifier: Modifier = Modifier,
    range: ClosedFloatingPointRange<Float> = 0f..100f,
    startValue: Float = (range.start + range.endInclusive) / 2f,
    selection: Float = startValue,
    enableSlider: Boolean = true,
    onValueChanged: (Float) -> Unit = {}
) {
    Slider(
        modifier = modifier,
        value = selection,
        valueRange = range,
        enabled = enableSlider,
        onValueChange = {
            onValueChanged(it)
        },
        colors = SliderDefaults.colors(
            activeTrackColor = Color.Transparent,
            inactiveTrackColor = MaterialTheme.colors.primary.copy(alpha = 0.2f)
        )
    )
}
