package com.example.flora1.data.auth

import com.example.flora1.domain.util.DataError
import com.example.flora1.domain.util.Result
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface RegisterService {
    suspend fun register(
        username: String,
        email: String,
        password: String,
    ): Result<Unit, DataError.Network>
}
