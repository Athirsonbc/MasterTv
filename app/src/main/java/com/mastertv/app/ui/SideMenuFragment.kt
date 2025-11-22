package com.mastertv.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mastertv.app.MainActivity
import com.mastertv.app.R

class SideMenuFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_side_menu, container, false)

        val btnHome = root.findViewById<TextView>(R.id.btnHome)
        val btnConta = root.findViewById<TextView>(R.id.btnConta)
        val btnSair = root.findViewById<TextView>(R.id.btnSair)

        val activity = requireActivity() as MainActivity

        btnHome.setOnClickListener { activity.openHome() }
        btnConta.setOnClickListener { activity.openAccount() }
        btnSair.setOnClickListener { activity.openLogin() }

        return root
    }
}
