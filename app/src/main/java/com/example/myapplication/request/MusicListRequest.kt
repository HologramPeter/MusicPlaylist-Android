package com.example.myapplication.request

import android.graphics.BitmapFactory
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.BufferedInputStream


class MusicListRequest(var pageInd: Int = 0, var term: String = "") {
    companion object{
        const val PAGE_LIMIT = 20
        val client = OkHttpClient()
    }

    private val offset: Int
        get() = Integer.max(0, pageInd) * PAGE_LIMIT

    fun request(completion: (response: MusicListResponse) -> Unit){
        val url: HttpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("itunes.apple.com")
            .addPathSegment("search")
            .addQueryParameter("media", "music")
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("limit", PAGE_LIMIT.toString())
            .addQueryParameter("lang", "en")
            .addQueryParameter("term", term)
            .build()

        val request: Request = Request.Builder()
            .url(url)
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val listResponse = Gson().fromJson(response.body!!.string(), MusicListResponse::class.java)
                completion(listResponse)
            }
        })
    }
}