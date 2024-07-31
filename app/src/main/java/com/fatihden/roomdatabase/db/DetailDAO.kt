package com.fatihden.roomdatabase.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.fatihden.roomdatabase.model.Detail
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface DetailDAO {

    @Query("SELECT * FROM Detail")
    fun getAll() :Flowable<List<Detail>>

    @Insert
    fun insert(detay : Detail) : Completable

    @Delete
    fun delete(detay : Detail) : Completable

    @Query("SELECT * FROM Detail WHERE id = :id")
    fun findById(id : Int) : Flowable<Detail>


}