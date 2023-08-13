package com.example.myapplication.adaptor

import android.view.View
import com.example.myapplication.request.MusicInfo

interface MusicListEventListener {
    fun onClick(data: MusicInfo, setFavourite: Boolean, view: View)
}