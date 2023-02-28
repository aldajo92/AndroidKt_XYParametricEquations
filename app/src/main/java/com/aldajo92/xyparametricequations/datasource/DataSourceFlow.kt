package com.aldajo92.xyparametricequations.datasource

import kotlinx.coroutines.flow.Flow

interface DataSourceFlow<T> {

    suspend fun saveData(data: T)

    fun getDataFlow(): Flow<T>

}
