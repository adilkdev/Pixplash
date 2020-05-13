package com.adil.pixplash.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.di.ActivityScope
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.ui.home.HomeViewModel
import com.adil.pixplash.ui.home.collection.CollectionPhotoAdapter
import com.adil.pixplash.ui.home.collection.CollectionPhotosViewModel
import com.adil.pixplash.ui.home.explore.ExploreAdapter
import com.adil.pixplash.ui.home.image_detail.ImageDetailAdapter
import com.adil.pixplash.ui.home.image_detail.ImageDetailViewModel
import com.adil.pixplash.utils.ViewModelProviderFactory
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CompletableJob

@Module
class ActivityModule(private val activity: BaseActivity<*>) {

    @ActivityScope
    @Provides
    fun provideContext(): Context = activity

    @Provides
    fun provideHomeViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ) : HomeViewModel =
        ViewModelProviders.of(activity,
            ViewModelProviderFactory(HomeViewModel::class) {
                HomeViewModel(schedulerProvider, compositeDisposable, networkHelper)
            }).get(HomeViewModel::class.java)

    @Provides
    fun provideImageDetailViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        photoRepository: PhotoRepository
    ) : ImageDetailViewModel =
        ViewModelProviders.of(activity,
            ViewModelProviderFactory(ImageDetailViewModel::class) {
                ImageDetailViewModel(
                    schedulerProvider,
                    compositeDisposable,
                    networkHelper,
                    photoRepository
                )
            }).get(ImageDetailViewModel::class.java)

    @Provides
    fun provideImageDetailAdapter(job: CompletableJob) =
        ImageDetailAdapter(activity, job)

    @Provides
    fun provideCollectionPhotoAdapter(job: CompletableJob) =
        CollectionPhotoAdapter(context = activity, job = job)

    @Provides
    fun provideCollectionPhotosViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        photoRepository: PhotoRepository
    ) : CollectionPhotosViewModel =
        ViewModelProviders.of(activity,
            ViewModelProviderFactory(CollectionPhotosViewModel::class) {
                CollectionPhotosViewModel(
                    schedulerProvider,
                    compositeDisposable,
                    networkHelper,
                    photoRepository
                )
            }).get(CollectionPhotosViewModel::class.java)

}