package com.adil.pixplash.di.component

import com.adil.pixplash.di.FragmentScope
import com.adil.pixplash.di.module.FragmentModule
import com.adil.pixplash.ui.home.collection.CollectionFragment
import com.adil.pixplash.ui.home.explore.ExploreFragment
import com.adil.pixplash.ui.home.profile.ProfileFragment
import dagger.Component
import kotlinx.coroutines.CompletableJob

@FragmentScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [FragmentModule::class]
)
interface FragmentComponent {

    fun inject(fragment: ExploreFragment)

    fun inject(fragment: CollectionFragment)

    fun inject(fragment: ProfileFragment)

}