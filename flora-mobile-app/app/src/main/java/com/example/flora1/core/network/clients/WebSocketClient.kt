package com.example.flora1.core.network.clients

import com.example.flora1.domain.util.DataError
import com.example.flora1.domain.util.Result
import com.example.flora1.features.main.TrainingData
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.SocketException

abstract class WebSocketClient {
    abstract val urlString: String
    abstract val httpClient : HttpClient

    private var session: WebSocketSession? = null

    suspend fun sendMessage(dict: TrainingData) {
        val jsonArray = Json.encodeToString(dict)

        session?.send(Frame.Text(jsonArray))
    }

    suspend fun disconnect() {
        session?.close()
        session = null
    }


    fun listenToSocket(): Flow<Result<String, DataError.Network>> {
        return callbackFlow {
            try {
                session = httpClient.webSocketSession(urlString = urlString)

                session?.let { session ->
                    session
                        .incoming
                        .consumeAsFlow()
                        .filterIsInstance<Frame.Text>()
                        .collect {
                            println(it.readText())
                            send(Result.Success(it.readText()))
                        }
                } ?: run {
                    session?.close()
                    session = null
                    close()
                }
            }
            catch (e: SocketException){ send(Result.Error(DataError.Network.SOCKET_ERROR)) }
            catch (e: Exception){ send(Result.Error(DataError.Network.UNKNOWN)) }

            awaitClose {
                launch(NonCancellable) {
                    session?.close()
                    session = null
                }

            }
        }
    }
}
