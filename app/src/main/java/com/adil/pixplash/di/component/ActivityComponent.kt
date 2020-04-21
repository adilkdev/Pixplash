package com.adil.pixplash.di.component

import android.app.Activity
import com.adil.pixplash.di.ActivityScope
import com.adil.pixplash.di.module.ActivityModule
import com.adil.pixplash.ui.home.HomeActivity
import dagger.Component

@ActivityScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [ActivityModule::class]
)
interface ActivityComponent {

    fun inject(activity: HomeActivity)

}