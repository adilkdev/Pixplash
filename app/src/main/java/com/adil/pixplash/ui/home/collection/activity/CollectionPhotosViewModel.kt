package com.adil.pixplash.ui.home.collection.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.ui.base.BaseViewModel
import com.adil.pixplash.utils.AppConstants
import com.adil.pixplash.utils.common.Resource
import com.adil.pixplash.utils.dispatcher.CoroutineDispatcherProvider
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CollectionPhotosViewModel (
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    networkHelper: NetworkHelper,
    private val photoRepository: PhotoRepository
) : BaseViewModel(
    coroutineDispatcherProvider, networkHelper
) {

    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val photos: MutableLiveData<Resource<List<Photo>>> = MutableLiveData()
    val error: MutableLiveData<Int> = MutableLiveData()

    private val TAG = this::class.simpleName
    private var page = 1
    private var collectionId: String = ""

    fun setCollectionId(collectionId: String) {
        this.collectionId = collectionId
        //makeCall()
    }

    /*
    private fun makeCall(pageNo: Int = page) {
        //Log.e("adil", "page = $pageNo  orderBy = $orderBy")
        loading.value = true
        compositeDisposable.add(
            photoRepository
                .fetchCollectionPhoto(collectionId = collectionId,page = pageNo)
                .delay(400, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        photos.postValue(Resource.success(it))
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


    fun onLoadMore() {
        if (loading.value !== null || loading.value == false) makeCall()
    }
    */

    fun getPage() = page - 1

    fun savePhotos(list: List<Photo>) {
        photoRepository.savePhotos(list, AppConstants.PHOTO_TYPE_COLLECTION)
    }

    fun removePhotos() {
        photoRepository.removePhotos(AppConstants.PHOTO_TYPE_COLLECTION)
    }

}