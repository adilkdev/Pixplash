package com.adil.pixplash.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.adil.pixplash.data.local.db.entity.Photo

@Dao
interface ExploreDao {

    @Insert
    suspend fun addImageList(photos: List<Photo>)

    @Query("DELETE FROM photo_entity")
    suspend fun removePhotos()

    @Query("SELECT * FROM photo_entity")
    suspend fun getAllPhotosFromDB(): List<Photo>


}