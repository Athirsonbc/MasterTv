package com.mastertv.app.ui.player

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.mastertv.app.R

class PlayerFragment : Fragment() {

    private var player: ExoPlayer? = null
    private var streamUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        streamUrl = arguments?.getString("url")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.player_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        initPlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initPlayer() {
        val url = streamUrl ?: return
        val playerView = view?.findViewById<PlayerView>(R.id.playerView)

        player = ExoPlayer.Builder(requireContext()).build()
        playerView?.player = player

        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }

    companion object {
        fun newInstance(url: String): PlayerFragment {
            val frag = PlayerFragment()
            val bundle = Bundle()
            bundle.putString("url", url)
            frag.arguments = bundle
            return frag
        }
    }
}
