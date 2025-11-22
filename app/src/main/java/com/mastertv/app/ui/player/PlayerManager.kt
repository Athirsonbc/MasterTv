package com.mastertv.app.ui.player

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

object PlayerManager {

    private var player: ExoPlayer? = null

    fun getPlayer(context: Context): ExoPlayer {
        if (player == null) {
            player = ExoPlayer.Builder(context).build()
        }
        return player!!
    }

    fun play(context: Context, url: String) {
        val p = getPlayer(context)
        val media = MediaItem.fromUri(Uri.parse(url))
        p.setMediaItem(media)
        p.prepare()
        p.playWhenReady = true
    }

    fun release() {
        player?.release()
        player = null
    }
}
