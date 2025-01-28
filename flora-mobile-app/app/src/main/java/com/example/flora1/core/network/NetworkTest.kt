package com.example.flora1.core.network

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.serialization.encodeToString

abstract class NetworkTest {
    var request: HttpRequestData? = null

    inline fun <reified T> getHttpClientEngine(
        response: T,
        isSuccessful: Boolean = true
    ) = MockEngine { requestData ->
        request = requestData

        respond(
            content = json.encodeToString(response),
            status = if (isSuccessful) HttpStatusCode.OK else HttpStatusCode.Unauthorized,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    }

}
