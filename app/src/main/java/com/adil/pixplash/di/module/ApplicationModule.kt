package com.adil.pixplash.di.module

import android.app.Application
import android.content.Context
import com.adil.pixplash.PixplashApplication
import com.adil.pixplash.data.remote.NetworkService
import com.adil.pixplash.data.remote.Networking
import com.adil.pixplash.di.ApplicationContext
import com.adil.pixplash.utils.AppConstants
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.RxSchedulerProvider
import com.adil.pixplash.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class ApplicationModule (private val application: PixplashApplication) {

    @Singleton
    @Provides
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    @ApplicationContext
    fun provideContext(): Context = application

    /**
     * Since this function do not have @Singleton then each time CompositeDisposable is injected
     * then a new instance of CompositeDisposable will be provided
     */
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
    fun provideNetworkService(): NetworkService =
        Networking.create(
            AppConstants.BASE_URL,
            application.cacheDir,
            10 * 1024 * 1024 // 10MB
        )

    @Singleton
    @Provides
    fun provideNetworkHelper(): NetworkHelper = NetworkHelper(application)

}