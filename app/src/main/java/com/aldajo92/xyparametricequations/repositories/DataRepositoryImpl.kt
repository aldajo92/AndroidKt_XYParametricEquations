package com.aldajo92.xyparametricequations.repositories

import com.aldajo92.xyparametricequations.datasource.DataSourceFlow
import com.aldajo92.xyparametricequations.domain.SettingsEquation
import com.aldajo92.xyparametricequations.domain.SettingsType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    private val settingsDatasource: DataSourceFlow<SettingsEquation>
) : DataRepository<SettingsType, SettingsEquation> {
    

    override suspend fun saveData(key: SettingsType, value: SettingsEquation) {
        settingsDatasource.saveData(value)
    }

    // TODO: Remove this method
    override suspend fun getData(key: SettingsType): SettingsEquation {
        return settingsDatasource.getDataFlow().last()
    }

    override fun getSettingsChangedFlow(): Flow<SettingsEquation> = settingsDatasource.getDataFlow()

}
