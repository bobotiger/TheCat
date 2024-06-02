package com.example.thecat.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TheCatApiService {
    @GET("images/search")
    fun getList(
        @Query("limit") limit:Int
    ) : Call<List<CatPicture>>
}