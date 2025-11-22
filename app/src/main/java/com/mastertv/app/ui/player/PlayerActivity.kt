package com.mastertv.app.ui.player

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class PlayerActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra("url") ?: return

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, PlayerFragment.newInstance(url))
            .commit()
    }
}
