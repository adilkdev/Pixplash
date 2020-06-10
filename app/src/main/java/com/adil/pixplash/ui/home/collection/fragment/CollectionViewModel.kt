package com.adil.pixplash.ui.home.collection.fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.adil.pixplash.data.remote.response.Collection
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.ui.base.BaseViewModel
import com.adil.pixplash.ui.home.collection.fragment.CollectionFragment.Companion.TYPE_ALL
import com.adil.pixplash.utils.common.Resource
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CollectionViewModel (
    schedulerProvider : SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val photoRepository: PhotoRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val collections: MutableLiveData<Resource<List<Collection>>> = MutableLiveData()
    val error: MutableLiveData<Int> = MutableLiveData()
    val randomPhoto: MutableLiveData<String> = MutableLiveData()

    private val TAG = this::class.simpleName
    private var page = 1

    private var currentType = TYPE_ALL

    override fun onCreate() {
        makeCall()
    }

    private fun makeCall(pageNo: Int = page) {
        Log.e("Adil", "page = $pageNo")
        loading.value = true
        randomPhoto()
        compositeDisposable.add(
            photoRepository
                .fetchCollections(page = pageNo)
                .delay(0, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        collections.postValue(Resource.success(it))
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

    private fun makeFeaturedCall(pageNo: Int = page) {
        Log.e("Adil", "page = $pageNo")
        loading.value = true
        randomPhoto()
        compositeDisposable.add(
            photoRepository
                .fetchFeaturedCollections(page = pageNo)
                .delay(0, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        collections.postValue(Resource.success(it))
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

    fun onLoadMore() {
        if (loading.value !== null || loading.value == false) {
            if (currentType == TYPE_ALL) {
                makeCall()
            } else {
                makeFeaturedCall()
            }
        }
    }

    fun updateType(type: Int) {
        this.currentType = type
        this.page = 1
    }

    fun getPage() = page - 1

}