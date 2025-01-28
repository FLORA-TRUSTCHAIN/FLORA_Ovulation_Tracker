package com.example.flora1.data.auth

import com.example.flora1.core.network.FloraApi
import com.example.flora1.core.network.post
import com.example.flora1.domain.util.DataError
import com.example.flora1.domain.util.Result
import io.ktor.client.engine.HttpClientEngine
import io.ktor.http.ContentType
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class DefaultRegisterService(
    override val httpClientEngine: HttpClientEngine? = null,
) : RegisterService, FloraApi {
    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): Result<Unit, DataError.Network> = post(
        endpoint = "/register",
        body = RegisterBody(
            email = email,
            password = password,
            username = username,
            pre_pk = "string",
            metadata = mapOf()
        )
    )
}

@Serializable
data class RegisterBody(
    @SerialName("username")
    val username: String,

    @SerialName("password")
    val password: String,

    @SerialName("email")
    val email: String,

    @SerialName("pre_pk")
    val pre_pk: String? = null,

    @SerialName("metadata")
    val metadata: Map<String, @Contextual Any?>? = null
)
