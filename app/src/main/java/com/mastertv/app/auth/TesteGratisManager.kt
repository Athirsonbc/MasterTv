package com.mastertv.app.auth

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.mastertv.app.network.RetrofitClient
import java.lang.Exception

class TesteGratisManager(private val context: Context) {

    private val api = RetrofitClient.create() // baseUrl set to starpainel.site by default
    private val auth = AuthManager(context)

    /**
     * Chama o endpoint de teste grátis fornecido (retornará um Map em JSON).
     * A sua URL fixa (4h) será passada como parâmetro.
     *
     * Exemplo de uso:
     * val result = createFreeTest("https://starpainel.site/api/chatbot/V01p4eR1dO/7loL7VM1XM")
     */
    suspend fun createFreeTest(fullUrl: String): Result<Map<String, Any>> {
        return withContext(Dispatchers.IO) {
            try {
                // Proteção: se já gerou, retorna falha
                if (auth.hasActiveTest()) {
                    return@withContext Result.failure(Exception("Já existe teste ativo"))
                }

                val resp = api.getCreateTest(fullUrl)
                if (resp.isSuccessful) {
                    val body = resp.body() ?: emptyMap()
                    // Exemplo: o body deve conter username/password (ajustaremos quando tivermos resposta real)
                    val username = body["username"]?.toString() ?: ""
                    val password = body["password"]?.toString() ?: ""
                    if (username.isNotEmpty()) {
                        auth.saveCredentials(username, password)
                        auth.markTestGenerated(System.currentTimeMillis())
                    }
                    return@withContext Result.success(body)
                } else {
                    return@withContext Result.failure(Exception("Erro na API: ${resp.code()}"))
                }
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }
    }
}
