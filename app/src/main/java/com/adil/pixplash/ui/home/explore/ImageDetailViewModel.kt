package com.adil.pixplash.ui.home.explore

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

class ImageDetailViewModel(schedulerProvider: SchedulerProvider,
                           compositeDisposable: CompositeDisposable,
                           networkHelper: NetworkHelper,
                           private val photoRepository: PhotoRepository
): BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    private val TAG = this::class.simpleName
    private var page = 1
    private var orderByStr = "latest"

    val photos: MutableLiveData<Resource<List<Photo>>> = MutableLiveData()

    val error: MutableLiveData<Int> = MutableLiveData()

    override fun onCreate() {

    }

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

    fun setPage(page: Int) {
        this.page = page + 1
    }

    fun setActiveOrder(orderBy: String) {
        this.orderByStr = orderBy
    }

}