package com.mastertv.app.m3u

import com.mastertv.app.models.Channel
import java.io.BufferedReader
import java.io.StringReader

object M3UParser {

    fun parse(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val reader = BufferedReader(StringReader(content))

        var line: String?
        var name = ""
        var logo = ""
        var group = ""
        var url = ""

        while (reader.readLine().also { line = it } != null) {
            when {
                line!!.startsWith("#EXTINF:") -> {
                    val attrs = line!!.substringAfter("#EXTINF:").split(",")
                    name = attrs.last()

                    logo = Regex("tvg-logo=\"(.*?)\"")
                        .find(line!!)?.groups?.get(1)?.value ?: ""

                    group = Regex("group-title=\"(.*?)\"")
                        .find(line!!)?.groups?.get(1)?.value ?: "Outros"
                }

                line!!.startsWith("http://") || line!!.startsWith("https://") -> {
                    url = line!!
                    channels.add(
                        Channel(name = name, logo = logo, group = group, url = url)
                    )
                }
            }
        }

        return channels
    }
}
