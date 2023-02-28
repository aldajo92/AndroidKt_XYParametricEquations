package com.aldajo92.xyparametricequations._di

import com.aldajo92.xyparametricequations.repositories.DataRepository
import com.aldajo92.xyparametricequations.repositories.DataRepositoryImpl
import com.aldajo92.xyparametricequations.datasource.DataSourceFlow
import com.aldajo92.xyparametricequations.domain.SettingsEquation
import com.aldajo92.xyparametricequations.domain.SettingsType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [DatasourceModule::class])
object RepositoryModule {

    @Provides
    @Singleton
    fun providesSettingsDataRepository(
        settingsDatasource : DataSourceFlow<SettingsEquation>
    ): DataRepository<SettingsType, SettingsEquation> = DataRepositoryImpl(settingsDatasource)

}
