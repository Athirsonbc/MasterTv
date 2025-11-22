package com.mastertv.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.mastertv.app.auth.AuthManager

class MainActivity : AppCompatActivity() {

    private lateinit var auth: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = AuthManager(this)

        // Se já tem login/teste → vai pra Home
        if (auth.getUsername() != null) {
            openHome()
        } else {
            openLogin()
        }
    }

    fun openLogin() {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, LoginFragment())
        }
    }

    fun openHome() {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, HomeFragment())
        }
    }

    fun openAccount() {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, AccountFragment())
        }
    }
}
