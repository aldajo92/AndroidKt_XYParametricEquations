package com.aldajo92.xyparametricequations._di

import android.content.Context
import com.aldajo92.xyparametricequations.datasource.DataSourceFlow
import com.aldajo92.xyparametricequations.domain.SettingsEquation
import com.aldajo92.xyparametricequations.framework.storage.SettingsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatasourceModule {

    @Provides
    @Singleton
    fun provideSettingsDatasource(
        @ApplicationContext appContext: Context
    ): DataSourceFlow<SettingsEquation> = SettingsDataSource(appContext)

}
