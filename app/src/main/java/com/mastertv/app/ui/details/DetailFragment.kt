package com.mastertv.app.ui.details

import android.content.Intent
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
import com.google.gson.Gson
import com.mastertv.app.R
import com.mastertv.app.models.Channel
import com.mastertv.app.models.Episode
import com.mastertv.app.ui.details.adapters.EpisodeAdapter
import com.mastertv.app.ui.details.adapters.SeasonAdapter
import com.mastertv.app.ui.player.PlayerActivity

class DetailFragment : Fragment() {

    companion object {
        const val ARG_CHANNEL = "arg_channel" // JSON of Channel
    }

    private val gson = Gson()
    private var channel: Channel? = null

    private lateinit var imgBackground: ImageView
    private lateinit var imgPoster: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvMeta: TextView
    private lateinit var tvSynopsis: TextView
    private lateinit var btnWatch: Button
    private lateinit var rvSeasons: RecyclerView
    private lateinit var rvEpisodes: RecyclerView
    private lateinit var episodeAdapter: EpisodeAdapter
    private lateinit var seasonAdapter: SeasonAdapter

    // model placeholders
    private var seasons: List<String> = listOf()
    private var episodesBySeason: Map<Int, List<Episode>> = mapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = arguments?.getString(ARG_CHANNEL)
        if (!json.isNullOrEmpty()) {
            channel = gson.fromJson(json, Channel::class.java)
        }
        // For now build placeholder seasons/episodes from channel url (in real app fetch metadata)
        buildPlaceholderSeasons()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_detail_netflix, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        imgBackground = view.findViewById(R.id.imgBackground)
        imgPoster = view.findViewById(R.id.imgPoster)
        tvTitle = view.findViewById(R.id.tvTitle)
        tvMeta = view.findViewById(R.id.tvMeta)
        tvSynopsis = view.findViewById(R.id.tvSynopsis)
        btnWatch = view.findViewById(R.id.btnWatch)
        rvSeasons = view.findViewById(R.id.rvSeasons)
        rvEpisodes = view.findViewById(R.id.rvEpisodes)

        // bind UI
        tvTitle.text = channel?.name ?: "Título"
        tvMeta.text = "" // could fill year/duration
        tvSynopsis.text = channel?.group ?: "Sinopse não disponível."

        // load images
        try {
            Glide.with(requireContext()).load(channel?.logo).into(imgPoster)
            Glide.with(requireContext()).load(channel?.logo).centerCrop().into(imgBackground)
        } catch (e: Exception) {}

        // Seasons recycler
        seasonAdapter = SeasonAdapter(seasons) { pos ->
            // update episodes list
            val eps = episodesBySeason[pos] ?: emptyList()
            episodeAdapter.update(eps)
        }
        rvSeasons.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvSeasons.adapter = seasonAdapter

        // Episodes recycler
        episodeAdapter = EpisodeAdapter(emptyList()) { ep, idx ->
            // open PlayerActivity full screen with episodesJson + index
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

        // initial episodes
        if (seasons.isNotEmpty()) {
            episodeAdapter.update(episodesBySeason[0] ?: emptyList())
        }

        btnWatch.setOnClickListener {
            // if single movie/stream, play directly
            val url = channel?.url ?: return@setOnClickListener
            val i = Intent(requireContext(), PlayerActivity::class.java)
            i.putExtra("url", url)
            startActivity(i)
        }
    }

    private fun buildPlaceholderSeasons() {
        // create 2 seasons as placeholder with 5 eps each, episodes use channel.url as stream (for placeholder)
        seasons = listOf("Temporada 1", "Temporada 2")
        val eps1 = (1..5).map {
            Episode(url = channel?.url ?: "", title = "Episódio $it", thumb = channel?.logo)
        }
        val eps2 = (1..4).map {
            Episode(url = channel?.url ?: "", title = "Episódio $it", thumb = channel?.logo)
        }
        episodesBySeason = mapOf(0 to eps1, 1 to eps2)
    }
}
