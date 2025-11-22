package com.mastertv.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mastertv.app.auth.AuthManager

class AccountFragment : Fragment() {

    private lateinit var auth: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = AuthManager(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_account, container, false)
        val tvUser = root.findViewById<TextView>(R.id.tvUser)
        val tvPass = root.findViewById<TextView>(R.id.tvPass)
        val btnWhats = root.findViewById<Button>(R.id.btnWhats)

        tvUser.text = "Usuário: ${auth.getUsername() ?: \"—\"}"
        tvPass.text = "Senha: ${auth.getPassword() ?: \"—\"}"

        btnWhats.setOnClickListener {
            // abre WhatsApp para renovação (usa seu número salvo nas memórias)
            val number = "+5587988244391"
            val uri = Uri.parse("https://wa.me/${number.replace("+","")}")
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        }

        return root
    }
}
