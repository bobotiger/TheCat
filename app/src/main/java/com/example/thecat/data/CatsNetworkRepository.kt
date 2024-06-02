package com.example.thecat.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CatsNetworkRepository {
    private val baseUrl = "https://api.thecatapi.com/v1/"

    val listResult = MutableLiveData<List<CatPicture>>()

    private val retrofitMoshi by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(TheCatApiService::class.java)
    }

    suspend fun getCatImageResponseForList() :List<CatPicture>? {
        return suspendCancellableCoroutine { continuation ->
            val call = retrofitMoshi.getList(10)
            call.enqueue(object : Callback<List<CatPicture>> {

                override fun onResponse(
                    call: Call<List<CatPicture>>,
                    response: Response<List<CatPicture>>
                ) {
                    if (response.isSuccessful) {
                        continuation.resume(response.body())
                    } else {
                        Log.e(
                            "HTTP响应错误:",
                            "获取数据失败\n${response.errorBody()?.string() ?: ""}"
                        )
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<List<CatPicture>>, t: Throwable) {
                    Log.e("网络错误:", t.toString(), t)
                    if (!continuation.isCancelled){
                        continuation.resumeWithException(t)
                    }
                }
            })
        }
    }
}