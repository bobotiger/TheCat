package com.example.thecat.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "picture_table")
data class CatPicture(
    @PrimaryKey
    @ColumnInfo(name = "pic_id")
    @field:Json(name = "id")
    val id : String,

    @ColumnInfo(name = "pic_url")
    @field:Json(name = "url")
    val url : String,

    @ColumnInfo(name = "pic_width")
    @field:Json(name = "width")
    val width : Int,

    @ColumnInfo(name = "pic_height")
    @field:Json(name = "height")
    val height : Int
)