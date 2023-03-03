package com.aldajo92.xyparametricequations.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldajo92.xyparametricequations.SettingsViewModel
import com.aldajo92.xyparametricequations.domain.SettingsType
import com.aldajo92.xyparametricequations.ui.theme.XYParametricEquationsTheme


@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalComposeUiApi::class)
fun Activity.showSettingsBottomSheet(
    settingsViewModel: SettingsViewModel,
    circleSizeChange: (Float) -> Unit = {},
    defaultCircleSize: Float = 40f,
) {
    this.showAsBottomSheet { dismissDialog ->
        // TODO: Consider using a ViewModel for this
        var currentCircleSize by remember { mutableStateOf(defaultCircleSize) }

        val tMinValueField by settingsViewModel.minField.collectAsStateWithLifecycle()
        val tMaxValueField by settingsViewModel.maxField.collectAsStateWithLifecycle()
        val timeDurationValueField by settingsViewModel.timeField.collectAsStateWithLifecycle()

        val enableButtonState by settingsViewModel.enableButtonStateFlow.collectAsStateWithLifecycle(
            false
        )

        XYParametricEquationsTheme {
            Surface(
                color = Color.Transparent
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                    backgroundColor = MaterialTheme.colors.background,
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Settings",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                        SettingsComponentSlider(
                            modifier = Modifier.fillMaxWidth(),
                            textTitle = "Circle Size: ${
                                String.format(
                                    "%.2f",
                                    currentCircleSize
                                )
                            }",
                            startValue = defaultCircleSize,
                            range = 1f..40f,
                            selection = currentCircleSize
                        ) {
                            circleSizeChange(it)
                            currentCircleSize = it
                        }
                        Text(
                            text = "Parameters",
                            modifier = Modifier
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val keyboardController = LocalSoftwareKeyboardController.current
                            InputNumberField(
                                modifier = Modifier
                                    .weight(1f),
                                textTitle = "tMin:",
                                textValue = tMinValueField.value,
                                showError = tMinValueField.showError,
                                errorMessage = tMinValueField.errorMessage,
                                keyboardController = keyboardController
                            ) {
                                settingsViewModel.updateSettings(it, SettingsType.MIN_T)
                            }
                            InputNumberField(
                                modifier = Modifier
                                    .weight(1f),
                                textTitle = "tMax:",
                                textValue = tMaxValueField.value,
                                showError = tMaxValueField.showError,
                                errorMessage = tMaxValueField.errorMessage,
                                keyboardController = keyboardController
                            ) {
                                settingsViewModel.updateSettings(it, SettingsType.MAX_T)
                            }
                            InputNumberField(
                                modifier = Modifier.weight(1f),
                                textTitle = "Time (ms):",
                                textValue = timeDurationValueField.value,
                                showError = timeDurationValueField.showError,
                                errorMessage = timeDurationValueField.errorMessage,
                                keyboardController = keyboardController
                            ) {
                                settingsViewModel.updateSettings(it, SettingsType.TIME_DURATION)
                            }
                            Button(
                                modifier = Modifier.align(Alignment.Bottom),
                                enabled = enableButtonState,
                                onClick = {
                                    settingsViewModel.saveData()
                                    dismissDialog()
                                    keyboardController?.hide()
                                })
                            {
                                Text(text = "Save")
                            }
                        }
                    }
                }
            }
        }
    }
}
