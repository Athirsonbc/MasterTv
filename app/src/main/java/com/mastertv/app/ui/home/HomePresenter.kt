package com.mastertv.app.ui.home

import android.content.Intent
import android.view.ViewGroup
import android.widget.ImageView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.mastertv.app.models.Channel
import com.mastertv.app.ui.player.PlayerActivity

class HomePresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val img = ImageView(parent.context)
        img.layoutParams = ViewGroup.LayoutParams(260, 260)
        img.scaleType = ImageView.ScaleType.CENTER_CROP
        return ViewHolder(img)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as Channel
        val image = viewHolder.view as ImageView

        Glide.with(image.context)
            .load(channel.logo)
            .into(image)

        image.setOnClickListener {
            val ctx = image.context
            val i = Intent(ctx, PlayerActivity::class.java)
            i.putExtra("url", channel.url)
            ctx.startActivity(i)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}
