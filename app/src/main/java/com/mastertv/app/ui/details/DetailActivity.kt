package com.mastertv.app.ui.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mastertv.app.R

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // layout could show poster, synopsis, episodes list - left as skeleton
        setContentView(R.layout.activity_detail)
    }
}
