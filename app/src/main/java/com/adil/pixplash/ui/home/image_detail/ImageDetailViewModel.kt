package com.adil.pixplash.ui.home.image_detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.ui.base.BaseViewModel
import com.adil.pixplash.utils.common.Resource
import com.adil.pixplash.utils.dispatcher.CoroutineDispatcherProvider
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ImageDetailViewModel(coroutineDispatcherProvider: CoroutineDispatcherProvider,
                           networkHelper: NetworkHelper,
                           private val photoRepository: PhotoRepository
): BaseViewModel(coroutineDispatcherProvider, networkHelper) {

    private val TAG = this::class.simpleName
    private var page = 1
    private var orderByStr = "latest"
    private var type = "photo"
    private var collectionId = ""

    val photos: MutableLiveData<Resource<List<Photo>>> = MutableLiveData()

    val error: MutableLiveData<Int> = MutableLiveData()

    fun setPagerType(type: String, id: String) {
        this.type = type
        this.collectionId = id
    }

    /*
    fun loadMore(pageNo: Int = page, orderBy: String = orderByStr) {
        //Log.e("adil", "page = $pageNo  orderBy = $orderBy")
        compositeDisposable.add(
            photoRepository
                .fetchPhotos(page = pageNo, orderBy = orderBy)
                .delay(0, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        photos.postValue(Resource.success(it))
                        page++
                    },
                    {
                        Log.e(TAG, "${it.localizedMessage}")
                        error.postValue(getNetworkError(it))
                    }
                )
        )
    }

    fun loadMoreCollectionPhotos(pageNo: Int = page) {
        //Log.e("adil", "page = $pageNo  orderBy = $orderBy")
        compositeDisposable.add(
            photoRepository
                .fetchCollectionPhoto(collectionId = collectionId,page = pageNo)
                .delay(0, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        photos.postValue(Resource.success(it))
                        page++
                    },
                    {
                        Log.e(TAG, "${it.localizedMessage}")
                        error.postValue(getNetworkError(it))
                    }
                )
        )
    }

    fun searchPhotos(pageNo: Int = page, query: String = "") {
        //Log.e("adil", "page = $pageNo  orderBy = $orderBy")
        compositeDisposable.add(
            photoRepository
                .searchPhotos(page = pageNo, query = query)
                .delay(0, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        photos.postValue(Resource.success(it.results))
                        page++
                    },
                    {
                        Log.e(TAG, "${it.localizedMessage}")
                        error.postValue(getNetworkError(it))
                    }
                )
        )
    }
     */

    fun setPage(page: Int) {
        this.page = page + 1
    }

    fun setActiveOrder(orderBy: String) {
        this.orderByStr = orderBy
    }

}