package com.insa.mygamelist.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.insa.mygamelist.R
import okhttp3.internal.platform.Platform

object IGDB {

    lateinit var covers: List<Cover>
    lateinit var games: List<Games>
    lateinit var genres: List<Genres>
    lateinit var platform_logos: List<PlatformLogos>
    lateinit var platforms: List<Platforms>

    fun load(context: Context) {
        val coversFromJson: List<Cover> = Gson().fromJson(
            context.resources.openRawResource(R.raw.covers).bufferedReader(),
            object : TypeToken<List<Cover>>() {}.type
        )
        covers = coversFromJson

        val gamesFromJson: List<Games> = Gson().fromJson(
            context.resources.openRawResource(R.raw.games).bufferedReader(),
            object : TypeToken<List<Games>>() {}.type
        )
        games = gamesFromJson

        val genresFromJson: List<Genres> = Gson().fromJson(
            context.resources.openRawResource(R.raw.genres).bufferedReader(),
            object : TypeToken<List<Genres>>() {}.type
        )
        genres = genresFromJson

        val platformLogosFromJson: List<PlatformLogos> = Gson().fromJson(
            context.resources.openRawResource(R.raw.platform_logos).bufferedReader(),
            object : TypeToken<List<PlatformLogos>>() {}.type
        )
        platform_logos = platformLogosFromJson

        val platformsFromJson: List<Platforms> = Gson().fromJson(
            context.resources.openRawResource(R.raw.platforms).bufferedReader(),
            object : TypeToken<List<Platforms>>() {}.type
        )
        platforms = platformsFromJson
    }
}

data class Cover(val id: Long, val url: String)
data class Games(val id: Long, val cover: Long, val first_release_date: Long, val genres: List<Long>,
                 val name: String, val platforms: List<Long>, val summary: String, val total_rating: Double)
data class Genres(val id: Long, val name: String)
data class PlatformLogos(val id: Long, val url: String)
data class Platforms(val id: Long, val name: String, val platform_logo: Long)

