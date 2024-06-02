package com.example.thecat

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.thecat.data.CatPicture
import com.example.thecat.data.CatPictureDao
import com.example.thecat.data.CatPictureDatabase
import com.example.thecat.data.CatsLocalRepository
import com.example.thecat.data.CatsNetworkRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class CatsLocalRepositoryTest {
    private lateinit var catPictureDatabase: CatPictureDatabase
    private lateinit var catPictureDao: CatPictureDao
    private lateinit var catsNetworkRepository: CatsNetworkRepository
    private lateinit var catsLocalRepository: CatsLocalRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup(){
        // 获取应用上下文
        val context = ApplicationProvider.getApplicationContext<Context>()
        // 创建内存中的Room数据库（这个数据库只存在测试期间，测试完毕后会被清除）
        catPictureDatabase = Room.inMemoryDatabaseBuilder(
            context, CatPictureDatabase::class.java
        ).build()
        // 从数据库获取DAO对象
        catPictureDao = catPictureDatabase.catPictureDao
        // 初始化网络和本地数据源仓库对象
        catsNetworkRepository = CatsNetworkRepository()
        catsLocalRepository = CatsLocalRepository(catPictureDao)
    }

    // 在测试结束后关闭数据库
    @After
    @Throws(IOException::class)
    fun closeDb(){
        catPictureDatabase.close()
    }

    // 异步从网络获取数据并插入到本地数据库
    private suspend fun insertDataFromNetWork(){
        catsNetworkRepository.getCatImageResponseForList()?.let { pictures ->
            catsLocalRepository.insertAll(pictures)
        }
    }

    @Test
    fun testInsertDataFromNetwork() = runBlocking{
        // 使用CountDownLatch让测试线程等待，直到LiveData更新
        val latch = CountDownLatch(1)
        var pictures: List<CatPicture>? = null

        // 观察LiveData变化，当数据变化时，存储图片列表并减少latch计数
        catsLocalRepository.catPictures.observeForever{ catPictures ->
            pictures = catPictures
            latch.countDown()
        }

        // 插入网络数据到本地数据库
        insertDataFromNetWork()
        // 等待LiveData更新或最多等待6秒钟
        latch.await(6, TimeUnit.SECONDS)

        // 断言插入到数据库中的数据数量为10（初次插入）
        assertEquals(10, pictures?.size)

        // 再次插入相同的数据
        insertDataFromNetWork()
        // 由于LiveData不会再次更新（数据相同），此次等待测试其实是多余的
        // 注意：这行代码在实际测试中可能导致无限等待，因为数据未更改，LiveData不会再次触发更新
        latch.await(6, TimeUnit.SECONDS)

        // 遍历并打印所有插入的猫图片ID，仅用于调试查看
        var count = 1
        for (p in pictures!!){
            Log.d("测试输出","$count : ${p.id}")
            count++
        }
    }
}