package com.mastertv.app.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.mastertv.app.R
import com.mastertv.app.data.ContentRepository
import com.mastertv.app.models.Channel
import com.mastertv.app.ui.home.HomePresenter
import kotlinx.coroutines.*

class ChannelPlayerFragment : Fragment() {

    private lateinit var vm: ChannelPlayerViewModel
    private lateinit var miniPlayer: ExoPlayer
    private val rows = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.player_mini, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm = ViewModelProvider(this)[ChannelPlayerViewModel::class.java]

        miniPlayer = ExoPlayer.Builder(requireContext()).build()
        val playerView = view.findViewById<com.google.android.exoplayer2.ui.PlayerView>(R.id.miniPlayer)
        playerView.player = miniPlayer

        loadChannels()

        // 2 cliques para fullscreen
        playerView.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_container, FullscreenPlayerFragment.new(vm.current.value!!))
                .addToBackStack(null)
                .commit()
        }

        vm.current.observe(viewLifecycleOwner) { ch ->
            if (ch != null) play(ch)
        }
    }

    private fun play(channel: Channel) {
        miniPlayer.setMediaItem(MediaItem.fromUri(channel.url))
        miniPlayer.prepare()
        miniPlayer.playWhenReady = true
    }

    private fun loadChannels() {
        CoroutineScope(Dispatchers.Main).launch {
            val cats = ContentRepository.loadCategories(requireContext())
            val live = cats.find { it.name == "Ao Vivo" } ?: return@launch

            val grouped = live.items.groupBy { it.group ?: "Outros" }

            grouped.forEach { (name, list) ->
                val a = ArrayObjectAdapter(HomePresenter())
                list.forEach { a.add(it) }
                rows.add(ListRow(HeaderItem(name), a))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        miniPlayer.release()
    }
}
