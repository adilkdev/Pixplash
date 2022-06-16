package com.adil.pixplash.di.module

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.di.FragmentScope
import com.adil.pixplash.ui.home.collection.fragment.CollectionAdapter
import com.adil.pixplash.ui.home.collection.fragment.CollectionViewModel
import com.adil.pixplash.ui.home.explore.ExploreAdapter
import com.adil.pixplash.ui.home.explore.ExploreEventsListener
import com.adil.pixplash.ui.home.explore.ExploreEventsListenerImpl
import com.adil.pixplash.ui.home.explore.ExploreViewModel
import com.adil.pixplash.ui.home.search.collection.SearchCollectionAdapter
import com.adil.pixplash.ui.home.search.collection.SearchCollectionViewModel
import com.adil.pixplash.ui.home.search.photo.SearchPhotoAdapter
import com.adil.pixplash.ui.home.search.photo.SearchPhotoViewModel
import com.adil.pixplash.utils.ViewModelProviderFactory
import com.adil.pixplash.utils.dispatcher.CoroutineDispatcherProvider
import com.adil.pixplash.utils.network.NetworkHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import kotlinx.coroutines.CompletableJob

@Module
@InstallIn(FragmentComponent::class)
class FragmentModule() {

    @Provides
    fun provideExploreFragmentEventListenerImplementation(
        exploreViewModel: ExploreViewModel,
        exploreAdapter: ExploreAdapter
    ) : ExploreEventsListener = ExploreEventsListenerImpl(exploreViewModel, exploreAdapter)

    @Provides
    fun provideExploreViewModel(
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
        networkHelper: NetworkHelper,
        photoRepository: PhotoRepository,
        @FragmentScope fragment: Fragment
    ): ExploreViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(ExploreViewModel::class) {
                ExploreViewModel(coroutineDispatcherProvider, networkHelper, photoRepository)
            })[ExploreViewModel::class.java]

    @Provides
    fun provideCollectionViewModel(
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
        networkHelper: NetworkHelper,
        photoRepository: PhotoRepository,
        @FragmentScope fragment: Fragment
    ): CollectionViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(CollectionViewModel::class) {
                CollectionViewModel(
                    coroutineDispatcherProvider,
                    networkHelper,
                    photoRepository
                )
            })[CollectionViewModel::class.java]

    @Provides
    fun provideSearchPhotoViewModel(
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
        networkHelper: NetworkHelper,
        photoRepository: PhotoRepository,
        @FragmentScope fragment: Fragment
    ): SearchPhotoViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(SearchPhotoViewModel::class) {
                SearchPhotoViewModel(coroutineDispatcherProvider, networkHelper, photoRepository)
            })[SearchPhotoViewModel::class.java]

    @Provides
    fun provideSearchCollectionViewModel(
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
        networkHelper: NetworkHelper,
        photoRepository: PhotoRepository,
        @FragmentScope fragment: Fragment
    ): SearchCollectionViewModel =
        ViewModelProviders.of(fragment,
            ViewModelProviderFactory(SearchCollectionViewModel::class) {
                SearchCollectionViewModel(
                    coroutineDispatcherProvider,
                    networkHelper,
                    photoRepository
                )
            })[SearchCollectionViewModel::class.java]

    @Provides
    fun provideExploreAdapter(job: CompletableJob, @FragmentScope fragment: Fragment) =
        ExploreAdapter(context = fragment.context!!, job = job)

    @Provides
    fun provideSearchPhotoAdapter(job: CompletableJob, @FragmentScope fragment: Fragment) =
        SearchPhotoAdapter(context = fragment.context!!, job = job)

    @Provides
    fun provideCollectionAdapter(job: CompletableJob, @FragmentScope fragment: Fragment) =
        CollectionAdapter(context = fragment.context!!, job = job)

    @Provides
    fun provideSearchCollectionAdapter(job: CompletableJob, @FragmentScope fragment: Fragment) =
        SearchCollectionAdapter(context = fragment.context!!, job = job)

}