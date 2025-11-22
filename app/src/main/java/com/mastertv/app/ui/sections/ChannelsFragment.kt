package com.mastertv.app.ui.sections

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.leanback.widget.*
import com.mastertv.app.data.ContentRepository
import com.mastertv.app.models.Channel
import com.mastertv.app.ui.player.PlayerFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChannelsFragment : Fragment() {

    private val adapter = ArrayObjectAdapter(ListRowPresenter())
    private val repo = ContentRepository()
    private var miniPlayerContainer: FrameLayout? = null

    // double click detection
    private var lastClickTime = 0L
    private var lastClickedUrl: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View? {
        val brows = BrowseSupportFragment()
        // but for simplicity use a container view and programmatic rows
        val root = LinearLayout(inflater.context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        miniPlayerContainer = FrameLayout(inflater.context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 280)
            visibility = View.GONE
        }
        val rowsView = FrameLayout(inflater.context).apply {
            layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,1f)
        }

        root.addView(miniPlayerContainer)
        root.addView(rowsView)

        // programmatic creation of rows (we'll populate later)
        val listRowPresenter = ListRowPresenter()
        val rowsAdapter = adapter
        val lv = RowsSupportFragment()
        childFragmentManager.beginTransaction().replace(rowsView.id, lv).commitNowAllowingStateLoss()

        // populate data async
        CoroutineScope(Dispatchers.Main).launch {
            loadAndPopulate()
        }
        return root
    }

    private suspend fun loadAndPopulate() {
        val dnsItems = repo.loadM3U("http://omnixcdn.online:80/get.php?username=placeholder&password=placeholder&type=m3u_plus&output=mpegts")
        // group by subcategory (group)
        val grouped = dnsItems.groupBy { it.group ?: "Outros" }
        adapter.clear()
        val presenter = object : Presenter() {
            override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
                val tv = TextView(parent.context)
                tv.layoutParams = ViewGroup.LayoutParams(320, 120)
                tv.isFocusable = true
                tv.setPadding(12,12,12,12)
                tv.setBackgroundColor(0xFF111111.toInt())
                return ViewHolder(tv)
            }
            override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
                val ch = item as Channel
                val tv = viewHolder.view as TextView
                tv.text = ch.name
                tv.setOnClickListener {
                    handleItemClick(ch)
                }
            }
            override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
        }

        var id = 0L
        grouped.forEach { (groupName, list) ->
            val header = HeaderItem(id++, groupName)
            val rowAdapter = ArrayObjectAdapter(presenter)
            list.forEach { rowAdapter.add(it) }
            adapter.add(ListRow(header, rowAdapter))
        }
    }

    private fun handleItemClick(ch: Channel) {
        val now = SystemClock.uptimeMillis()
        if (lastClickedUrl == ch.url && now - lastClickTime < 500) {
            // double click -> open full player
            openFullPlayer(ch.url)
            lastClickTime = 0
            lastClickedUrl = null
        } else {
            // single click -> open mini player
            openMiniPlayer(ch.url)
            lastClickTime = now
            lastClickedUrl = ch.url
        }
    }

    private fun openMiniPlayer(url: String) {
        miniPlayerContainer?.removeAllViews()
        val frag = PlayerFragment.newInstance(url)
        childFragmentManager.beginTransaction().replace(miniPlayerContainer!!.id, frag).commitNowAllowingStateLoss()
        miniPlayerContainer?.visibility = View.VISIBLE
    }

    private fun openFullPlayer(url: String) {
        val ctx = requireContext()
        val i = android.content.Intent(ctx, com.mastertv.app.ui.player.PlayerActivity::class.java)
        i.putExtra("url", url)
        startActivity(i)
    }
}
