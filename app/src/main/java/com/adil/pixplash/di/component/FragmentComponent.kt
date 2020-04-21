package com.adil.pixplash.di.component

import com.adil.pixplash.di.FragmentScope
import com.adil.pixplash.di.module.FragmentModule
import com.adil.pixplash.ui.home.HomeFragment
import dagger.Component

@FragmentScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [FragmentModule::class]
)
interface FragmentComponent {

    fun inject(fragment: HomeFragment)

}