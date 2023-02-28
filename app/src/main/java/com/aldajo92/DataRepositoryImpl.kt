package com.aldajo92

import com.aldajo92.xyparametricequations.domain.SettingsEquation
import com.aldajo92.xyparametricequations.domain.SettingsType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class DataRepositoryImpl : DataRepository<SettingsType, SettingsEquation> {

    private val _settingsEquationState = MutableStateFlow(SettingsEquation())

    override fun saveData(key: SettingsType, value: SettingsEquation) {
        val min = value.tMin
        val max = value.tMax
        _settingsEquationState.value = SettingsEquation(
            tMin = min,
            tMax = max
        )
    }

    override suspend fun getData(key: SettingsType): SettingsEquation {
        return SettingsEquation()
    }

    override fun getSettingsChangedFlow(): Flow<SettingsEquation> = _settingsEquationState

}
