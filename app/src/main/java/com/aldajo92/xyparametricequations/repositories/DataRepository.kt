package com.aldajo92.xyparametricequations.repositories

import kotlinx.coroutines.flow.Flow

interface DataRepository<T, S> {

    suspend fun saveData(key: T, value: S)

    suspend fun getData(key: T): S

    fun getSettingsChangedFlow(): Flow<S>

}
