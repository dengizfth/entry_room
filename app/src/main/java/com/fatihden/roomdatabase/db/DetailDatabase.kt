package com.fatihden.roomdatabase.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fatihden.roomdatabase.model.Detail

@Database(entities = [Detail::class], version = 1)
abstract class DetailDatabase : RoomDatabase() {
    abstract fun detailDao(): DetailDAO



}