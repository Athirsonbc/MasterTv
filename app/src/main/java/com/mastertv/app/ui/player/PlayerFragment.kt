package com.mastertv.app.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.ui.StyledPlayerView
import androidx.fragment.app.Fragment
import com.mastertv.app.R

class PlayerFragment : Fragment() {

    private var url: String? = null
    private var playerView: StyledPlayerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = arguments?.getString("url")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.player_mini, container, false)
        playerView = v.findViewById(com.mastertv.app.R.id.miniPlayerView)
        return v
    }

    override fun onStart() {
        super.onStart()
        url?.let {
            val player = PlayerManager.getPlayer(requireContext())
            playerView?.player = player
            PlayerManager.play(requireContext(), it)
        }
    }

    override fun onStop() {
        super.onStop()
        // keep player alive for mini-player use; do not release here
    }

    companion object {
        fun newInstance(url: String): PlayerFragment {
            val f = PlayerFragment()
            val b = Bundle()
            b.putString("url", url)
            f.arguments = b
            return f
        }
    }
}
