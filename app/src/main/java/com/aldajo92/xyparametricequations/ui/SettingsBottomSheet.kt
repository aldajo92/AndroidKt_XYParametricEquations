package com.aldajo92.xyparametricequations.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aldajo92.xyparametricequations.SettingsViewModel
import com.aldajo92.xyparametricequations.domain.SettingsType
import com.aldajo92.xyparametricequations.ui.theme.XYParametricEquationsTheme

fun Activity.showSettingsBottomSheet(
    circleSizeChange: (Float) -> Unit = {},
) {
    this.showAsBottomSheet { dismissDialog ->
        val settingsViewModel = viewModel(SettingsViewModel::class.java)
        // TODO: Consider using a ViewModel for this
        val currentCircleSize by settingsViewModel.circleSizeField

        val tMinValueField by settingsViewModel.minField.collectAsStateWithLifecycle(lifecycleOwner = LocalLifecycleOwner.current)
        val tMaxValueField by settingsViewModel.maxField.collectAsStateWithLifecycle(lifecycleOwner = LocalLifecycleOwner.current)
        val timeDurationValueField by settingsViewModel.timeField.collectAsStateWithLifecycle(
            lifecycleOwner = LocalLifecycleOwner.current
        )
        val pathField by settingsViewModel.showPath.collectAsStateWithLifecycle(lifecycleOwner = LocalLifecycleOwner.current)

        val enableButtonState by settingsViewModel.enableButtonStateFlow.collectAsStateWithLifecycle(
            false,
            lifecycleOwner = LocalLifecycleOwner.current
        )

        XYParametricEquationsTheme {
            val keyboardController = LocalSoftwareKeyboardController.current
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
                colors = CardColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                    disabledContentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(bottom = 16.dp)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "Settings"
                        )
                        Button(
                            modifier = Modifier.align(Alignment.CenterEnd),
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
                    SettingsComponentSlider(
                        modifier = Modifier.fillMaxWidth(),
                        textTitle = "Circle Size: ${
                            String.format(
                                "%.2f",
                                currentCircleSize
                            )
                        }",
                        startValue = currentCircleSize,
                        range = 0.1f..40f,
                        selection = currentCircleSize
                    ) {
                        settingsViewModel.updateCircleSize(it)
                        circleSizeChange(it)
                    }
                    SettingsComponentSwitch(
                        textTitle = "Path points:",
                        settingsUIField = pathField,
                        onEnableChanged = {
                            settingsViewModel.updateSettings(
                                it.toString(),
                                SettingsType.SHOW_PATH
                            )
                        },
                        onValueChange = {
                            settingsViewModel.updateSettings(
                                it,
                                SettingsType.PATH_POINTS
                            )
                        }
                    )
                    Text(
                        text = "Parameters",
                        modifier = Modifier
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        InputNumberField(
                            modifier = Modifier.weight(1f),
                            textTitle = "tMin:",
                            textValue = tMinValueField.value,
                            showError = tMinValueField.showError,
                            errorMessage = tMinValueField.errorMessage,
                            keyboardController = keyboardController
                        ) {
                            settingsViewModel.updateSettings(it, SettingsType.MIN_T)
                        }
                        InputNumberField(
                            modifier = Modifier.weight(1f),
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
                    }
                }
            }
        }
    }
}
