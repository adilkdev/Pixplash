package com.adil.pixplash.di.module

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.di.ActivityContext
import com.adil.pixplash.di.ActivityScope
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.ui.home.HomeViewModel
import com.adil.pixplash.ui.home.collection.activity.CollectionPhotoAdapter
import com.adil.pixplash.ui.home.collection.activity.CollectionPhotosViewModel
import com.adil.pixplash.ui.home.image_detail.ImageDetailAdapter
import com.adil.pixplash.ui.home.image_detail.ImageDetailViewModel
import com.adil.pixplash.utils.ViewModelProviderFactory
import com.adil.pixplash.utils.dispatcher.CoroutineDispatcherProvider
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CompletableJob

@Module
@InstallIn(ActivityComponent::class)
class ActivityModule() {

    @Provides
    fun provideHomeViewModel(
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
        networkHelper: NetworkHelper,
        @ActivityContext activity: FragmentActivity
    ) : HomeViewModel =
        ViewModelProviders.of(activity,
            ViewModelProviderFactory(HomeViewModel::class) {
                HomeViewModel(coroutineDispatcherProvider, networkHelper)
            })[HomeViewModel::class.java]

    @Provides
    fun provideImageDetailViewModel(
       coroutineDispatcherProvider: CoroutineDispatcherProvider,
        networkHelper: NetworkHelper,
        photoRepository: PhotoRepository,
        @ActivityContext activity: FragmentActivity
    ) : ImageDetailViewModel =
        ViewModelProviders.of(activity,
            ViewModelProviderFactory(ImageDetailViewModel::class) {
                ImageDetailViewModel(
                    coroutineDispatcherProvider,
                    networkHelper,
                    photoRepository
                )
            }).get(ImageDetailViewModel::class.java)

    @Provides
    fun provideImageDetailAdapter(job: CompletableJob, @ActivityContext activity: FragmentActivity) =
        ImageDetailAdapter(activity, job)

    @Provides
    fun provideCollectionPhotoAdapter(job: CompletableJob, @ActivityContext activity: FragmentActivity) =
        CollectionPhotoAdapter(
            context = activity,
            job = job
        )

    @Provides
    fun provideCollectionPhotosViewModel(
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
        networkHelper: NetworkHelper,
        photoRepository: PhotoRepository,
        @ActivityContext activity: FragmentActivity
    ) : CollectionPhotosViewModel =
        ViewModelProviders.of(activity,
            ViewModelProviderFactory(CollectionPhotosViewModel::class) {
                CollectionPhotosViewModel(
                    coroutineDispatcherProvider,
                    networkHelper,
                    photoRepository
                )
            }).get(CollectionPhotosViewModel::class.java)

}