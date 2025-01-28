package com.example.flora1.core.network

import com.example.flora1.core.network.clients.createHttpClient
import com.example.flora1.domain.util.DataError
import com.example.flora1.domain.util.Result
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import kotlin.coroutines.cancellation.CancellationException

interface Api {
    val baseUrl: String

    val httpClientEngine: HttpClientEngine?
        get() = null

    val contentType: ContentType
        get() = ContentType.Application.Json
}

private data class HttpClientKey(
    val baseUrl: String,
    val contentType: ContentType,
    val httpClientEngine: HttpClientEngine?,
)

private val httpClients = mutableMapOf<HttpClientKey, HttpClient>()

val Api.httpClient: HttpClient
    get() = httpClients.getOrPut(HttpClientKey(baseUrl, contentType, httpClientEngine)) {
        createHttpClient(baseUrl, contentType)
    }

suspend inline fun <reified T : Any, reified R> Api.post(
    endpoint: String,
    queryParams: Map<String, String> = emptyMap(),
    multiQueryParams: Map<String, Iterable<String>> = emptyMap(),
    headers: Map<String, String> = emptyMap(),
    body: T,
): Result<R, DataError.Network> = safeCall {
    httpClient.post(endpoint) {
        setQueryParams(
            queryParams = queryParams,
            multiQueryParams = multiQueryParams,
        )
        setHeaders(headers)
        setBody(body)
    }.body()
}

suspend inline fun <reified T> safeCall(execute: () -> HttpResponse): Result<T, DataError.Network> {
    val response = try {
        execute()
    } catch (e: Exception) {
        if (e is CancellationException) throw e
        e.printStackTrace()
        return Result.Error(DataError.Network.UNKNOWN)
    }

    return responseToResult<T>(response)
}

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Network> {
    return when (response.status.value) {
        in 200..299 -> Result.Success(response.body<T>())
        400 -> Result.Error(DataError.Network.BAD_REQUEST)
        401 -> Result.Error(DataError.Network.UNAUTHORIZED)
        408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        409 -> Result.Error(DataError.Network.CONFLICT)
        413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }
}

suspend inline fun <reified T, reified R> Api.put(
    endpoint: String,
    queryParams: Map<String, String> = emptyMap(),
    multiQueryParams: Map<String, Iterable<String>> = emptyMap(),
    headers: Map<String, String> = emptyMap(),
    body: T,
): Result<R, DataError.Network> = safeCall {
    httpClient.put(endpoint) {
        setQueryParams(
            queryParams = queryParams,
            multiQueryParams = multiQueryParams,
        )
        setHeaders(headers)
        setBody(body)
    }.body()
}


suspend inline fun <reified R> Api.get(
    endpoint: String,
    queryParams: Map<String, String> = emptyMap(),
    multiQueryParams: Map<String, Iterable<String>> = emptyMap(),
    headers: Map<String, String> = emptyMap(),
): Result<R, DataError.Network> = safeCall {
    httpClient.get(endpoint) {
        setQueryParams(
            queryParams = queryParams,
            multiQueryParams = multiQueryParams,
        )
        setHeaders(headers)
    }.body()
}


suspend inline fun <reified R> Api.delete(
    endpoint: String,
    queryParams: Map<String, String> = emptyMap(),
    multiQueryParams: Map<String, Iterable<String>> = emptyMap(),
    headers: Map<String, String> = emptyMap(),
): Result<R, DataError.Network> = safeCall {
    httpClient.delete(endpoint) {
        setQueryParams(
            queryParams = queryParams,
            multiQueryParams = multiQueryParams,
        )
        setHeaders(headers)
    }.body()
}


fun HttpRequestBuilder.setQueryParams(
    queryParams: Map<String, String> = emptyMap(),
    multiQueryParams: Map<String, Iterable<String>> = emptyMap(),
) {
    url {
        queryParams.forEach { (name, value) ->
            parameters.append(name, value)
        }
        multiQueryParams.forEach { (name, values) ->
            values.forEach { value ->
                parameters.append(name, value)
            }
        }
    }
}


fun HttpRequestBuilder.setHeaders(
    headers: Map<String, String> = emptyMap(),
) {
    headers {
        headers.forEach { (name, value) ->
            append(name, value)
        }
    }
}

