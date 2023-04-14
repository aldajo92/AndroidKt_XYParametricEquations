package com.aldajo92.xyparametricequations.framework.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aldajo92.xyparametricequations.datasource.DataSourceFlow
import com.aldajo92.xyparametricequations.domain.SettingsAnimation
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsDataSource(
    private val context: Context,
    private val moshiBuild : Moshi
) : DataSourceFlow<SettingsAnimation> {

    private val jsonAdapter = moshiBuild.adapter(SettingsAnimation::class.java)

    private val settingDataFlow: Flow<SettingsAnimation> =
        context.dataStore.data.map { preferences ->
            preferences[SETTINGS_KEY]?.let { jsonAdapter.fromJson(it) } ?: SettingsAnimation()
        }

    override suspend fun saveData(data: SettingsAnimation) {
        context.dataStore.edit { preferences ->
            preferences[SETTINGS_KEY] = jsonAdapter.toJson(data)
        }
    }

    override fun getDataFlow(): Flow<SettingsAnimation> = settingDataFlow

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        private val SETTINGS_KEY = stringPreferencesKey("settings_equations")
    }
}
