package com.example.flora1.data.auth

import com.example.flora1.core.network.FloraApi
import com.example.flora1.core.network.post
import com.example.flora1.domain.util.DataError
import com.example.flora1.domain.util.Result
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.request.forms.FormDataContent
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class DefaultLoginService(
    override val httpClientEngine: HttpClientEngine? = null,
) : LoginService, FloraApi {
    override val contentType: ContentType
        get() = ContentType.Application.FormUrlEncoded

    override suspend fun login(
        username: String,
        password: String
    ): Result<LoginResponse, DataError.Network> = post(
        endpoint = "/login",
        body = FormDataContent(Parameters.build {
            append("username", username)
            append("password", password)
        })
    )
}

@Serializable
data class LoginResponse(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("token_type")
    val tokenType: String? = null,
)
