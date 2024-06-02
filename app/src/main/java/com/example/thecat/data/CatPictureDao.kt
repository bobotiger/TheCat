package com.example.thecat.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CatPictureDao {
    // 在插入记录时，如果数据表中已存在该记录，则忽略该记录，并继续其他记录的插入
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(pictures: List<CatPicture>)

    @Query("SELECT * FROM picture_table")
    fun getAll() : LiveData<List<CatPicture>>
}