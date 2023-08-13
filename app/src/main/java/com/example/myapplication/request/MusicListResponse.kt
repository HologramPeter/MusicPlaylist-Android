package com.example.myapplication.request

import java.io.Serializable

data class MusicListResponse(
    val resultCount: Int,
    val results: ArrayList<MusicInfo>
): Serializable

data class MusicInfo(
    val wrapperType: String?,

    val trackExplicitness: String?,
    val collectionExplicitness: String?,
    val artistExplicitness: String?,

    val kind: String?,
    val trackName: String?,
    val artistName: String?,
    val collectionName: String?,

    val trackCensoredName: String?,
    val collectionCensoredName: String?,
    val artistCensoredName: String?,

    val artworkUrl100: String?,
    val artworkUrl60: String?,

    val trackViewUrl: String?,
    val collectionViewUrl: String?,
    val artistViewUrl: String?,

    val previewURL: String?,
    val trackTimeMillis: Int?,

    val country: String?
): Serializable

val MusicInfo.name: String
    get() = trackName?:""

val MusicInfo.viewURL: String
    get() = trackViewUrl?:""

val MusicInfo.primaryKey: String
    get() = viewURL