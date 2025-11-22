package com.mastertv.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mastertv.app.auth.AuthManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LoginFragment : Fragment() {

    private lateinit var auth: AuthManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = AuthManager(requireContext())

        val edtUser = view.findViewById<EditText>(R.id.edtUser)
        val edtPass = view.findViewById<EditText>(R.id.edtPass)
        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val btnTeste = view.findViewById<Button>(R.id.btnTesteGratis)
        val activity = requireActivity() as MainActivity

        // LOGIN NORMAL
        btnLogin.setOnClickListener {
            val u = edtUser.text.toString().trim()
            val p = edtPass.text.toString().trim()

            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha usuário e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.saveCredentials(u, p)
            activity.openHome()
        }

        // TESTE GRÁTIS
        btnTeste.setOnClickListener {
            if (auth.getUsername() != null) {
                Toast.makeText(requireContext(), "Teste já foi usado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            gerarTesteGratis(activity)
        }
    }

    private fun gerarTesteGratis(activity: MainActivity) {
        lifecycleScope.launch {

            val apiUrl = "https://starpainel.site/api/chatbot/V01p4eR1dO/7loL7VM1XM"

            val result = withContext(Dispatchers.IO) {
                try {
                    val url = URL(apiUrl)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.connectTimeout = 5000

                    val response = conn.inputStream.bufferedReader().readText()
                    response
                } catch (e: Exception) {
                    null
                }
            }

            if (result == null) {
                Toast.makeText(requireContext(), "Erro ao gerar teste", Toast.LENGTH_SHORT).show()
                return@launch
            }

            try {
                val json = JSONObject(result)
                val user = json.getString("username")
                val pass = json.getString("password")

                auth.saveCredentials(user, pass)
                Toast.makeText(requireContext(), "Teste Gerado!", Toast.LENGTH_SHORT).show()

                activity.openHome()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro ao ler resposta da API", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
