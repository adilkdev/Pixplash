package com.adil.pixplash.data.repository

import android.util.Log
import com.adil.pixplash.data.local.db.DatabaseService
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.remote.NetworkService
import com.adil.pixplash.data.remote.response.PhotoResponse
import io.reactivex.Single
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(private val networkService: NetworkService,
                                          private val databaseService: DatabaseService,
                                          private val job: CompletableJob
) {

    fun fetchPhotos(page: Int = 1, itemsPerPage: Int = 30, orderBy: String = "latest"): Single<List<Photo>> =
        networkService.fetchPhotos(page = page, perPage = itemsPerPage, orderBy = orderBy)

    fun fetchOneRandomPhoto(): Single<Photo> = networkService.fetchOneRandomPhoto()

    fun savePhotos(photos: List<Photo>) {
        CoroutineScope(Dispatchers.IO + job).launch {
            val ids = databaseService.exploreDao().addImageList(photos)
        }
    }

    fun removePhotos() {
        CoroutineScope(Dispatchers.IO + job).launch {
            databaseService.exploreDao().removePhotos()
        }
    }

    fun getAllPhotosFromDB(): List<Photo> {
        lateinit var photos: List<Photo>
        runBlocking(Dispatchers.IO + job) {
            val job = async { gettingPhotos() }
            photos = job.await()
        }
        return photos
    }

    private suspend fun gettingPhotos() = databaseService.exploreDao().getAllPhotosFromDB()

}