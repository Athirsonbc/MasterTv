package com.mastertv.app.ui.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class SeriesDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val chJson = intent.getStringExtra("channelJson")
        val frag = SeriesDetailFragment()
        if (chJson != null) {
            val b = Bundle()
            b.putString(SeriesDetailFragment.ARG_CHANNEL, chJson)
            frag.arguments = b
        }
        supportFragmentManager.beginTransaction().replace(android.R.id.content, frag).commit()
    }
}
