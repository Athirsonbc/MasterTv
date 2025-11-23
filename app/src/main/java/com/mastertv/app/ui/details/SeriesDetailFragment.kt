package com.mastertv.app.ui.details

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.mastertv.app.R
import com.mastertv.app.models.Channel
import com.mastertv.app.models.Episode
import com.mastertv.app.ui.details.adapters.EpisodeAdapter
import com.mastertv.app.ui.details.adapters.SeasonAdapter
import com.mastertv.app.ui.player.PlayerActivity

class SeriesDetailFragment : Fragment() {

    companion object {
        const val ARG_CHANNEL = "arg_channel_series"
    }

    private val gson = Gson()
    private var channel: Channel? = null

    private lateinit var imgBg: ImageView
    private lateinit var imgPoster: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvMeta: TextView
    private lateinit var tvSynopsis: TextView
    private lateinit var btnPlayFirst: Button
    private lateinit var rvSeasons: RecyclerView
    private lateinit var rvEpisodes: RecyclerView

    private lateinit var seasonAdapter: SeasonAdapter
    private lateinit var episodeAdapter: EpisodeAdapter

    private var seasons: List<String> = emptyList()
    private var episodesBySeason: Map<Int, List<Episode>> = mapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = arguments?.getString(ARG_CHANNEL)
        if (!json.isNullOrEmpty()) {
            channel = gson.fromJson(json, Channel::class.java)
        }
        buildSeasonsFromChannel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_detail_series, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imgBg = view.findViewById(R.id.imgBgSeries)
        imgPoster = view.findViewById(R.id.imgPosterSeries)
        tvTitle = view.findViewById(R.id.tvSeriesTitle)
        tvMeta = view.findViewById(R.id.tvSeriesMeta)
        tvSynopsis = view.findViewById(R.id.tvSeriesSynopsis)
        btnPlayFirst = view.findViewById(R.id.btnPlayFirst)
        rvSeasons = view.findViewById(R.id.rvSeasonsSeries)
        rvEpisodes = view.findViewById(R.id.rvEpisodesSeries)

        // bind
        tvTitle.text = channel?.name ?: "Série"
        tvMeta.text = channel?.group ?: ""
        tvSynopsis.text = "Sinopse não disponível."

        // load images (poster & background). background is dimmed via xml alpha
        try {
            Glide.with(requireContext()).load(channel?.logo).into(imgPoster)
            Glide.with(requireContext()).load(channel?.logo).centerCrop().into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    imgBg.setImageDrawable(resource)
                    // optionally blur could be applied if transformation lib available
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
        } catch (e: Exception) {}

        // seasons recycler
        seasonAdapter = SeasonAdapter(seasons) { pos ->
            val eps = episodesBySeason[pos] ?: emptyList()
            episodeAdapter.update(eps)
        }
        rvSeasons.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvSeasons.adapter = seasonAdapter

        // episodes recycler
        episodeAdapter = EpisodeAdapter(emptyList()) { ep, idx ->
            // preview behavior: update background and show ephemeral preview (here we just update background & title)
            // open full player when user presses the "Assistir" button or clicks again
            // we open PlayerActivity with episodes list
            val epsList = episodesBySeason[seasonAdapter.selected] ?: emptyList()
            val epsJson = Gson().toJson(epsList)
            val i = Intent(requireContext(), PlayerActivity::class.java)
            i.putExtra("episodesJson", epsJson)
            i.putExtra("index", idx)
            i.putExtra("url", ep.url)
            startActivity(i)
        }
        rvEpisodes.layoutManager = LinearLayoutManager(requireContext())
        rvEpisodes.adapter = episodeAdapter

        // initial
        if (seasons.isNotEmpty()) episodeAdapter.update(episodesBySeason[0] ?: emptyList())

        btnPlayFirst.setOnClickListener {
            // play first episode of first season
            val first = episodesBySeason[0]?.getOrNull(0) ?: return@setOnClickListener
            val list = episodesBySeason[0] ?: emptyList()
            val i = Intent(requireContext(), PlayerActivity::class.java)
            i.putExtra("episodesJson", Gson().toJson(list))
            i.putExtra("index", 0)
            i.putExtra("url", first.url)
            startActivity(i)
        }
    }

    private fun buildSeasonsFromChannel() {
        // Create placeholder seasons/episodes based on channel; in real case you'll fetch metadata
        seasons = listOf("Temporada 1", "Temporada 2", "Temporada 3")
        val baseUrl = channel?.url ?: ""
        val makeList: (Int)->List<Episode> = { seasonIndex ->
            (1..6).map { ep ->
                Episode(url = baseUrl, title = "T${seasonIndex+1}E$ep", thumb = channel?.logo)
            }
        }
        episodesBySeason = seasons.indices.associateWith { makeList(it) }
    }
}
