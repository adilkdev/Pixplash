package com.adil.pixplash.di.module

import androidx.lifecycle.ViewModelProviders
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.ui.home.HomeViewModel
import com.adil.pixplash.ui.home.explore.ExploreViewModel
import com.adil.pixplash.utils.ViewModelProviderFactory
import com.adil.pixplash.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class FragmentModule(private val fragment: BaseFragment<*>) {

    @Provides
    fun provideHomeViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable
    ) : HomeViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(HomeViewModel::class) {
                HomeViewModel(schedulerProvider, compositeDisposable)
            }).get(HomeViewModel::class.java)

    @Provides
    fun provideExploreViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        photoRepository: PhotoRepository
    ) : ExploreViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(ExploreViewModel::class) {
                ExploreViewModel(schedulerProvider, compositeDisposable, photoRepository)
            }).get(ExploreViewModel::class.java)

}