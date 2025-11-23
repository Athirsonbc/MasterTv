package com.mastertv.app.ui.player

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mastertv.app.R
import com.mastertv.app.models.Episode
import kotlinx.coroutines.*

class PlayerActivity : AppCompatActivity() {

    private var playerView: StyledPlayerView? = null
    private var player: ExoPlayer? = null

    private var btnSkipIntro: Button? = null
    private var nextOverlay: View? = null
    private var tvNextLabel: TextView? = null
    private var imgNextThumb: ImageView? = null
    private var tvNextTitle: TextView? = null
    private var btnPlayNextNow: Button? = null
    private var tvOverlayTitle: TextView? = null
    private var tvOverlaySubtitle: TextView? = null

    private val gson = Gson()
    private var episodes: List<Episode> = emptyList()
    private var index: Int = 0

    private var countdownJob: Job? = null
    private var endedFlag = false
    private var skipIntroVisible = false
    private var skipIntroJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate overlay layout; ensure a player view exists as first child
        setContentView(R.layout.player_fullscreen_wrapper)

        // find views
        playerView = findViewById(R.id.fullPlayerView)
        tvOverlayTitle = findViewById(R.id.tvOverlayTitle)
        tvOverlaySubtitle = findViewById(R.id.tvOverlaySubtitle)
        btnSkipIntro = findViewById(R.id.btnSkipIntro)
        nextOverlay = findViewById(R.id.nextOverlay)
        tvNextLabel = findViewById(R.id.tvNextLabel)
        imgNextThumb = findViewById(R.id.imgNextThumb)
        tvNextTitle = findViewById(R.id.tvNextTitle)
        btnPlayNextNow = findViewById(R.id.btnPlayNextNow)

        // read intent
        val url = intent.getStringExtra("url")
        val epsJson = intent.getStringExtra("episodesJson")
        index = intent.getIntExtra("index", 0)

        if (!epsJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<Episode>>() {}.type
            episodes = gson.fromJson(epsJson, type)
        }

        if (url.isNullOrEmpty()) {
            finish(); return
        }

        player = PlayerManager.getPlayer(this)
        playerView?.player = player
        PlayerManager.play(this, url)

        setupPlayerListeners()
        setupSkipIntroBehavior()
        setupNextOverlayBehavior()
        updateOverlayText()
    }

    private fun setupPlayerListeners() {
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if (state == Player.STATE_ENDED) {
                    // only act if truly at the end
                    val pos = player?.currentPosition ?: 0
                    val dur = player?.duration ?: 0
                    if (dur > 0 && pos >= dur - 1000) {
                        onPlaybackActuallyEnded()
                    }
                }
            }
        })
    }

    private fun onPlaybackActuallyEnded() {
        if (endedFlag) return
        endedFlag = true
        // show next overlay only if there is a next episode
        val nextIndex = index + 1
        val next = episodes.getOrNull(nextIndex) ?: run {
            // no next episode — do nothing (or show "replay" controls)
            return
        }
        showNextOverlay(next)
    }

    private fun showNextOverlay(next: Episode) {
        runOnUiThread {
            tvNextTitle?.text = next.title
            try { com.bumptech.glide.Glide.with(this).load(next.thumb).into(imgNextThumb!!) } catch (_: Exception) {}
            nextOverlay?.visibility = View.VISIBLE
            nextOverlay?.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_short))
        }

        // countdown 7 seconds
        countdownJob?.cancel()
        countdownJob = CoroutineScope(Dispatchers.Main).launch {
            var remaining = 7
            while (remaining > 0) {
                tvNextLabel?.text = "Próximo episódio em ${remaining}s..."
                delay(1000)
                remaining--
            }
            // auto-play next
            playNextEpisodeImmediate()
        }
    }

    private fun playNextEpisodeImmediate() {
        countdownJob?.cancel()
        val nextIndex = index + 1
        val next = episodes.getOrNull(nextIndex) ?: return
        index = nextIndex
        // play
        PlayerManager.play(this, next.url)
        // update overlay
        runOnUiThread {
            nextOverlay?.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_short))
            nextOverlay?.visibility = View.GONE
            tvOverlayTitle?.text = next.title
            tvOverlaySubtitle?.text = ""
        }
        endedFlag = false
    }

    private fun setupNextOverlayBehavior() {
        btnPlayNextNow?.setOnClickListener {
            playNextEpisodeImmediate()
        }
    }

    private fun setupSkipIntroBehavior() {
        // only show skip intro if episode duration > 6min, and within first 90s
        btnSkipIntro?.visibility = View.GONE
        skipIntroJob?.cancel()
        skipIntroJob = CoroutineScope(Dispatchers.Main).launch {
            val limit = 90_000L
            val checkInterval = 400L
            val maxChecks = (limit / checkInterval).toInt()
            var checks = 0
            while (checks < maxChecks) {
                val dur = player?.duration ?: -1L
                val pos = player?.currentPosition ?: 0L
                if (dur > 0 && dur < 6 * 60 * 1000L) break // episode too short
                if (pos < limit) {
                    if (!skipIntroVisible) {
                        skipIntroVisible = true
                        btnSkipIntro?.startAnimation(AnimationUtils.loadAnimation(this@PlayerActivity, R.anim.fade_in_short))
                        btnSkipIntro?.visibility = View.VISIBLE
                    }
                } else {
                    if (skipIntroVisible) {
                        btnSkipIntro?.startAnimation(AnimationUtils.loadAnimation(this@PlayerActivity, R.anim.fade_out_short))
                        btnSkipIntro?.visibility = View.GONE
                        skipIntroVisible = false
                    }
                    break
                }
                delay(checkInterval)
                checks++
            }
        }

        btnSkipIntro?.setOnClickListener {
            val p = PlayerManager.getPlayer(this)
            val newPos = (p.currentPosition + 90_000L).coerceAtMost((p.duration.takeIf { it>0 } ?: p.currentPosition + 90_000L))
            p.seekTo(newPos)
            // hide button
            btnSkipIntro?.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out_short))
            btnSkipIntro?.visibility = View.GONE
            skipIntroVisible = false
        }
    }

    private fun updateOverlayText() {
        val current = episodes.getOrNull(index)
        tvOverlayTitle?.text = current?.title ?: ""
        tvOverlaySubtitle?.text = "" // could show season/ep meta
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // handle DPAD center to toggle overlay visibility (common on TV)
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            // if nextOverlay visible, a second press should confirm next now
            if (nextOverlay?.visibility == View.VISIBLE) {
                playNextEpisodeImmediate()
                return true
            }
            // toggle controllers (playerView handles controller visibility)
            playerView?.useController = !(playerView?.useController ?: true)
            return true
        }
        // allow player to handle other keys (volume etc)
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownJob?.cancel()
        skipIntroJob?.cancel()
        // keep global PlayerManager player alive for mini-player; do not release here
    }
}
