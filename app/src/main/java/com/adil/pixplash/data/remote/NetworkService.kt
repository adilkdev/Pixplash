package com.adil.pixplash.data.remote

import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.remote.response.Collection
import com.adil.pixplash.data.remote.response.PhotoDetailResponse
import com.adil.pixplash.data.remote.response.SearchCollectionResponse
import com.adil.pixplash.data.remote.response.SearchPhotoResponse
import com.adil.pixplash.utils.AppConstants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface NetworkService {

    @GET(Endpoints.PHOTOS)
    suspend fun fetchPhotos(
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("order_by") orderBy: String
    ): List<Photo>

    @GET(Endpoints.SEARCH_PHOTOS)
    suspend fun searchPhotos(
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("query") query: String
    ): SearchPhotoResponse

    @GET(Endpoints.RANDOM_PHOTO)
    suspend fun fetchOneRandomPhoto(
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY
    ): Photo

    @GET(Endpoints.PHOTO_DETAIL+"{id}")
    suspend fun fetchPhotoDetails(@Path("id") id: String,
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY
    ): PhotoDetailResponse

    @GET(Endpoints.COLLECTIONS)
    suspend fun fetchCollections(
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ) : List<Collection>

    @GET(Endpoints.SEARCH_COLLECTION)
    suspend fun searchCollections(
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("query") query: String
    ) : SearchCollectionResponse

    @GET(Endpoints.COLLECTION_FEATURED)
    suspend fun fetchFeaturedCollections(
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ) : List<Collection>

    @GET(Endpoints.COLLECTION_PHOTOS+"{id}/photos")
    suspend fun fetchCollectionPhotos(
        @Path("id") id: String,
        @Query("client_id") clientId: String = AppConstants.ACCESS_KEY,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ) : List<Photo>
}