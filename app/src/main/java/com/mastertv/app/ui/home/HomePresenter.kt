package com.mastertv.app.ui.home

import android.view.ViewGroup
import android.widget.ImageView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.mastertv.app.models.Channel

class HomePresenter : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val img = ImageView(parent.context)
        img.layoutParams = ViewGroup.LayoutParams(250, 250)
        img.scaleType = ImageView.ScaleType.FIT_XY
        return ViewHolder(img)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as Channel
        val image = viewHolder.view as ImageView

        Glide.with(image.context)
            .load(channel.logo)
            .into(image)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {}
}
