package com.mastertv.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import com.mastertv.app.data.ContentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val adapter = ArrayObjectAdapter(ListRowPresenter())
    private val repository = ContentRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View(inflater.context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loadContent()
    }

    private fun loadContent() {
        CoroutineScope(Dispatchers.Main).launch {
            val m3uUrl = "URL_DA_M3U_AQUI"
            val items = repository.loadM3U(m3uUrl)
            buildRows(items)
        }
    }

    private fun buildRows(channels: List<com.mastertv.app.models.Channel>) {
        val grouped = channels.groupBy { it.group }

        grouped.forEach { (groupName, list) ->
            val rowAdapter = ArrayObjectAdapter(HomePresenter())
            list.forEach { rowAdapter.add(it) }

            val header = androidx.leanback.widget.HeaderItem(groupName)
            adapter.add(ListRow(header, rowAdapter))
        }
    }
}
