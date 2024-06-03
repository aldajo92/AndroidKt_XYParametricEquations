package com.aldajo92.xyparametricequations.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aldajo92.xyparametricequations.SettingsUIField

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
fun SettingsComponentSwitch(
    modifier: Modifier = Modifier,
    textTitle: String = "No title",
    settingsUIField: SettingsUIField = SettingsUIField("20"),
    keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
    onEnableChanged: (Boolean) -> Unit = {},
    onValueChange: (String) -> Unit = {}
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = textTitle
        )
        InputNumberField(
            modifier = Modifier.weight(1f),
            textTitle = "Points:",
            textValue = settingsUIField.value,
            showError = settingsUIField.showError,
            errorMessage = settingsUIField.errorMessage,
            keyboardController = keyboardController,
            onValueChange = onValueChange
        )
        Switch(
            modifier = Modifier.weight(1f).align(Alignment.CenterVertically),
            checked = settingsUIField.enabled,
            onCheckedChange = onEnableChanged
        )
    }
}
