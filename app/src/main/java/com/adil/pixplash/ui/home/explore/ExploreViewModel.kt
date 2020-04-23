package com.adil.pixplash.ui.home.explore

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.adil.pixplash.data.remote.response.PhotoResponse
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.ui.base.BaseViewModel
import com.adil.pixplash.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ExploreViewModel(
    schedulerProvider : SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    private val photoRepository: PhotoRepository
) : BaseViewModel(schedulerProvider, compositeDisposable) {

    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val photos: MutableLiveData<List<PhotoResponse>> = MutableLiveData()

    init {
        compositeDisposable.add(
            photoRepository
                .fetchPhotos()
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        photos.postValue(it)
                    },
                    {
                        Log.e("EVM", "${it.localizedMessage}")
                    }
                )
        )
    }

    override fun onCreate() {
        //loadMorePosts()
    }

    private fun loadMorePosts() {
        photoRepository.fetchPhotos()
    }

}