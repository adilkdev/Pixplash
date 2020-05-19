package com.adil.pixplash.ui.home.search.collection

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.adil.pixplash.data.remote.response.Collection
import com.adil.pixplash.data.repository.PhotoRepository
import com.adil.pixplash.ui.base.BaseViewModel
import com.adil.pixplash.ui.home.collection.fragment.CollectionFragment
import com.adil.pixplash.utils.common.Resource
import com.adil.pixplash.utils.network.NetworkHelper
import com.adil.pixplash.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SearchCollectionViewModel (
    schedulerProvider : SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val photoRepository: PhotoRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val collections: MutableLiveData<Resource<List<Collection>>> = MutableLiveData()
    val error: MutableLiveData<Int> = MutableLiveData()

    private val TAG = this::class.simpleName
    private var page = 1
    private var query = ""

    override fun onCreate() {

    }

    fun searchCollection(pageNo: Int = page, query: String = "") {
        //Log.e("adil", "page = $pageNo  orderBy = $orderBy")
        loading.value = true
        compositeDisposable.add(
            photoRepository
                .searchCollections(page = pageNo, query = query)
                .delay(0, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        collections.postValue(Resource.success(it.results))
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
        if (loading.value !== null || loading.value == false) {
            searchCollection(query = query)
        }
    }

    fun getPage() = page - 1

    fun resetPage() {
        page = 1
    }

}