package com.adil.pixplash.ui.home.explore


import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.ui.base.BaseViewModel
import com.adil.pixplash.utils.AppConstants
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
    val photos: MutableLiveData<Resource<List<Photo>>> = MutableLiveData()
    val error: MutableLiveData<Int> = MutableLiveData()
    val randomPhoto: MutableLiveData<String> = MutableLiveData()

    private val TAG = this::class.simpleName
    private var page = 1
    private var orderByStr = "latest"

    override fun onCreate() {
        makeCall()
    }

    private fun makeCall(pageNo: Int = page, orderBy: String = orderByStr) {
        //Log.e("adil", "page = $pageNo  orderBy = $orderBy")
        loading.value = true
        compositeDisposable.add(
            photoRepository
                .fetchPhotos(page = pageNo, orderBy = orderBy)
                .delay(0, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        photos.postValue(Resource.success(it))
                        loading.postValue(false)
                        if (page==1)
                        randomPhoto()
                        page++
                    },
                    {
                        Log.e(TAG, "${it.localizedMessage}")
                        error.postValue(getNetworkError(it))
                        loading.postValue(false)
                    }
                )
        )
    }

    private fun randomPhoto() {
        compositeDisposable.add(
            photoRepository
                .fetchOneRandomPhoto()
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        randomPhoto.postValue(it.urls.regular)
                    },
                    {
                        Log.e(TAG, "${it.localizedMessage}")
                    }
                )
        )
    }

    fun savePhotos(list: List<Photo>, photoType: String) {
        photoRepository.savePhotos(list, photoType)
    }

    fun removePhotos(photoType: String) {
        photoRepository.removePhotos(photoType)
    }

    fun updateState(orderBy: String) {
        this.page = 1
        this.orderByStr = orderBy
    }

    fun onLoadMore() {
        if (loading.value !== null || loading.value == false) makeCall()
    }

    fun getPage() = page - 1

}