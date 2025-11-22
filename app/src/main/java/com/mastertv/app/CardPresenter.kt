package com.mastertv.app

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide

class CardPresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_poster, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val view = viewHolder.view
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val img = view.findViewById<ImageView>(R.id.imgPoster)

        if (item is RowItem) {
            title.text = item.title
            val url = item.posterUrl
            if (!url.isNullOrEmpty()) {
                Glide.with(img.context).load(url).centerCrop().into(img)
            } else {
                img.setImageResource(R.drawable.ic_banner)
            }

            view.setOnClickListener {
                // abrir PlayerActivity
                val ctx = img.context
                val intent = android.content.Intent(ctx, PlayerActivity::class.java)
                intent.putExtra(PlayerActivity.EXTRA_URL, item.posterUrl ?: "") // temporarily use posterUrl if real stream unknown
                ctx.startActivity(intent)
            }
        } else {
            title.text = item.toString()
            img.setImageResource(R.drawable.ic_banner)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
    }
}
