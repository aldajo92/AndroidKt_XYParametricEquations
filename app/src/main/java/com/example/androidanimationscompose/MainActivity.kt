package com.example.androidanimationscompose

import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidanimationscompose.ui.theme.AndroidAnimationsComposeTheme
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt


/* TODO:
* Support DarkMode (Done)
* Add resolution for x, y steps in the plane (Done)
* Add input text for equations. (Done)
* Add slider for parameter. (Done)
* Add configuration section.
* Add animation for parameter.
*  */

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalLifecycleComposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidAnimationsComposeTheme {
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

                    Column(modifier = Modifier.fillMaxSize()) {
                        RenderXYBoardUI(
                            modifier = Modifier.weight(1f),
                            resolution = resolution,
                            tParameter = tParameter,
                            parametricEquation = {
                                viewModel.evaluateInEquation(it)
                            },
                            resolutionChange = {
                                resolution = it
                            }
                        )
                        Column(Modifier.fillMaxWidth()) {
                            InputVariableField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                textTitle = "X(t)=",
                                textValue = equationXUIState.equationString,
                                onValueChange = {
                                    viewModel.setEquationStringX(it)
                                },
                                showError = equationXUIState.showError,
                                errorMessage = equationXUIState.errorMessage
                            )
                            InputVariableField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp)
                                    .padding(bottom = 8.dp),
                                textTitle = "Y(t)=",
                                textValue = equationYUIState.equationString,
                                onValueChange = {
                                    viewModel.setEquationStringY(it)
                                },
                                showError = equationYUIState.showError,
                                errorMessage = equationYUIState.errorMessage
                            )
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(8.dp),
                                    text = "t="
                                )
                                SimpleContinuousSlider(
                                    modifier = Modifier.weight(1f),
                                    range = -10f..10f,
                                ) {
                                    viewModel.setTParameter(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InputVariableField(
    modifier: Modifier = Modifier,
    textTitle: String = "X",
    textValue: String = "",
    showError: Boolean = false,
    errorMessage: String = "Error",
    onValueChange: (String) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth(),
        value = textValue,
        label = { if (showError) Text(errorMessage) else Text(textTitle) },
        onValueChange = onValueChange,
        isError = showError,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() })
    )
}

@Composable
fun SimpleContinuousSlider(
    modifier: Modifier = Modifier,
    range: ClosedFloatingPointRange<Float> = 0f..100f,
    startValue: Float = (range.start + range.endInclusive) / 2f,
    onValueChanged: (Float) -> Unit = {}
) {
    var selection by remember { mutableStateOf(startValue) }

    Slider(
        modifier = modifier,
        value = selection,
        valueRange = range,
        onValueChange = {
            selection = it
            onValueChanged(it)
        },
        colors = SliderDefaults.colors(
            activeTrackColor = Color.Transparent,
            inactiveTrackColor = MaterialTheme.colors.primary.copy(alpha = 0.2f)
        )
    )
}

@Composable
fun RenderXYBoardUI(
    modifier: Modifier = Modifier,
    resolution: Float = 50f,
    tParameter: Float = 0f,
    parametricEquation: (Float) -> Point = { Point(it, it) },
    resolutionChange: (Float) -> Unit = {}
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
        AnimatedCircleV2(
            modifier = Modifier.fillMaxSize(),
            pointOrigin = newOriginOffset,
            step = step,
            circleColor = Color.Red,
            lineColor = MaterialTheme.colors.onBackground,
            textColor = MaterialTheme.colors.onBackground,
            tParameter = tParameter,
            parametricEquation = parametricEquation
        )
        SimpleContinuousSlider(
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = 90f
                    transformOrigin = TransformOrigin(0f, 0f)
                }
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(
                        Constraints(
                            minWidth = constraints.minHeight,
                            maxWidth = constraints.maxHeight,
                            minHeight = constraints.minWidth,
                            maxHeight = constraints.maxHeight,
                        )
                    )
                    layout(placeable.height, placeable.width) {
                        placeable.place(0, -placeable.width + 150)
                    }
                }
                .align(Alignment.TopStart),
            range = 12f..100f,
            startValue = resolution
        ) {
            resolutionChange(it)
        }
//        {
////            val circleInitialCenter = Point(10f, 10f)
////            val pDirector = Point(-1f, 1f)
////            rectLine(circleInitialCenter, pDirector, tParameter)
//            parabola(it)
//        }
    }
}

fun Float.convertPixelsToDp(): Dp {
    val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
    val dp = this / (metrics.densityDpi / 160f)
    return dp.roundToInt().dp
}

@Composable
fun AnimatedCircleV2(
    modifier: Modifier = Modifier,
    circleColor: Color = Color.Blue,
    lineColor: Color = Color.Blue,
    textColor: Color = Color.Black,
    pointOrigin: Offset,
    step: Float,
    tParameter: Float = 0f,
    parametricEquation: (Float) -> Point = { Point(it, it) }
) {

//    TODO: Pending to move it as a separate feature
//    val tParameterStart = 0f
//    val tParameterEnd = 5f

//    var isRunning by remember { mutableStateOf(false) }

//    var tParameter by remember { mutableStateOf(tParameterStart) }
//    val tAnimation2 = remember { Animatable(tParameter, Float.VectorConverter) }
//    val animationSpec = remember {
//        InfiniteRepeatableSpec<Float>(
//            tween(durationMillis = 5000, easing = LinearEasing)
//        )
//    }
//    LaunchedEffect(isRunning) {
//        if (isRunning) {
//            tAnimation2.snapTo(tParameterStart)
//            tAnimation2.animateTo(
//                targetValue = tParameterEnd,
//                animationSpec = animationSpec
//            ) {
//                tParameter = this.value
//            }
//        }
//    }

    val pathEffect = remember {
        PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    val circleCenter = parametricEquation(tParameter)

    val circleCenterOffset = circleCenter
        .invertYaxis()
        .toOffset(step)
        .translate(pointOrigin)

    if (step > 0) Canvas(modifier = modifier.fillMaxSize()) {
        drawCircle(
            color = circleColor,
            radius = 50f,
            center = circleCenterOffset,
            style = Stroke(5f)
        )
        drawLine(
            color = lineColor,
            start = Offset(pointOrigin.x, circleCenterOffset.y),
            end = circleCenterOffset,
            pathEffect = pathEffect
        )
        drawLine(
            color = lineColor,
            start = Offset(circleCenterOffset.x, pointOrigin.y),
            end = circleCenterOffset,
            pathEffect = pathEffect
        )
    }
    Text(
        modifier = Modifier.padding(10.dp),
        text = "t: ${String.format("%.2f", tParameter)}",
        color = textColor
    )
//    TODO: Pending to move it as a separate feature
//    Button(
//        modifier = Modifier
//            .padding(10.dp)
//            .align(Alignment.TopEnd),
//        onClick = {
//            isRunning = !isRunning
//        }
//    ) {
//        Text(if (isRunning) "Stop Animation" else "Start Animation")
//    }
}


fun rectLine(p0: Point, pointDirector: Point, t: Float): Point = p0 + (pointDirector * t)
fun parabola(t: Float): Point = Point(t * 20f, (t * 5f).pow(2))
