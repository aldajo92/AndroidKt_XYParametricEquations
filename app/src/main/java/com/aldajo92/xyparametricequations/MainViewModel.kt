package com.aldajo92.xyparametricequations

import androidx.lifecycle.ViewModel
import com.aldajo92.xyparametricequations.domain.Point
import com.aldajo92.xyparametricequations.domain.SettingsAnimation
import com.aldajo92.xyparametricequations.domain.SettingsType
import com.aldajo92.xyparametricequations.equationParser.ExpressionParser
import com.aldajo92.xyparametricequations.repositories.DataRepository
import com.aldajo92.xyparametricequations.ui.model.EquationUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: DataRepository<SettingsType, SettingsAnimation>
) : ViewModel() {

    private val expressionParser: ExpressionParser = ExpressionParser()

    private val _tParameter = MutableStateFlow(0f)
    val tParameterStateFlow get() : StateFlow<Float> = _tParameter

    private val _tParameterPrevious = MutableStateFlow(0f)
    val tParameterPreviousStateFlow get() : StateFlow<Float> = _tParameterPrevious

    private val _isRunning = MutableStateFlow(false)
    val isRunningStateFlow get() : StateFlow<Boolean> = _isRunning

    private val _isPaused = MutableStateFlow(false)
    val isPausedStateFlow get() : StateFlow<Boolean> = _isPaused

    val settingsEquationFlow = settingsRepository.getSettingsChangedFlow()
        .map {
            val currentTParameter = _tParameter.value
            val tMin = it.tMin ?: 0f
            val tMax = it.tMax ?: 0f
            if (currentTParameter < tMin) _tParameter.value = tMin
            else if (currentTParameter > tMax) _tParameter.value = tMax
            it
        }

    private val _equationXUIStateFlow = MutableStateFlow(EquationUIState("t*cos(t)"))
    val equationXUIStateFlow: StateFlow<EquationUIState> = _equationXUIStateFlow

    private val _equationYUIStateFlow = MutableStateFlow(EquationUIState("t*sin(t)"))
    val equationYUIStateFlow: StateFlow<EquationUIState> = _equationYUIStateFlow

    fun evaluateInEquations(t: Float): Point {
        val valueX = evaluate(t, _equationXUIStateFlow.value.equationString)
        val valueY = evaluate(t, _equationYUIStateFlow.value.equationString)
        return Point(valueX, valueY)
    }

    private fun evaluate(
        t: Float,
        equationString: String,
        onError: (String) -> Unit = {}
    ): Float =
        try {
            val expression = equationString.replace("t", t.toString())
            expressionParser.evaluate(expression).toFloat()
        } catch (e: Exception) {
            onError(e.message ?: "Error")
            0f
        }

    fun setEquationStringX(equationString: String) {
        if (equationString.isEmpty()) {
            _equationXUIStateFlow.value = EquationUIState(
                equationString = equationString,
                showError = false,
                errorMessage = ""
            )
            return
        }
        var showError = false
        var errorMessage = ""
        evaluate(_tParameter.value, equationString) {
            showError = true
            errorMessage = it
        }
        _equationXUIStateFlow.value = EquationUIState(
            equationString = equationString,
            showError = showError,
            errorMessage = errorMessage
        )
    }

    fun setEquationStringY(equationString: String) {
        if (equationString.isEmpty()) {
            _equationYUIStateFlow.value = EquationUIState(
                equationString = equationString,
                showError = false,
                errorMessage = ""
            )
            return
        }
        var showError = false
        var errorMessage = ""
        evaluate(_tParameter.value, equationString) {
            showError = true
            errorMessage = it
        }
        _equationYUIStateFlow.value = EquationUIState(
            equationString = equationString,
            showError = showError,
            errorMessage = errorMessage
        )
    }

    fun setTParameter(t: Float) {
        _tParameterPrevious.value = _tParameter.value
        _tParameter.value = t
    }

    fun setIsRunning(isRunning: Boolean) {
        _isRunning.value = isRunning
    }

    fun setIsPaused(isPaused: Boolean) {
        _isPaused.value = isPaused
    }

}
