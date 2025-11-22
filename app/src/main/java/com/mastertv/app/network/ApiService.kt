package com.mastertv.app.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    // Exemplo: endpoint teste grátis (o seu endpoint específico será chamado aqui)
    // A URL que você forneceu: https://starpainel.site/api/chatbot/V01p4eR1dO/7loL7VM1XM
    @GET
    suspend fun getCreateTest(@Url fullUrl: String): Response<Map<String, Any>>

    // Endpoint genérico para autenticação (exemplo)
    @GET("login")
    suspend fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Response<Map<String, Any>>

    // Se precisar baixar M3U por URL completa
    @GET
    suspend fun fetchM3U(@Url m3uUrl: String): Response<String>
}
