package com.mastertv.app.data

import android.content.Context
import com.mastertv.app.auth.AuthManager
import com.mastertv.app.m3u.M3UParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class ContentItem(
    val title: String,
    val url: String,
    val group: String?,
    val logo: String?
)

data class Category(
    val name: String,
    val items: List<ContentItem>
)

object ContentRepository {

    // DNS internas (NÃO EXIBIR NO UI)
    private val dnsList = listOf(
        "http://omnixcdn.online:80",
        "http://cirtzh.xyz"
    )

    // índice preferido (in-memory); persista se quiser
    private var preferredDnsIndex = 0

    // HTTP client com timeout
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    /** retorna DNS que está preferida (interno) */
    fun getPreferredDns(): String = dnsList.getOrElse(preferredDnsIndex) { dnsList[0] }

    /** trocar preferida por índice (0,1...) - UI chama sem exibir URL */
    fun setPreferredDns(index: Int) {
        if (index in dnsList.indices) preferredDnsIndex = index
    }

    private fun buildM3UUrl(dns: String, username: String, password: String): String {
        val u = URLEncoder.encode(username, "UTF-8")
        val p = URLEncoder.encode(password, "UTF-8")
        return "${dns.trimEnd('/')}/get.php?username=${u}&password=${p}&type=m3u_plus&output=mpegts"
    }

    /** Fetch raw M3U a partir das credenciais salvas. 
     * Se forceDnsIndex fornecido, tenta com ele; se null, tenta preferida e retorna null em falha.
     */
    suspend fun fetchRawM3U(context: Context, forceDnsIndex: Int? = null): String? {
        return withContext(Dispatchers.IO) {
            try {
                val auth = AuthManager(context)
                val user = auth.getUsername() ?: return@withContext null
                val pass = auth.getPassword() ?: return@withContext null

                val tryIndices = if (forceDnsIndex != null) listOf(forceDnsIndex) else listOf(preferredDnsIndex, (preferredDnsIndex + 1) % dnsList.size)

                for (idx in tryIndices) {
                    if (idx !in dnsList.indices) continue
                    val dns = dnsList[idx]
                    val url = buildM3UUrl(dns, user, pass)
                    try {
                        val req = Request.Builder().url(url).get().build()
                        client.newCall(req).execute().use { resp ->
                            if (resp.isSuccessful) {
                                val body = resp.body?.string()
                                if (!body.isNullOrBlank()) return@withContext body
                            }
                        }
                    } catch (_: Exception) {
                        // tenta próximo DNS
                    }
                }
                null
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Carrega e organiza em categorias FILMES, SERIES, CANAIS (others)
     * Limites: filmsLimit, seriesLimit (para Home)
     */
    suspend fun loadCategories(context: Context, filmsLimit: Int = 10, seriesLimit: Int = 10, forceDnsIndex: Int? = null): List<Category> {
        return withContext(Dispatchers.IO) {
            val raw = fetchRawM3U(context, forceDnsIndex) ?: ""
            val entries = M3UParser.parse(raw)
            val films = mutableListOf<ContentItem>()
            val series = mutableListOf<ContentItem>()
            val channels = mutableListOf<ContentItem>()

            val filmKeys = listOf("FILME","FILMES","MOVIE","MOVIES","VOD","CINE","LANÇAMENTO","LANÇAMENTOS")
            val seriesKeys = listOf("SÉRIE","SERIE","SÉRIES","SERIES","SHOW","TEMPORADA","SEASON")

            for (e in entries) {
                val combined = ((e.group ?: "") + " " + e.title).uppercase()
                val item = ContentItem(e.title, e.url, e.group, e.logo)
                when {
                    filmKeys.any { combined.contains(it) } -> films.add(item)
                    seriesKeys.any { combined.contains(it) } -> series.add(item)
                    else -> channels.add(item)
                }
            }

            val filmsOut = if (films.size > filmsLimit) films.subList(0, filmsLimit) else films
            val seriesOut = if (series.size > seriesLimit) series.subList(0, seriesLimit) else series

            val result = mutableListOf<Category>()
            if (filmsOut.isNotEmpty()) result.add(Category("Filmes", filmsOut))
            if (seriesOut.isNotEmpty()) result.add(Category("Séries", seriesOut))
            if (channels.isNotEmpty()) result.add(Category("Ao Vivo", channels))

            result
        }
    }
}
