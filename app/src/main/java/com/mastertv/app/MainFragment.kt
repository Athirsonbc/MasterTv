package com.mastertv.app

import android.os.Bundle
import android.util.TypedValue
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.lifecycleScope
import com.mastertv.app.data.ContentRepository
import com.mastertv.app.data.ContentItem
import kotlinx.coroutines.launch

class MainFragment : BrowseSupportFragment() {

    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Master TV"
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        setupUIElements()
        adapter = rowsAdapter

        // Carregar conteúdo (async)
        lifecycleScope.launch {
            loadAndPopulate()
        }
    }

    private fun setupUIElements() {
        brandColor = resources.getColor(R.color.purple_700, requireContext().theme)
        val metrics = resources.displayMetrics
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250f, metrics).toInt()
        headersFragment?.view?.layoutParams?.width = px
    }

    private suspend fun loadAndPopulate() {
        // Limites: 10 filmes, 10 séries
        val categories = ContentRepository.loadCategories(requireContext(), 10, 10, null)
        // limpar
        rowsAdapter.clear()
        val cardPresenter = CardPresenter()
        categories.forEachIndexed { idx, cat ->
            val header = HeaderItem(idx.toLong(), cat.name)
            val adapter = ArrayObjectAdapter(cardPresenter)
            cat.items.forEach { it ->
                adapter.add(RowItem(it.title, it.logo ?: null))
            }
            rowsAdapter.add(ListRow(header, adapter))
        }
    }
}
