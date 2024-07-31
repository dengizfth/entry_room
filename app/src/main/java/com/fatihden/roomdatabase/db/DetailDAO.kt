package com.fatihden.roomdatabase.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.fatihden.roomdatabase.model.Detail

@Dao
interface DetailDAO {

    @Query("SELECT * FROM Detail")
    fun getAll() : List<Detail>

    @Insert
    fun insert(detay : Detail)

    @Delete
    fun delete(detay : Detail)

    @Query("SELECT * FROM Detail WHERE id = :id")
    fun findById(id : Int) : Detail


}