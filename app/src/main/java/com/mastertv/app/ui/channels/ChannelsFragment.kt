package com.mastertv.app.ui.channels

import android.os.Bundle
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import com.mastertv.app.data.ContentRepository
import com.mastertv.app.data.Category
import com.mastertv.app.ui.home.HomePresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChannelsFragment : BrowseSupportFragment() {

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = rowsAdapter
        title = "Canais ao Vivo"
        load()
    }

    private fun load() {
        CoroutineScope(Dispatchers.Main).launch {
            val categories = ContentRepository.loadCategories(requireContext())
            val live = categories.find { it.name == "Ao Vivo" }
            build(live)
        }
    }

    private fun build(cat: Category?) {
        rowsAdapter.clear()
        if (cat == null) return

        val grouped = cat.items.groupBy { it.group ?: "Outros" }
        grouped.forEach { (gname, list) ->
            val a = ArrayObjectAdapter(HomePresenter())
            list.forEach { a.add(it) }
            rowsAdapter.add(ListRow(HeaderItem(gname), a))
        }
    }
}
