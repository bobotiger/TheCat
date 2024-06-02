package com.example.thecat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.thecat.data.CatsLocalRepository
import com.example.thecat.data.CatsNetworkRepository

class CatsViewModelFactory(
    private val networkRepository: CatsNetworkRepository,
    private val localRepository: CatsLocalRepository)
    : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatsViewModel::class.java))
            return modelClass.getConstructor(
                CatsNetworkRepository::class.java,
                CatsLocalRepository::class.java
            ).newInstance(networkRepository,localRepository)
        throw IllegalArgumentException("未知类型ViewModel")
    }
}