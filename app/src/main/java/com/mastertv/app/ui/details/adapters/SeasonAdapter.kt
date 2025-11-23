package com.mastertv.app.ui.details.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mastertv.app.R

class SeasonAdapter(
    private val seasons: List<String>,
    private val onSelect: (position: Int) -> Unit
) : RecyclerView.Adapter<SeasonAdapter.VH>() {

    var selected = 0

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tv: TextView = v.findViewById(R.id.tvSeason)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_season, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.tv.text = seasons[position]
        holder.itemView.isFocusable = true
        holder.itemView.setOnClickListener {
            val prev = selected
            selected = position
            notifyItemChanged(prev)
            notifyItemChanged(selected)
            onSelect(position)
        }
        holder.tv.alpha = if (position == selected) 1.0f else 0.6f
    }

    override fun getItemCount(): Int = seasons.size
}
