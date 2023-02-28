package com.aldajo92.xyparametricequations

import androidx.lifecycle.ViewModel
import com.aldajo92.DataRepository
import com.aldajo92.DataRepositoryImpl
import com.aldajo92.xyparametricequations.domain.SettingsEquation
import com.aldajo92.xyparametricequations.domain.SettingsType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
) : ViewModel() {

    private val dataRepository: DataRepository<SettingsType, SettingsEquation> = DataRepositoryImpl()

    private val _settingsEquationUIStateFlow = MutableStateFlow(SettingsEquation())
    val settingsEquationUIStateFlow: StateFlow<SettingsEquation> = _settingsEquationUIStateFlow

    private val _minField = MutableStateFlow(SettingsUIField("-100"))
    val minField: StateFlow<SettingsUIField> = _minField

    private val _maxField = MutableStateFlow(SettingsUIField("100"))
    val maxField: StateFlow<SettingsUIField> = _maxField

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
        }
    }
}

data class SettingsUIField(
    var value: String,
    var showError: Boolean = false,
    var errorMessage: String = ""
)

fun SettingsEquation.getRangeForTParameter() = tMin..tMax
