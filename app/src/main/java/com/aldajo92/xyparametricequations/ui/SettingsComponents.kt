package com.aldajo92.xyparametricequations.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            modifier = Modifier,
            text = textTitle
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

@Preview
@Composable
fun SettingsComponentToggle(
    modifier: Modifier = Modifier,
    textTitle: String = "No title",
    value: Boolean = true,
    onValueChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = textTitle
        )
        Switch(
            modifier = Modifier,
            checked = value, onCheckedChange = onValueChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colors.primary,
                checkedTrackColor = MaterialTheme.colors.secondaryVariant,
            )
        )
    }
}
