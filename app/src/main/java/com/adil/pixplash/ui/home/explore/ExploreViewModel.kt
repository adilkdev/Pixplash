package com.adil.pixplash.ui.home.explore


import androidx.lifecycle.MutableLiveData
import com.adil.pixplash.data.remote.response.PhotoResponse
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.ui.base.BaseViewModel
import com.adil.pixplash.utils.common.Resource
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ExploreViewModel(
    schedulerProvider : SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val photoRepository: PhotoRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val photos: MutableLiveData<Resource<List<PhotoResponse>>> = MutableLiveData()
    val error: MutableLiveData<Int> = MutableLiveData()

    var page = 1

    init {
        makeCall()
    }

    override fun onCreate() {
        //loadMorePosts()
    }

    private fun makeCall() {
        compositeDisposable.add(
            photoRepository
                .fetchPhotos(page = page)
                .delay(600, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        photos.postValue(Resource.success(it))
                        loading.postValue(false)
                        page++
                    },
                    {
                        error.postValue(getNetworkError(it))
                        loading.postValue(false)
                    }
                )
        )
    }

    fun onLoadMore() {
        if (loading.value !== null || loading.value == false) loadMorePosts()
    }

    private fun loadMorePosts() {
        makeCall()
    }

}