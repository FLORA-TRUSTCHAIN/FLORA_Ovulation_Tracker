package com.example.flora1.domain.auth

import com.example.flora1.data.auth.LoginResponse
import com.example.flora1.data.auth.LoginService
import com.example.flora1.domain.util.map
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val loginService: LoginService,
) {
    suspend fun login(
        username: String,
        password: String,
    ) = loginService.login(
        username = username,
        password = password
    ).map { it.toToken() }
}

fun LoginResponse.toToken() = accessToken
