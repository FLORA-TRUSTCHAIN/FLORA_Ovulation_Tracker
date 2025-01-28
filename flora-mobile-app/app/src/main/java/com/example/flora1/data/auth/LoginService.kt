package com.example.flora1.data.auth

import com.example.flora1.domain.util.DataError
import com.example.flora1.domain.util.Result

interface LoginService {
    suspend fun login(username: String, password: String): Result<LoginResponse, DataError.Network>
}
