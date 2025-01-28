package com.example.flora1.core.network.clients

import com.example.flora1.BuildConfig
import com.example.flora1.core.network.Api
import io.ktor.client.engine.cio.CIO
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun Api.createHttpClient(
    baseUrl: String,
    contentType: ContentType = ContentType.Application.Json,
) = HttpClient {
    defaultRequest {
        contentType(contentType)
        url(baseUrl)
    }
    install(WebSockets)
    install(ContentNegotiation) {
        json(com.example.flora1.core.network.json)
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 15000
    }

    install(Logging) {
        if (BuildConfig.DEBUG)
            level = LogLevel.ALL
    }
}

fun Api.HttpClient(
    clientConfigBlock: HttpClientConfig<*>.() -> Unit,
): HttpClient =
    httpClientEngine?.let {
        HttpClient(
            engine = it,
            block = clientConfigBlock,
        )
    } ?: HttpClient(block = clientConfigBlock)

object HttpClientFactory {

    fun createForWebSockets(): HttpClient {
        return HttpClient(CIO) {
            install(WebSockets)
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}
