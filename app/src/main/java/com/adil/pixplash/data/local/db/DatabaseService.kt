package com.adil.pixplash.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adil.pixplash.data.local.db.dao.ExploreDao
import com.adil.pixplash.data.local.db.entity.Link
import com.adil.pixplash.data.local.db.entity.Photo
import com.adil.pixplash.data.local.db.entity.Url
import javax.inject.Singleton

@Singleton
@Database(
    entities = [
        Photo::class
    ],
    exportSchema = false,
    version = 1
)
abstract class DatabaseService : RoomDatabase() {

    abstract fun exploreDao(): ExploreDao

}