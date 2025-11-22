package com.mastertv.app.ui.sections

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.leanback.widget.*
import com.mastertv.app.data.ContentRepository
import com.mastertv.app.models.Channel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MoviesFragment : Fragment() {

    private val adapter = ArrayObjectAdapter(ListRowPresenter())
    private val repo = ContentRepository()

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, s: Bundle?): View? {
        val root = androidx.leanback.app.BrowseSupportFragment().requireView()
        CoroutineScope(Dispatchers.Main).launch {
            loadMovies()
        }
        return root
    }

    private suspend fun loadMovies() {
        val items = repo.loadM3U("http://omnixcdn.online:80/get.php?username=placeholder&password=placeholder&type=m3u_plus&output=mpegts")
        val films = items.filter { (it.group ?: "").contains("FILM", ignoreCase=true) || (it.group ?: "").contains("VOD", ignoreCase=true) || (it.group ?: "").contains("MOVIE", ignoreCase=true) }
        val grouped = films.groupBy { it.group ?: "Filmes" }
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
