package com.adil.pixplash.ui.home.search.photo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.ui.base.BaseViewModel
import com.adil.pixplash.utils.common.Resource
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SearchPhotoViewModel(
    schedulerProvider : SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val photoRepository: PhotoRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val photos: MutableLiveData<Resource<List<Photo>>> = MutableLiveData()
    val error: MutableLiveData<Int> = MutableLiveData()

    private val TAG = this::class.simpleName
    private var page = 1

    override fun onCreate() {
    }

    fun searchPhotos(pageNo: Int = page, query: String = "") {
        //Log.e("adil", "page = $pageNo  orderBy = $orderBy")
        loading.value = true
        compositeDisposable.add(
            photoRepository
                .searchPhotos(page = pageNo, query = query)
                .delay(400, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        photos.postValue(Resource.success(it.results))
                        loading.postValue(false)
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

    fun onLoadMore(query: String) {
        if (loading.value !== null || loading.value == false)
            searchPhotos(query = query)
    }

    fun savePhotos(list: List<Photo>, photoType: String) {
        photoRepository.savePhotos(list, photoType)
    }

    fun removePhotos(photoType: String) {
        photoRepository.removePhotos(photoType)
    }

    fun getPage() = page - 1

}