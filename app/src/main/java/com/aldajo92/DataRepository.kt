package com.aldajo92

import kotlinx.coroutines.flow.Flow

interface DataRepository<T, S> {

    fun saveData(key: T, value: S)

    suspend fun getData(key: T): S

    fun getSettingsChangedFlow(): Flow<S>

}
