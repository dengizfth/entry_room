package com.fatihden.roomdatabase.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Detail(
    // id'yi kendimiz verebiliriz . Otomatik id yerine kendimiz burada tanımlarız .

    @ColumnInfo(name = "isim")
    var isim : String ,

    @ColumnInfo(name = "detay")
    val detay : String ,

    @ColumnInfo(name = "gorsel")
    var gorsel : ByteArray
) {
    // İd'yi otomatik tanımlar.
    @PrimaryKey(autoGenerate = true)
    var id = 0  //Default Değeri 0 'dır.


}