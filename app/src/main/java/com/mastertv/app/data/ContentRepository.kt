package com.mastertv.app.data

import com.mastertv.app.m3u.M3UParser
import com.mastertv.app.models.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class ContentRepository {

    suspend fun loadM3U(url: String): List<Channel> = withContext(Dispatchers.IO) {
        val content = URL(url).readText()
        return@withContext M3UParser.parse(content)
    }
}
