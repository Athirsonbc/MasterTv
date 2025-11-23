package com.mastertv.app.ui.series

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

class SeriesFragment : BrowseSupportFragment() {

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = rowsAdapter
        title = "Séries"
        load()
    }

    private fun load() {
        CoroutineScope(Dispatchers.Main).launch {
            val categories = ContentRepository.loadCategories(requireContext(), filmsLimit = 9999, seriesLimit = 9999)
            val cat = categories.find { it.name == "Séries" }
            build(cat)
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
