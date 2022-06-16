package com.adil.pixplash.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.adil.pixplash.PixplashApplication
import com.adil.pixplash.data.local.db.DatabaseService
import com.adil.pixplash.data.remote.NetworkService
import com.adil.pixplash.data.remote.Networking
import com.adil.pixplash.utils.AppConstants
import com.adil.pixplash.utils.dispatcher.CoroutineDispatcherProvider
import com.adil.pixplash.utils.dispatcher.CoroutineDispatcherProviderImpl
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.RxSchedulerProvider
import com.adil.pixplash.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideApplication(@ApplicationContext context: Context) : PixplashApplication = context as PixplashApplication

    @Provides
    fun getCoroutineDispatcherProvider() : CoroutineDispatcherProvider =
        CoroutineDispatcherProviderImpl()

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    /**
     * Needs to be checked if working correctly
     */
    @Singleton
    @Provides
    fun provideSchedulerProvider(): SchedulerProvider =
        RxSchedulerProvider()

    @Provides
    @Singleton
    fun provideNetworkService(pixplashApplication: PixplashApplication): NetworkService =
        Networking.create(
            AppConstants.BASE_URL,
            pixplashApplication.cacheDir,
            10 * 1024 * 1024 // 10MB
        )

    @Provides
    @Singleton
    fun provideDatabaseService(pixplashApplication: PixplashApplication): DatabaseService =
        Room.databaseBuilder(
            pixplashApplication, DatabaseService::class.java,
            "pixplash-db"
        ).build()

    @Provides
    fun provideCompletableJob(): CompletableJob = Job()

    @Singleton
    @Provides
    fun provideNetworkHelper(pixplashApplication: PixplashApplication): NetworkHelper = NetworkHelper(pixplashApplication)

    @Provides
    @Singleton
    fun provideSharedPreferences(pixplashApplication: PixplashApplication): SharedPreferences =
        pixplashApplication.getSharedPreferences("pixplash-prefs", Context.MODE_PRIVATE)

}