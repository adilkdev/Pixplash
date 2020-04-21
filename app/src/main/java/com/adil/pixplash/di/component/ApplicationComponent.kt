package com.adil.pixplash.di.component

import android.app.Application
import android.content.Context
import com.adil.pixplash.PixplashApplication
import com.adil.pixplash.di.ApplicationContext
import com.adil.pixplash.di.module.ApplicationModule
import dagger.Component
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(app: PixplashApplication)

    fun getApplication(): Application

    @ApplicationContext
    fun getContext(): Context

    fun getCompositeDisposable(): CompositeDisposable


}