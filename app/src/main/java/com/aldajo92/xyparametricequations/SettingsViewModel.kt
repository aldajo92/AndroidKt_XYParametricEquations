package com.aldajo92.xyparametricequations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldajo92.xyparametricequations.domain.SettingsAnimation
import com.aldajo92.xyparametricequations.domain.SettingsType
import com.aldajo92.xyparametricequations.repositories.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: DataRepository<SettingsType, SettingsAnimation>
) : ViewModel() {

    private val settingsEquationUIStateFlow = settingsRepository.getSettingsChangedFlow()

    private val _minField = MutableStateFlow(SettingsUIField("-10"))
    val minField: StateFlow<SettingsUIField> = _minField

    private val _maxField = MutableStateFlow(SettingsUIField("10"))
    val maxField: StateFlow<SettingsUIField> = _maxField

    private val _timeField = MutableStateFlow(SettingsUIField("5000"))
    val timeField: StateFlow<SettingsUIField> = _timeField

    private val _pathField = MutableStateFlow(SettingsUIField(value = "20", enabled = true))
    val showPath: StateFlow<SettingsUIField> = _pathField

    init {
        viewModelScope.launch {
            settingsEquationUIStateFlow.collect {
                _minField.value = SettingsUIField(it.tMin.toString())
                _maxField.value = SettingsUIField(it.tMax.toString())
                _timeField.value = SettingsUIField(it.timeDurationMillis.toString())
                // _pathField.value = SettingsUIField(value = "20", enabled = it.showPath)
            }
        }
    }

    val enableButtonStateFlow = combine(
        settingsEquationUIStateFlow,
        _minField,
        _maxField,
        _timeField
    ) { settingsEquation, minField, maxField, timeField ->
        val containsErrors = minField.showError || maxField.showError
        val tMinEquals = settingsEquation.tMin == minField.value.toFloatOrNull()
        val tMaxEquals = settingsEquation.tMax == maxField.value.toFloatOrNull()
        val timeFieldEquals = settingsEquation.timeDurationMillis == timeField.value.toIntOrNull()
        !containsErrors && (!tMinEquals || !tMaxEquals || !timeFieldEquals)
    }

    fun updateSettings(value: String, settingsType: SettingsType) {
        when (settingsType) {
            SettingsType.MIN_T -> {
                val min = value.toFloatOrNull()
                val max = _maxField.value.value.toFloatOrNull() ?: Float.MAX_VALUE
                val showError = min == null || min >= max
                _minField.value = SettingsUIField(
                    value = value,
                    showError = showError,
                    errorMessage = if (showError) "Invalid number" else ""
                )
            }
            SettingsType.MAX_T -> {
                val min = _minField.value.value.toFloatOrNull() ?: Float.MIN_VALUE
                val max = value.toFloatOrNull()
                val showError = max == null || max <= min
                _maxField.value = SettingsUIField(
                    value = value,
                    showError = showError,
                    errorMessage = if (showError) "Invalid number" else ""
                )
            }
            SettingsType.TIME_DURATION -> {
                val time = value.toIntOrNull()
                val showError = time == null || time < 100
                _timeField.value = SettingsUIField(
                    value = value,
                    showError = showError,
                    errorMessage = if (showError) "Invalid time" else ""
                )
            }
            SettingsType.SHOW_PATH -> {
                val showPath = value.toBooleanStrictOrNull() == true
                _pathField.value = _pathField.value.copy(enabled = showPath)
                viewModelScope.launch {
                    // TODO: Temporal solution: Repository should save fields with a key
                    val settingsAnimation = getSettingsAnimationCurrentValues()
                    settingsRepository.saveData(SettingsType.ALL_SETTINGS, settingsAnimation)
                }
            }
            SettingsType.PATH_POINTS -> {
                val pathPoints = value.toIntOrNull()
                val showError = pathPoints == null || pathPoints < 1
                _pathField.value = _pathField.value.copy(
                    value = pathPoints?.toString() ?: "",
                    showError = showError,
                    errorMessage = if (showError) "Invalid number" else ""
                )
                viewModelScope.launch {
                    // TODO: Temporal solution: Repository should save fields with a key
                    val settingsAnimation = getSettingsAnimationCurrentValues()
                    settingsRepository.saveData(SettingsType.ALL_SETTINGS, settingsAnimation)
                }
            }
            else -> Unit
        }
    }

    fun saveData() {
        val settingsAnimation = getSettingsAnimationCurrentValues()
        viewModelScope.launch {
            settingsRepository.saveData(SettingsType.ALL_SETTINGS, settingsAnimation)
        }
    }

    private fun getSettingsAnimationCurrentValues(): SettingsAnimation {
        val min = _minField.value.value.toFloatOrNull() ?: Float.MIN_VALUE
        val max = _maxField.value.value.toFloatOrNull() ?: Float.MAX_VALUE
        val time = _timeField.value.value.toIntOrNull() ?: 5000
        val pathPoints = _pathField.value.value
        val showPath = _pathField.value.enabled
        return SettingsAnimation(
            tMin = min,
            tMax = max,
            timeDurationMillis = time,
            pathPoints = pathPoints.toIntOrNull() ?: 0,
            showPath = showPath
        )
    }
}

data class SettingsUIField(
    var value: String,
    var showError: Boolean = false,
    var errorMessage: String = "",
    var enabled: Boolean = true
)

fun SettingsAnimation.getRangeForTParameter() = (tMin ?: 0f)..(tMax ?: 0f)
