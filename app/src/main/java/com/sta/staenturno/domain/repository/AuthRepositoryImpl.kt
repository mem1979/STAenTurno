package com.sta.staenturno.domain.repository

import com.sta.staenturno.data.local.TokenSecureStorage
import com.sta.staenturno.data.remote.AuthApiService
import com.sta.staenturno.data.local.remote.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementación concreta del repositorio de autenticación.
 *
 * • login()  → llama al endpoint /auth/login y guarda el token JWT.
 * • changePassword() → actualiza la contraseña en el backend.
 */
class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val storage: TokenSecureStorage
) : AuthRepository {

    /** Autentica al usuario y devuelve el cuerpo completo de la respuesta. */
    override suspend fun login(
        usuario: String,
        password: String,
        deviceId: String
    ): Result<LoginResponse> =
        runCatching {
            val response = withContext(Dispatchers.IO) {
                api.login(deviceId, usuario, password)   // ← form-urlencoded
            }
            // Guardamos el JWT independientemente de si requiere cambio de clave
            storage.saveToken(response.token, response.expira)
            response
        }

    /** Cambia la contraseña del usuario autenticado. */
    override suspend fun changePassword(
        token: String,
        nueva: String
    ): Result<Unit> =
        runCatching {
            withContext(Dispatchers.IO) {
                api.changePassword("Bearer $token", nueva)
            }
            Unit
        }
}


