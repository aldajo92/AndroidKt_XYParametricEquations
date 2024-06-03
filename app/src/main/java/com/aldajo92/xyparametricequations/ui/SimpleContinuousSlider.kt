package com.aldajo92.xyparametricequations.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
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
            inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    )
}
