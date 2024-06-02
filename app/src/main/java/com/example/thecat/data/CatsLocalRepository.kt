package com.example.thecat.data

class CatsLocalRepository(
    private val dao : CatPictureDao
) {
    val catPictures = dao.getAll()

    suspend fun insertAll(pictures:List<CatPicture>) = dao.insertAll(pictures)
}