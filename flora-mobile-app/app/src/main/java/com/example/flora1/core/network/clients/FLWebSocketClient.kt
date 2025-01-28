package com.example.flora1.core.network.clients

import com.example.flora1.domain.Preferences
import io.ktor.client.HttpClient
import javax.inject.Inject

class FLWebSocketClient @Inject constructor(
    private val preferences: Preferences,
) : WebSocketClient () {

    override val urlString: String
        get() = "ws://spartacus.ee.duth.gr:7093/ws?token=${preferences.token}"

    override val httpClient: HttpClient
        get() = HttpClientFactory.createForWebSockets()
}
