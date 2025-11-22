package com.mastertv.app.ui.player

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mastertv.app.databinding.PlayerFullscreenBinding

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: PlayerFullscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = PlayerFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra("url") ?: return

        val player = PlayerManager.getPlayer(this)
        binding.fullPlayerView.player = player
        PlayerManager.play(this, url)
    }

    override fun onStop() {
        super.onStop()
        // não libera para manter mini-player funcionando
    }
}
