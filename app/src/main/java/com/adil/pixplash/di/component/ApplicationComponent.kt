package com.adil.pixplash.di.component

import android.app.Application
import android.content.Context
import com.adil.pixplash.PixplashApplication
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.di.ApplicationContext
import com.adil.pixplash.di.module.ApplicationModule
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.SchedulerProvider
import dagger.Component
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(app: PixplashApplication)

    fun getApplication(): Application

    @ApplicationContext
    fun getContext(): Context

    fun getPhotoRepository(): PhotoRepository

    fun getSchedulerProvider(): SchedulerProvider

    fun getCompositeDisposable(): CompositeDisposable

    fun getJob(): CompletableJob

    fun getNetworkHelper(): NetworkHelper


}