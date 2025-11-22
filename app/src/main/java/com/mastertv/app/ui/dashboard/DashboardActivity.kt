package com.mastertv.app.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.mastertv.app.R
import com.mastertv.app.ui.home.HomeBrowseFragment
import com.mastertv.app.ui.sections.ChannelsFragment
import com.mastertv.app.ui.sections.MoviesFragment
import com.mastertv.app.ui.sections.SeriesFragment
import com.mastertv.app.ui.settings.SettingsFragment

class DashboardActivity : AppCompatActivity() {\n
    override fun onStart() {
        super.onStart()
        findViewById<TextView>(R.id.menuChannels).setOnClickListener { openChannels() }
        findViewById<TextView>(R.id.menuMovies).setOnClickListener { openMovies() }
        findViewById<TextView>(R.id.menuSeries).setOnClickListener { openSeries() }
        findViewById<TextView>(R.id.menuSettings).setOnClickListener { openSettings() }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // start with Channels first (as requested)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.containerFragment, ChannelsFragment())
            }
        }

        // The menu will call public methods to switch fragments via fragmentManager
    }

    fun openChannels() {
        supportFragmentManager.commit {
            replace(R.id.containerFragment, ChannelsFragment())
        }
    }

    fun openMovies() {
        supportFragmentManager.commit {
            replace(R.id.containerFragment, MoviesFragment())
        }
    }

    fun openSeries() {
        supportFragmentManager.commit {
            replace(R.id.containerFragment, SeriesFragment())
        }
    }

    fun openSettings() {
        supportFragmentManager.commit {
            replace(R.id.containerFragment, SettingsFragment())
        }
    }
}
