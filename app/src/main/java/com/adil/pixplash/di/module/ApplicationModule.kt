package com.adil.pixplash.di.module

import android.app.Application
import android.content.Context
import com.adil.pixplash.UnsplashApplication
import com.adil.pixplash.di.ApplicationContext
import com.adil.pixplash.utils.rx.RxScheduleProvider
import com.adil.pixplash.utils.rx.ScheduleProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class ApplicationModule (private val application: UnsplashApplication) {

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
    fun provideSchedulerProvider(): ScheduleProvider =
        RxScheduleProvider()

}