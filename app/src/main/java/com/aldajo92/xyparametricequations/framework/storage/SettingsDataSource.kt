package com.aldajo92.xyparametricequations.framework.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aldajo92.xyparametricequations.datasource.DataSourceFlow
import com.aldajo92.xyparametricequations.domain.SettingsEquation
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataSource(private val context: Context) : DataSourceFlow<SettingsEquation> {

    private val jsonAdapter = Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
        .adapter(SettingsEquation::class.java)

    private val settingDataFlow: Flow<SettingsEquation> =
        context.dataStore.data.map { preferences ->
            preferences[SETTINGS_KEY]?.let { jsonAdapter.fromJson(it) } ?: SettingsEquation()
        }

    override suspend fun saveData(data: SettingsEquation) {
        context.dataStore.edit { preferences ->
            preferences[SETTINGS_KEY] = jsonAdapter.toJson(data)
        }
    }

    override fun getDataFlow(): Flow<SettingsEquation> = settingDataFlow

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        private val SETTINGS_KEY = stringPreferencesKey("settings_equations")
    }
}
