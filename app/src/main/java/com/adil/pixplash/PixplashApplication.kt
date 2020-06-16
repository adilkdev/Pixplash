package com.adil.pixplash

import android.app.Application
import android.util.Log
import com.adil.pixplash.di.component.ApplicationComponent
import com.adil.pixplash.di.component.DaggerApplicationComponent
import com.adil.pixplash.di.module.ApplicationModule

class PixplashApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        injectDependencies()
    }

    private fun injectDependencies() {
        applicationComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
        applicationComponent.inject(this)
    }

}