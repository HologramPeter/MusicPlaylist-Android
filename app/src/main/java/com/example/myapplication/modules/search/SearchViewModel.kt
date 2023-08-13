package com.example.myapplication.modules.search

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.extensions.saveFavouriteList
import com.example.myapplication.request.MusicInfo
import com.example.myapplication.request.MusicListRequest
import com.example.myapplication.request.primaryKey
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL

class SearchViewModel(private val context: Context) : ViewModel(){
    val loading = MutableLiveData<Boolean>().apply { value = false }

    private var data: ArrayList<MusicInfo> = ArrayList()
    var displayData: ArrayList<MusicInfo> = ArrayList()
    var images = mutableMapOf<String, Bitmap>()
    private var downloadTask: Int = 0

    var filterMode = false
        set(value) {
            field = value
            filterDisplayData()
        }
    var filterStr = ""

    var request = MusicListRequest()

    fun search(term: String?) {
        request.term = term ?: ""
        if (request.term == "") return
        request.pageInd = 0
        loading.value = true
        request.request { response ->
            data = response.results
            filterDisplayData()
            downloadTask = response.results.size
            response.results.forEach { info ->
                downloadImage(info)
            }
        }
    }

    fun loadMoreData() {
        request.pageInd += 1
        loading.value = true
        request.request { response ->
            data += response.results
            filterDisplayData()
            downloadTask = response.results.size
            response.results.forEach { info ->
                downloadImage(info)
            }
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

    private fun completeDownload() {
        downloadTask -= 1
        if (downloadTask == 0) {
            loading.postValue(false)
        }
    }

    fun filter(term: String?) {
        filterStr = term ?: ""
        filterDisplayData()
    }

    private fun filterDisplayData() {
        if (filterMode && filterStr.isNotBlank()) {
            displayData = ArrayList(data.filter {
                it.artistName?.contains(filterStr, true) ?: false ||
                        it.country?.contains(filterStr, true) ?: false
            })
        } else {
            displayData = data
        }
    }


    fun removeFromList(music: MusicInfo) {
        data.removeAll { it.primaryKey == music.primaryKey }
        context.saveFavouriteList(data)
    }
}