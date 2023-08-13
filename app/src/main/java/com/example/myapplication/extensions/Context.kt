package com.example.myapplication.extensions

import android.content.Context
import com.example.myapplication.request.MusicInfo
import com.example.myapplication.request.primaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


const val USER_CONFIG = "USER_CONFIG"

const val FAVOURITE_LIST = "FavouriteList"

fun Context.getFavouriteList(): ArrayList<MusicInfo> {
    val musicListType = object : TypeToken<ArrayList<MusicInfo>>() {}.type
    return Gson().fromJson(
        getSharedPreferences(USER_CONFIG, Context.MODE_PRIVATE).getString(
            FAVOURITE_LIST,
            "[]"
        ), musicListType
    ) ?: ArrayList()
}

fun Context.saveFavouriteList(list : ArrayList<MusicInfo>){
    getSharedPreferences(USER_CONFIG, Context.MODE_PRIVATE).edit()
        .putString(FAVOURITE_LIST, Gson().toJson(list))
        .apply()
}

fun Context.isFavourite(info: MusicInfo): Boolean {
    if (info.primaryKey == "") return false
    return getFavouriteList().firstOrNull { it.primaryKey == info.primaryKey } != null
}

fun Context.setFavourite(info: MusicInfo, on: Boolean = true) {
    if (info.primaryKey == "") return
    if (on) {
        if (!isFavourite(info)) {
            val list = getFavouriteList()
            list?.add(info)
            saveFavouriteList(list)
        }
    } else {
        val list = getFavouriteList()
        list?.removeAll { it.primaryKey == info.primaryKey }
        saveFavouriteList(list)
    }
}