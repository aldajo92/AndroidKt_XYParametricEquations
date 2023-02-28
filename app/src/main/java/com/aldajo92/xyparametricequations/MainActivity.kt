package com.aldajo92.xyparametricequations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldajo92.xyparametricequations.domain.Point
import com.aldajo92.xyparametricequations.domain.SettingsEquation
import com.aldajo92.xyparametricequations.domain.SettingsType
import com.aldajo92.xyparametricequations.ui.AnimatedCircleComponent
import com.aldajo92.xyparametricequations.ui.SimpleContinuousSlider
import com.aldajo92.xyparametricequations.ui.showAsBottomSheet
import com.aldajo92.xyparametricequations.ui.theme.XYParametricEquationsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.min


/* TODO:
* Support DarkMode (Done)
* Add resolution for x, y steps in the plane (Done)
* Add input text for equations. (Done)
* Add slider for parameter. (Done)
* Add configuration section. (Done)
* Add animation for t parameter.
* Enable, disable vector for point.
*  */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    @OptIn(ExperimentalLifecycleComposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XYParametricEquationsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var resolution by remember { mutableStateOf(50f) }

                    val tParameter by viewModel.tParameterStateFlow.collectAsStateWithLifecycle()

                    val equationXUIState by viewModel.equationXUIStateFlow.collectAsStateWithLifecycle(
                        EquationUIState()
                    )
                    val equationYUIState by viewModel.equationYUIStateFlow.collectAsStateWithLifecycle(
                        EquationUIState()
                    )

                    val settings by viewModel.settingsEquationFlow.collectAsStateWithLifecycle(
                        initialValue = SettingsEquation(),
                        lifecycle = lifecycle
                    )

                    Column(modifier = Modifier.fillMaxSize()) {
                        RenderXYBoardUI(
                            modifier = Modifier.weight(1f),
                            resolution = resolution,
                            tParameter = tParameter,
                            parametricEquation = {
                                viewModel.evaluateInEquation(it)
                            }
                        )
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colors.background)
                        ) {
                            InputEquationsRow(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .fillMaxWidth(),
                                equationXUIState = equationXUIState,
                                equationYUIState = equationYUIState,
                                onEquationExpressionXChange = {
                                    viewModel.setEquationStringX(it)
                                },
                                onEquationExpressionYChange = {
                                    viewModel.setEquationStringY(it)
                                }
                            )
                            SliderForTParameter(
                                modifier = Modifier.fillMaxWidth(),
                                range = settings.getRangeForTParameter(),
                                onSettingsClicked = {
                                    showSettingsBottomSheet(
                                        defaultResolution = resolution,
                                        resolutionChange = {
                                            resolution = it
                                        }
                                    )
                                }
                            ) {
                                viewModel.setTParameter(it)
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalComposeUiApi::class)
    private fun showSettingsBottomSheet(
        resolutionChange: (Float) -> Unit = {},
        defaultResolution: Float = 50f
    ) {
        this.showAsBottomSheet { dismissDialog ->
            var currentResolution by remember { mutableStateOf(defaultResolution) }

            val tMinValueField by settingsViewModel.minField.collectAsStateWithLifecycle()
            val tMaxValueField by settingsViewModel.maxField.collectAsStateWithLifecycle()

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
                            Text(
                                text = "Resolution: ${String.format("%.2f", currentResolution)}",
                                modifier = Modifier
                            )
                            SimpleContinuousSlider(
                                modifier = Modifier.fillMaxWidth(),
                                range = 12f..100f,
                                startValue = defaultResolution
                            ) {
                                resolutionChange(it)
                                currentResolution = it
                            }
                            Text(
                                text = "Parameter t:",
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
                                    textTitle = "Min:",
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
                                    textTitle = "Max:",
                                    textValue = tMaxValueField.value,
                                    showError = tMaxValueField.showError,
                                    errorMessage = tMaxValueField.errorMessage,
                                    keyboardController = keyboardController
                                ) {
                                    settingsViewModel.updateSettings(it, SettingsType.MAX_T)
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
}

@Composable
fun InputEquationsRow(
    modifier: Modifier = Modifier,
    equationXUIState: EquationUIState = EquationUIState(),
    equationYUIState: EquationUIState = EquationUIState(),
    onEquationExpressionXChange: (String) -> Unit = {},
    onEquationExpressionYChange: (String) -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InputStringField(
            modifier = Modifier
                .weight(1f),
            textTitle = "x(t)=",
            textValue = equationXUIState.equationString,
            onValueChange = onEquationExpressionXChange,
            showError = equationXUIState.showError,
            errorMessage = equationXUIState.errorMessage
        )
        InputStringField(
            modifier = Modifier
                .weight(1f),
            textTitle = "y(t)=",
            textValue = equationYUIState.equationString,
            onValueChange = onEquationExpressionYChange,
            showError = equationYUIState.showError,
            errorMessage = equationYUIState.errorMessage
        )
    }
}

@Composable
fun SliderForTParameter(
    modifier: Modifier = Modifier,
    range: ClosedFloatingPointRange<Float> = 0f..100f,
    onSettingsClicked: () -> Unit = {},
    onValueChanged: (Float) -> Unit = {}
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(8.dp),
            text = "t="
        )
        SimpleContinuousSlider(
            modifier = Modifier.weight(1f),
            range = range,
            onValueChanged = onValueChanged
        )
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically)
                .clickable { onSettingsClicked() },
            imageVector = Icons.Default.Settings,
            tint = MaterialTheme.colors.onBackground,
            contentDescription = "Settings"
        )
    }
}

@Composable
fun RenderXYBoardUI(
    modifier: Modifier = Modifier,
    resolution: Float = 50f,
    tParameter: Float = 0f,
    parametricEquation: (Float) -> Point = { Point(it, it) }
) {
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }
    var step by remember { mutableStateOf(0f) }

    Box(
        modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    width = placeable.width.toFloat()
                    height = placeable.height.toFloat()
                    step = min(width, height) / resolution
                    placeable.placeRelative(0, 0)
                }
            }
    ) {
        val screenBottomRightCorner = Offset(width, height)
        val newOriginOffset = screenBottomRightCorner / 2f
        XYAxisBoard(
            modifier = Modifier.fillMaxSize(),
            pointOrigin = newOriginOffset,
            width = width,
            height = height,
            step = step,
            colorAxisX = MaterialTheme.colors.onBackground,
            colorAxisY = MaterialTheme.colors.onBackground
        )
        AnimatedCircleComponent(
            modifier = Modifier.fillMaxSize(),
            pointOrigin = newOriginOffset,
            step = step,
            circleColor = Color.Red,
            lineColor = MaterialTheme.colors.onBackground,
            textColor = MaterialTheme.colors.onBackground,
            tParameter = tParameter,
            parametricEquation = parametricEquation
        )
    }
}
