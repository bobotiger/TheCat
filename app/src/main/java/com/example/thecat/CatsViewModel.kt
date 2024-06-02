package com.example.thecat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.thecat.data.CatsLocalRepository
import com.example.thecat.data.CatsNetworkRepository
import kotlinx.coroutines.launch

class CatsViewModel(
    private val networkRepository: CatsNetworkRepository,
    private val localRepository: CatsLocalRepository
): ViewModel(){
    // 从本地数据源中获取的List<CatPicture>列表，为LiveData
    val catPictures = localRepository.catPictures
    // 用于ImageView加载的网络图片url
    val curUrl = MutableLiveData<String>()

    // 本ViewModel逻辑的核心数据，所有Button控件的可用性，都以其为依据
    private val _curPos = MutableLiveData<Int>(0)
    val curPos: LiveData<Int> = _curPos

    // 这里使用了LiveData的map方法，将prevEnabled的值映射表示为curPos值得关系
    // 即curPos值大于0时，prevEnabled为true
    // 否则，prevEnabled的值为false
    val prevEnabled = curPos.map { pos -> pos > 0 }

    // nextEnabled的值需要监听两个livedata的值变化来决定
    // 即nextEnabled在curPos值小于catPictures值的列表大小-1时，为true
    // 否则，nextEnabled值为false
    val nextEnabled = catPictures.map { pictures ->
        curPos.map{pos->
            pos < pictures.size - 1
        }
    }

    // refreshEnabled使用另外一种监听方式，即MediatorLiveData
    // 通过addSource来添加一个监听条件，可以同时使用多个addSource来添加多个监听条件
    // 也可以使用前面nextEnabled的监听处理方式进行设置
    val refreshEnabled = MediatorLiveData<Boolean>().apply {
        // 添加第一个源 LiveData - catPictures
        addSource(catPictures) { pictures ->
            // 当 catPictures 更新时，计算 nextEnabled 的值
            val pos = curPos.value ?: 0
            value = pos == (pictures?.size ?: 0) - 1
        }

        // 添加第二个源 LiveData - curPos
        addSource(curPos) { pos ->
            // 当 curPos 更新时，计算 nextEnabled 的值
            val pictures = catPictures.value ?: emptyList()
            value = pos == pictures.size - 1
        }
    }

    // viewmodel实例化后，执行本init块
    init {
        refreshCatPictures()
    }

    // 根据catPictures的值列表以及curPos的值，获取当前浏览图片的url
    fun updateCurrentCatPictureUrl(){
        catPictures.value?.let {list ->
            if(list.isNotEmpty()){
                _curPos.value?.let {pos ->
                    curUrl.value = list[pos].url
                }
            }
        }
    }

    // 由next_button控件点击时调用，用来改变curPos的值，同时刷新url
    fun nextImage(){
        _curPos.value?.let {pos ->
            catPictures.value?.let {list ->
                if (pos < list.size-1){
                    _curPos.value = pos + 1
                    Log.d("调试输出", "(nextImage said)当前位置: ${pos+1}")
                    updateCurrentCatPictureUrl()
                }
            }
        }
    }

    // 由prev_button控件点击时调用，用来改变curPos的值，同时刷新url
    fun prevImage(){
        _curPos.value?.let {pos ->
            if (pos > 0){
                _curPos.value = pos - 1
                Log.d("调试输出", "(prevImage said)当前位置: ${pos-1}")
                updateCurrentCatPictureUrl()
            }
        }
    }

    // 从网络获取初始数据，并写入数据库
    private fun refreshCatPictures(){
        viewModelScope.launch {
            networkRepository.getCatImageResponseForList().let {list ->
                list?.let { localRepository.insertAll(list)}
            }
        }
    }

    // 由refresh_button控件点击时调用，用来继续从网络获取数据，并写入数据库
    fun refreshImageList(){
        refreshCatPictures()
    }
}