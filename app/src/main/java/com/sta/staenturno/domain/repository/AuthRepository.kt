// domain/repository/AuthRepository.kt
package com.sta.staenturno.domain.repository

import com.sta.staenturno.data.remote.LoginResponse

interface AuthRepository {
    suspend fun login(usuario: String, pass: String, deviceId: String): Result<LoginResponse>
    suspend fun changePassword(token: String, nueva: String): Result<Unit>
}
