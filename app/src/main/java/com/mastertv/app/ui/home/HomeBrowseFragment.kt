package com.mastertv.app.ui.home

import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.mastertv.app.data.ContentRepository
import com.mastertv.app.models.Channel
import kotlinx.coroutines.*

class HomeBrowseFragment : BrowseSupportFragment() {

    private val adapter = ArrayObjectAdapter(ListRowPresenter())
    private val repo = ContentRepository()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = "Master TV 📺"
        headersState = HEADERS_ENABLED
        brandColor = resources.getColor(android.R.color.transparent, null)

        adapter = ArrayObjectAdapter(ListRowPresenter())
        setAdapter(adapter)

        loadContent()
    }

    private fun loadContent() {
        CoroutineScope(Dispatchers.Main).launch {
            val url = "URL_DA_M3U_AQUI"
            val channels = repo.loadM3U(url)
            buildRows(channels)
        }
    }

    private fun buildRows(channels: List<Channel>) {
        val grouped = channels.groupBy { it.group }

        grouped.forEach { (groupName, list) ->
            val rowAdapter = ArrayObjectAdapter(HomePresenter())
            list.forEach { rowAdapter.add(it) }

            val header = HeaderItem(groupName)
            adapter.add(ListRow(header, rowAdapter))
        }
    }
}
