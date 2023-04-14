package com.aldajo92.xyparametricequations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aldajo92.xyparametricequations.domain.SettingsAnimation
import com.aldajo92.xyparametricequations.ui.InputEquationsRow
import com.aldajo92.xyparametricequations.ui.SliderForTParameter
import com.aldajo92.xyparametricequations.ui.XYMainUI
import com.aldajo92.xyparametricequations.ui.showSettingsBottomSheet
import com.aldajo92.xyparametricequations.ui.theme.XYParametricEquationsTheme
import dagger.hilt.android.AndroidEntryPoint

/* TODO:
* Support DarkMode (Done)
* Add resolution for x, y steps in the plane (Done)
* Add input text for equations. (Done)
* Add slider for parameter. (Done)
* Add configuration section. (Done)
* Add animation for t parameter. (Done).
* Add time duration for animation. (Done)
* Show path when t parameter increases. (Done)
* Done button on settings bottom sheet.
* Modify max path points in settings.
* Save circle size in settings.
* Allow to show circle size.
* Add button to just center the origin.
* Show dotted path or solid path.
* Handle SettingsAnimation model new parameters, to avoid crashes.
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
                    var minUnitsAxisScreen by remember { mutableStateOf(50f) } // TODO: Remove this
                    var circleSizeUnits by remember { mutableStateOf(1.75f) }
                    var offsetOrigin by remember { mutableStateOf(Offset.Zero) }

                    val tParameter by viewModel.tParameterStateFlow.collectAsStateWithLifecycle()
                    val tParameterPrevious by viewModel.tParameterPreviousStateFlow.collectAsStateWithLifecycle()

                    val settings by viewModel.settingsEquationFlow.collectAsStateWithLifecycle(
                        SettingsAnimation()
                    )

                    val tParameterStart = settings.tMin ?: 0f
                    val tParameterEnd = settings.tMax ?: 1f
                    val timeDurationMillis = settings.timeDurationMillis ?: 1000
                    val showPath = settings.showPath ?: false
                    val maxPathPoints = settings.pathPoints ?: 100

                    // Animation ////////////////////////////////////////////////////////////////////////////
                    val isRunning by viewModel.isRunningStateFlow.collectAsStateWithLifecycle()

                    val tAnimation = remember { Animatable(tParameterStart, Float.VectorConverter) }
                    val animationSpec = remember {
                        InfiniteRepeatableSpec<Float>(
                            tween(durationMillis = timeDurationMillis, easing = LinearEasing)
                        )
                    }
                    LaunchedEffect(isRunning) {
                        if (isRunning) {
                            tAnimation.snapTo(tParameterStart)
                            tAnimation.animateTo(
                                targetValue = tParameterEnd,
                                animationSpec = animationSpec
                            ) {
                                viewModel.setTParameter(this.value)
                            }
                        }
                    }
                    // End Animation //////////////////////////////////////////////////////////////////////

                    Column(modifier = Modifier.fillMaxSize()) {
                        XYMainUI(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            minUnitsAxisScreen = minUnitsAxisScreen,
                            circleSizeInUnits = circleSizeUnits,
                            tParameter = tParameter,
                            offsetOrigin = offsetOrigin,
                            isDragEnabled = true,
                            // TODO: Use another variable to show path, and other to reset
                            showPath = showPath && isRunning && tParameter > tParameterPrevious,
                            maxPathPoints = maxPathPoints,
                            onOffsetChange = { offsetChange ->
                                offsetOrigin += offsetChange
                            },
                            onZoomChange = { zoomChange ->
                                minUnitsAxisScreen /= zoomChange
                            },
                            evaluateCircleInParametricEquation = {
                                viewModel.evaluateInEquations(it)
                            },
                            topContent = {
                                TopContent(
                                    tParameter,
                                    isRunning = isRunning,
                                    onPlayClicked = {
                                        viewModel.setIsRunning(!isRunning)
                                    },
                                    centerButtonClicked = {
                                        offsetOrigin = Offset.Zero
                                        minUnitsAxisScreen = 50f
                                    }
                                )
                            }
                        )
                        BottomInputEquations(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colors.background),
                            onSettingsClicked = {
                                showSettingsBottomSheet(
//                                    settingsViewModel,
                                    circleSizeChange = {
                                        circleSizeUnits = it
                                    },
//                                    defaultCircleSize = circleSizeUnits
                                )
                            },
                            sliderChange = {
                                viewModel.setTParameter(it)
                            },
                            tRange = settings.getRangeForTParameter(),
                            tParameter = tParameter,
                            viewModel = viewModel,
                            enableInput = !isRunning
                        )
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.setIsRunning(false)
    }

    override fun onStop() {
        super.onStop()
        viewModel.setIsRunning(false)
    }

    @OptIn(ExperimentalLifecycleComposeApi::class)
    @Composable
    fun BottomInputEquations(
        modifier: Modifier = Modifier,
        onSettingsClicked: () -> Unit = {},
        sliderChange: (Float) -> Unit = {},
        tRange: ClosedFloatingPointRange<Float>,
        tParameter: Float = 0f,
        enableInput: Boolean = true,
        viewModel: MainViewModel
    ) {
        val equationXUIState by viewModel.equationXUIStateFlow.collectAsStateWithLifecycle(
            EquationUIState()
        )
        val equationYUIState by viewModel.equationYUIStateFlow.collectAsStateWithLifecycle(
            EquationUIState()
        )

        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
        ) {
            InputEquationsRow(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                equationXUIState = equationXUIState,
                equationYUIState = equationYUIState,
                enableInputText = enableInput,
                onEquationExpressionXChange = {
                    viewModel.setEquationStringX(it)
                },
                onEquationExpressionYChange = {
                    viewModel.setEquationStringY(it)
                }
            )
            SliderForTParameter(
                modifier = Modifier.fillMaxWidth(),
                range = tRange,
                tParameter = tParameter,
                onSettingsClicked = onSettingsClicked,
                onValueChanged = sliderChange,
                enableSlider = enableInput
            )
        }
    }
}

@Composable
fun BoxScope.TopContent(
    tParameter: Float = 0f,
    isRunning: Boolean = false,
    onPlayClicked: () -> Unit = {},
    centerButtonClicked: () -> Unit = {}
) {
    Text(
        modifier = Modifier.padding(10.dp),
        text = "t: ${String.format("%.2f", tParameter)}",
        color = MaterialTheme.colors.onBackground
    )
    Row(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            modifier = Modifier
                .clickable { centerButtonClicked() },
            painter = painterResource(R.drawable.icon_center_focus),
            tint = MaterialTheme.colors.onBackground,
            contentDescription = "Center origin"
        )
        Icon(
            modifier = Modifier
                .clickable { onPlayClicked() },
            painter = painterResource(if (isRunning) R.drawable.ic_stop else R.drawable.ic_play_arrow),
            tint = Color.Green,
            contentDescription = (if (isRunning) "Stop" else "Play")
        )
        // TODO Add icon to enable zoom here
    }

}
