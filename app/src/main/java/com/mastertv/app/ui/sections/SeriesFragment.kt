package com.mastertv.app.ui.sections

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.leanback.widget.*
import com.mastertv.app.data.ContentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SeriesFragment : Fragment() {

    private val adapter = ArrayObjectAdapter(ListRowPresenter())
    private val repo = ContentRepository()

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, s: Bundle?): android.view.View? {
        val root = androidx.leanback.app.BrowseSupportFragment().requireView()
        CoroutineScope(Dispatchers.Main).launch {
            loadSeries()
        }
        return root
    }

    private suspend fun loadSeries() {
        val items = repo.loadM3U("http://omnixcdn.online:80/get.php?username=placeholder&password=placeholder&type=m3u_plus&output=mpegts")
        val series = items.filter { (it.group ?: "").contains("SERIE", ignoreCase=true) || (it.group ?: "").contains("SEASON", ignoreCase=true) || (it.group ?: "").contains("SHOW", ignoreCase=true) }
        val grouped = series.groupBy { it.group ?: "Séries" }
        adapter.clear()
        val presenter = com.mastertv.app.ui.home.HomePresenter()
        var id = 0L
        grouped.forEach { (groupName, list) ->
            val header = HeaderItem(id++, groupName)
            val rowAdapter = ArrayObjectAdapter(presenter)
            list.forEach { rowAdapter.add(it) }
            adapter.add(ListRow(header, rowAdapter))
        }
    }
}
