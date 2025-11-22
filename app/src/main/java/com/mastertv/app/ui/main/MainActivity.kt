package com.mastertv.app.ui.main

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.mastertv.app.ui.home.HomeBrowseFragment

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, HomeBrowseFragment())
            .commit()
    }
}
