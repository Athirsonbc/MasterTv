package com.mastertv.app.ui.details.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mastertv.app.R
import com.mastertv.app.models.Episode
import com.mastertv.app.ui.player.PlayerActivity
import com.google.gson.Gson

class EpisodeAdapter(
    private var items: List<Episode>,
    private val onPlay: (ep: Episode, index:Int)->Unit
) : RecyclerView.Adapter<EpisodeAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgEp)
        val title: TextView = v.findViewById(R.id.tvEpTitle)
        val meta: TextView = v.findViewById(R.id.tvEpMeta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_episode, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ep = items[position]
        holder.title.text = ep.title.ifEmpty { "Episódio ${position+1}" }
        holder.meta.text = "" // placeholder, can set runtime
        Glide.with(holder.img.context).load(ep.thumb).into(holder.img)
        holder.itemView.isFocusable = true

        holder.itemView.setOnClickListener {
            // single click: start mini/full player depending context - here we'll open full player with episodes list
            onPlay(ep, position)
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(list: List<Episode>) {
        items = list
        notifyDataSetChanged()
    }
}
