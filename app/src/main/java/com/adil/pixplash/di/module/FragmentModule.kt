package com.adil.pixplash.di.module

import androidx.lifecycle.ViewModelProviders
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.ui.base.BaseFragment
import com.adil.pixplash.ui.home.HomeViewModel
import com.adil.pixplash.ui.home.collection.fragment.CollectionAdapter
import com.adil.pixplash.ui.home.collection.fragment.CollectionViewModel
import com.adil.pixplash.ui.home.explore.ExploreAdapter
import com.adil.pixplash.ui.home.explore.ExploreViewModel
import com.adil.pixplash.utils.ViewModelProviderFactory
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CompletableJob

@Module
class FragmentModule(private val fragment: BaseFragment<*>) {

    @Provides
    fun provideHomeViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ) : HomeViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(HomeViewModel::class) {
                HomeViewModel(schedulerProvider, compositeDisposable, networkHelper)
            }).get(HomeViewModel::class.java)

    @Provides
    fun provideExploreViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        photoRepository: PhotoRepository
    ) : ExploreViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(ExploreViewModel::class) {
                ExploreViewModel(schedulerProvider, compositeDisposable, networkHelper, photoRepository)
            }).get(ExploreViewModel::class.java)

    @Provides
    fun provideCollectionViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        photoRepository: PhotoRepository
    ) : CollectionViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(CollectionViewModel::class) {
                CollectionViewModel(
                    schedulerProvider,
                    compositeDisposable,
                    networkHelper,
                    photoRepository
                )
            }).get(CollectionViewModel::class.java)

    @Provides
    fun provideExploreAdapter(job: CompletableJob) =
        ExploreAdapter(context = fragment.context!!, job = job)

    @Provides
    fun provideCollectionAdapter(job: CompletableJob) =
        CollectionAdapter(
            context = fragment.context!!,
            job = job
        )

}