package com.example.myapplication.modules.favourite

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.extensions.getFavouriteList
import com.example.myapplication.extensions.saveFavouriteList
import com.example.myapplication.request.MusicInfo
import com.example.myapplication.request.MusicListResponse
import com.example.myapplication.request.primaryKey
import com.example.myapplication.testResponse
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL

class FavouriteViewModel(private val context: Context) : ViewModel() {
    val loading: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }

    var data: ArrayList<MusicInfo> = ArrayList()

    var images = mutableMapOf<String, Bitmap>()
    private var downloadTask: Int = 0

    init {
        reload()
    }

    fun reload() {
        data = context.getFavouriteList()

        loading.postValue(true)

        downloadTask = data.size
        for (info in data) {
            downloadImage(info)
        }
    }

    private fun downloadImage(info: MusicInfo) {
        val urlStr = info.artworkUrl60
        val url = urlStr?.let { URL(it) }

        if (url != null) {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    completeDownload()
                }

                override fun onResponse(call: Call, response: Response) {
                    val inputStream = response.body?.byteStream()
                    val bufferedInputStream = BufferedInputStream(inputStream)
                    val bitmap = BitmapFactory.decodeStream(bufferedInputStream)
                    images[urlStr] = bitmap
                    completeDownload()
                }
            })
        } else {
            completeDownload()
        }
    }

    fun completeDownload() {
        downloadTask -= 1
        if (downloadTask == 0) {
            loading.postValue(false)
        }
    }

    fun removeFromList(music: MusicInfo) {
        data.removeAll { it.primaryKey == music.primaryKey }
        context.saveFavouriteList(data)
    }
}