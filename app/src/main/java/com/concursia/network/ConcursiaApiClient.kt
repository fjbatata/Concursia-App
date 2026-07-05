package com.concursia.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import com.concursia.BuildConfig

/**
 * Cliente HTTP para comunicação com o backend Concursia Admin.
 *
 * Configuração: defina BASE_URL e API_TOKEN no construtor ou via variáveis.
 */
class ConcursiaApiClient(
    private val baseUrl: String = BuildConfig.API_BASE_URL,
    private val apiToken: String = BuildConfig.API_TOKEN
) {

    data class ApiResult<T>(
        val sucesso: Boolean,
        val data: T? = null,
        val erro: String? = null,
        val statusCode: Int = 200
    )

    /**
     * Registra um novo usuário após compra no Google Play.
     */
    suspend fun registrarUsuario(
        email: String,
        nome: String,
        purchaseToken: String,
        deviceId: String = "",
        telefone: String = ""
    ): ApiResult<Map<String, Any?>> = withContext(Dispatchers.IO) {
        try {
            val json = mapOf(
                "email" to email.lowercase().trim(),
                "nome" to nome,
                "purchase_token" to purchaseToken,
                "device_id" to deviceId,
                "telefone" to telefone
            )
            val response = post("/api/v1/registrar", json)
            ApiResult(sucesso = response.first == 201, data = response.second, statusCode = response.first)
        } catch (e: Exception) {
            ApiResult(sucesso = false, erro = e.message ?: "Erro de conexão")
        }
    }

    /**
     * Verifica se a assinatura do usuário está ativa.
     */
    suspend fun verificarAssinatura(email: String): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val (code, body) = get("/api/v1/verificar/${email.lowercase().trim()}")
            if (code == 200) {
                val ativo = body["ativo"] as? Boolean ?: false
                ApiResult(sucesso = true, data = ativo)
            } else {
                ApiResult(sucesso = false, data = false, erro = body["mensagem"] as? String)
            }
        } catch (e: Exception) {
            ApiResult(sucesso = false, data = false, erro = e.message)
        }
    }

    /**
     * Registra atividade do usuário (simulado, sessão de estudo, login).
     */
    suspend fun registrarAtividade(
        email: String,
        acao: String,
        detalhes: String = "",
        minutos: Int = 0
    ): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val json = mutableMapOf(
                "email" to email.lowercase().trim(),
                "acao" to acao,
                "detalhes" to detalhes
            )
            if (minutos > 0) json["minutos"] = minutos.toString()
            val (code, _) = post("/api/v1/atividade", json)
            ApiResult(sucesso = code == 200, data = code == 200)
        } catch (e: Exception) {
            ApiResult(sucesso = false, erro = e.message)
        }
    }

    /**
     * Busca estatísticas gerais do sistema.
     */
    suspend fun buscarEstatisticas(): ApiResult<Map<String, Any?>> = withContext(Dispatchers.IO) {
        try {
            val (code, body) = get("/api/v1/estatisticas")
            ApiResult(sucesso = code == 200, data = body)
        } catch (e: Exception) {
            ApiResult(sucesso = false, erro = e.message)
        }
    }

    suspend fun obterAtualizacoes(): ApiResult<List<Map<String, Any?>>> = withContext(Dispatchers.IO) {
        try {
            val (code, body) = get("/api/v1/atualizacoes")
            val list = if (body.containsKey("atualizacoes")) {
                @Suppress("UNCHECKED_CAST")
                (body["atualizacoes"] as? List<Map<String, Any?>>) ?: emptyList()
            } else emptyList()
            ApiResult(sucesso = code == 200, data = list, statusCode = code)
        } catch (e: Exception) {
            ApiResult(sucesso = false, erro = e.message)
        }
    }

    // ========== INTERNAL ==========

    private suspend fun get(path: String): Pair<Int, Map<String, Any?>> {
        val url = URL("$baseUrl$path")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("X-API-Token", apiToken)
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        return parseResponse(conn)
    }

    private suspend fun post(path: String, body: Map<String, Any?>): Pair<Int, Map<String, Any?>> {
        val url = URL("$baseUrl$path")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("X-API-Token", apiToken)
        conn.connectTimeout = 10000
        conn.readTimeout = 10000

        val jsonBody = buildJsonString(body)
        OutputStreamWriter(conn.outputStream).use { it.write(jsonBody) }
        return parseResponse(conn)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseResponse(conn: HttpURLConnection): Pair<Int, Map<String, Any?>> {
        val code = conn.responseCode
        val body = conn.inputStream?.bufferedReader()?.readText()
            ?: conn.errorStream?.bufferedReader()?.readText()
            ?: "{}"
        val parsed = org.json.JSONObject(body)
        val map = mutableMapOf<String, Any?>()
        parsed.keys().forEach { key -> map[key] = parsed.get(key) }
        return code to map
    }

    private fun buildJsonString(map: Map<String, Any?>): String {
        val sb = StringBuilder("{")
        map.entries.forEachIndexed { i, (k, v) ->
            if (i > 0) sb.append(",")
            sb.append("\"$k\":")
            when (v) {
                is String -> sb.append("\"${v.replace("\"", "\\\"")}\"")
                is Number, is Boolean -> sb.append(v)
                null -> sb.append("null")
                else -> sb.append("\"$v\"")
            }
        }
        sb.append("}")
        return sb.toString()
    }
}