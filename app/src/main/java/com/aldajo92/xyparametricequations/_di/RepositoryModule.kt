package com.aldajo92.xyparametricequations._di

import com.aldajo92.xyparametricequations.repositories.DataRepository
import com.aldajo92.xyparametricequations.repositories.DataRepositoryImpl
import com.aldajo92.xyparametricequations.datasource.DataSourceFlow
import com.aldajo92.xyparametricequations.domain.SettingsAnimation
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
        settingsDatasource : DataSourceFlow<SettingsAnimation>
    ): DataRepository<SettingsType, SettingsAnimation> = DataRepositoryImpl(settingsDatasource)

}
