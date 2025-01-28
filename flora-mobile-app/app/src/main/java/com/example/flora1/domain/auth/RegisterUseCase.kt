package com.example.flora1.domain.auth

import com.example.flora1.data.auth.RegisterService
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val registerService: RegisterService,
) {
    suspend fun register(
        username: String,
        email: String,
        password: String,
    ) = registerService.register(
        username, email, password
    )
}
