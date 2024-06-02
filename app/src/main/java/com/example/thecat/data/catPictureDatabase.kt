package com.example.thecat.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ CatPicture::class], version = 1, exportSchema = false)
abstract class CatPictureDatabase: RoomDatabase(){
    abstract val catPictureDao : CatPictureDao

    companion object{
        @Volatile
        private var INSTANCE: CatPictureDatabase? = null

        fun getDatabase(context: Context): CatPictureDatabase{
            synchronized(this) {
                // 确保INSTANCE不为null时，不会再次构建数据库实例
                var instance = INSTANCE
                // 如果INSTANCE为null，则构建数据库实例
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CatPictureDatabase::class.java,
                        "cat_picture_db"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}