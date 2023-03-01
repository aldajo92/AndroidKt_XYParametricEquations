package com.aldajo92.xyparametricequations.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun SettingsComponentSlider(
    modifier: Modifier = Modifier,
    textTitle: String = "No title",
    startValue: Float = 0f,
    selection: Float = 0f,
    range: ClosedFloatingPointRange<Float> = 12f..100f,
    onValueChange: (Float) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = textTitle,
            modifier = Modifier
        )
        SimpleContinuousSlider(
            modifier = Modifier.fillMaxWidth(),
            range = range,
            startValue = startValue,
            selection = selection
        ) {
            onValueChange(it)
        }
    }
}
