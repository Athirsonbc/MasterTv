package com.mastertv.app.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mastertv.app.auth.AuthManager
import com.mastertv.app.data.ContentRepository
import com.mastertv.app.util.DeviceUtil
import com.mastertv.app.util.QrUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SettingsFragment : Fragment() {

    private lateinit var auth: AuthManager
    private val repo = ContentRepository()

    // DNS internal list (not shown)
    private val dnsList = listOf("http://omnixcdn.online:80", "http://cirtzh.xyz")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View? {
        return inflater.inflate(com.mastertv.app.R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = AuthManager(requireContext())

        val tvUser = view.findViewById<TextView>(com.mastertv.app.R.id.tvLoginUser)
        val tvPass = view.findViewById<TextView>(com.mastertv.app.R.id.tvLoginPass)
        val tvExpiry = view.findViewById<TextView>(com.mastertv.app.R.id.tvExpiry)
        val btnReload = view.findViewById<Button>(com.mastertv.app.R.id.btnReload)
        val btnReloadSwapDns = view.findViewById<Button>(com.mastertv.app.R.id.btnReloadSwapDns)
        val btnPlanos = view.findViewById<Button>(com.mastertv.app.R.id.btnPlanos)
        val ivQr = view.findViewById<ImageView>(com.mastertv.app.R.id.ivQr)
        val btnLogout = view.findViewById<Button>(com.mastertv.app.R.id.btnLogout)

        tvUser.text = "Usuário: ${auth.getUsername() ?: \"—\"}"
        tvPass.text = "Senha: ${auth.getPassword() ?: \"—\"}"
        tvExpiry.text = "Expira em: ${formatExpiry(auth)}"

        btnReload.setOnClickListener {
            // recarrega sem trocar DNS (usa preferida)
            CoroutineScope(Dispatchers.Main).launch {
                // chama re-load (conteúdo será recarregado pelo repository)
                // usando preferred DNS (sem mostrar)
                val ctx = requireContext()
                repo.fetchRawM3U(ctx, null) // descarrega e ignora retorno aqui
                // atualizar UI feedback
                tvExpiry.text = "Expira em: ${formatExpiry(auth)}"
            }
        }

        btnReloadSwapDns.setOnClickListener {
            // troca DNS (faz um toggle interno entre 0 e 1) e recarrega
            CoroutineScope(Dispatchers.Main).launch {
                val current = ContentRepository.getPreferredDns()
                val idx = if (current.contains("omnixcdn")) 1 else 0
                ContentRepository.setPreferredDns(idx)
                repo.fetchRawM3U(requireContext(), idx)
                tvExpiry.text = "Expira em: ${formatExpiry(auth)}"
            }
        }

        btnPlanos.setOnClickListener {
            // Criar mensagem com device code e instruções
            val deviceCode = DeviceUtil.getDeviceCode(requireContext())
            val message = Uri.encode("Oi, vim pela TV (código: $deviceCode). Quero informações sobre Planos e renovação. Meu usuário atual: ${auth.getUsername() ?: "—"}")
            val waLink = "https://wa.me/5587988244391?text=$message"

            // tenta abrir WhatsApp, senão mostra QR para escanear
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waLink))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                startActivity(intent)
            } catch (e: Exception) {
                // mostrar QR para o usuário escanear com o celular
                ivQr.setImageBitmap(QrUtil.generateQrBitmap(waLink, 600))
                ivQr.visibility = View.VISIBLE
            }
        }

        btnLogout.setOnClickListener {
            auth.logout()
            // voltar para Login (assumimos que atividade controla navegação)
            requireActivity().recreate()
        }
    }

    private fun formatExpiry(auth: AuthManager): String {
        val ts = try { auth.getTestGeneratedAt() } catch (e: Exception) { 0L }
        if (ts == 0L) return "—"
        val expiry = ts + 4 * 3600 * 1000L
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(expiry))
    }
}
