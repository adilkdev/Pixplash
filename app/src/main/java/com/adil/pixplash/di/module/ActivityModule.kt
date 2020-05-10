package com.adil.pixplash.di.module

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.di.ActivityScope
import com.adil.pixplash.ui.base.BaseActivity
import com.adil.pixplash.ui.home.HomeViewModel
import com.adil.pixplash.ui.home.explore.ImageDetailAdapter
import com.adil.pixplash.ui.home.explore.ImageDetailViewModel
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
                ImageDetailViewModel(schedulerProvider, compositeDisposable, networkHelper, photoRepository)
            }).get(ImageDetailViewModel::class.java)

    @Provides
    fun provideImageDetailAdapter(job: CompletableJob) = ImageDetailAdapter(activity, job)

}