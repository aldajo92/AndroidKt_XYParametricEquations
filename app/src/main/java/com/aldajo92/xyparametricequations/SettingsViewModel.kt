package com.aldajo92.xyparametricequations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldajo92.xyparametricequations.domain.SettingsEquation
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
    private val settingsRepository: DataRepository<SettingsType, SettingsEquation>
) : ViewModel() {

    private val settingsEquationUIStateFlow = settingsRepository.getSettingsChangedFlow()

    private val _minField = MutableStateFlow(SettingsUIField("-100"))
    val minField: StateFlow<SettingsUIField> = _minField

    private val _maxField = MutableStateFlow(SettingsUIField("100"))
    val maxField: StateFlow<SettingsUIField> = _maxField

    init {
        viewModelScope.launch {
            settingsEquationUIStateFlow.collect {
                _minField.value = SettingsUIField(it.tMin.toString())
                _maxField.value = SettingsUIField(it.tMax.toString())
            }
        }
    }

    val enableButtonStateFlow = combine(
        settingsEquationUIStateFlow,
        _minField,
        _maxField
    ) { settingsEquation, minField, maxField ->
        val containsErrors = minField.showError || maxField.showError
        val tMinEquals = settingsEquation.tMin == minField.value.toFloatOrNull()
        val tMaxEquals = settingsEquation.tMax == maxField.value.toFloatOrNull()
        val result = !containsErrors && (!tMinEquals || !tMaxEquals)
        result
    }

    fun updateSettings(it: String, settingsType: SettingsType) {
        when (settingsType) {
            SettingsType.MIN_T -> {
                val min = it.toFloatOrNull()
                val max = _maxField.value.value.toFloatOrNull() ?: Float.MAX_VALUE
                val showError = min == null || min >= max
                _minField.value = SettingsUIField(
                    value = it,
                    showError = showError,
                    errorMessage = if (showError) "Invalid number" else ""
                )
            }
            SettingsType.MAX_T -> {
                val min = _minField.value.value.toFloatOrNull() ?: Float.MIN_VALUE
                val max = it.toFloatOrNull()
                val showError = max == null || max <= min
                _maxField.value = SettingsUIField(
                    value = it,
                    showError = showError,
                    errorMessage = if (showError) "Invalid number" else ""
                )
            }
            else -> Unit
        }
    }

    fun saveData() {
        val min = _minField.value.value.toFloatOrNull() ?: Float.MIN_VALUE
        val max = _maxField.value.value.toFloatOrNull() ?: Float.MAX_VALUE
        val settingsEquation = SettingsEquation(
            tMin = min,
            tMax = max
        )
        viewModelScope.launch {
            settingsRepository.saveData(SettingsType.ALL_SETTINGS, settingsEquation)
        }
    }
}

data class SettingsUIField(
    var value: String,
    var showError: Boolean = false,
    var errorMessage: String = ""
)

fun SettingsEquation.getRangeForTParameter() = tMin..tMax
