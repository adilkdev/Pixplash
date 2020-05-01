package com.adil.pixplash.data.repository

import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.remote.NetworkService
import com.adil.pixplash.data.remote.response.PhotoResponse
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(private val networkService: NetworkService) {

    fun fetchPhotos(page: Int = 1, itemsPerPage: Int = 30, orderBy: String = "latest"): Single<List<Photo>> =
        networkService.fetchPhotos(page = page, perPage = itemsPerPage, orderBy = orderBy)

    fun fetchOneRandomPhoto(): Single<Photo> = networkService.fetchOneRandomPhoto()

}