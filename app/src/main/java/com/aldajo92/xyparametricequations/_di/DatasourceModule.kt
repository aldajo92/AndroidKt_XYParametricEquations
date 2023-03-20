package com.aldajo92.xyparametricequations._di

import android.content.Context
import com.aldajo92.xyparametricequations.datasource.DataSourceFlow
import com.aldajo92.xyparametricequations.domain.SettingsAnimation
import com.aldajo92.xyparametricequations.framework.storage.SettingsDataSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatasourceModule {

    // TODO: Move this to a separate module
    @Provides
    @Singleton
    fun provideMoshiBuild(): Moshi = Moshi
        .Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideSettingsDatasource(
        @ApplicationContext appContext: Context,
        moshiBuild: Moshi
    ): DataSourceFlow<SettingsAnimation> = SettingsDataSource(appContext, moshiBuild)

}
