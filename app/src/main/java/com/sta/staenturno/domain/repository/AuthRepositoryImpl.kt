package com.sta.staenturno.domain.repository

import android.util.Log
import com.sta.staenturno.data.local.TokenSecureStorage
import com.sta.staenturno.data.local.remote.LoginResponse
import com.sta.staenturno.data.remote.AuthApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Repositorio de autenticación con manejo robusto de expiración.
 *
 * • Si el backend envía 0 o un número negativo, se asume “sin fecha” y
 *   se usa ahora + 30 días como expiración de cortesía.
 * • Si son 10 dígitos se interpreta como epoch-seconds y se pasa a ms.
 * • Si son 13 dígitos ya es epoch-millis.
 */
class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val storage: TokenSecureStorage
) : AuthRepository {

    companion object {
        private const val TAG = "AuthRepo"
        private const val SEGUNDOS_UMBRAL = 1_000_000_000_000L   // 2001-09-09 en ms
        private val TREINTA_DIAS_MS = TimeUnit.DAYS.toMillis(30)
    }

    override suspend fun login(
        usuario: String,
        pass: String,
        deviceId: String
    ): Result<LoginResponse> = runCatching {

        Log.d(TAG, "login() → solicitando token...")
        val resp = withContext(Dispatchers.IO) {
            api.login(deviceId, usuario, pass)
        }
        Log.d(TAG, "login() ← expira(raw)=${resp.expira}")

        /* ---------- Normalización a epoch-millis ---------- */
        val expMs = when {
            resp.expira <= 0L -> {                        // 0 o negativo → sin vencimiento
                val provisional = System.currentTimeMillis() + TREINTA_DIAS_MS
                Log.w(TAG, "expira es 0: usando ahora+30 días ($provisional)")
                provisional
            }
            resp.expira < SEGUNDOS_UMBRAL -> {           // 10 dígitos → segundos
                Log.d(TAG, "expira en SEGUNDOS → ×1000")
                resp.expira * 1_000
            }
            else -> {                                    // 13 dígitos → ms
                Log.d(TAG, "expira ya en MILISEGUNDOS")
                resp.expira
            }
        }
        /* -------------------------------------------------- */

        storage.saveToken(resp.token, expMs)
        Log.d(TAG, "Token guardado (expiraMs=$expMs, ahora=${System.currentTimeMillis()})")

        resp
    }.onFailure { e ->
        Log.e(TAG, "login() – error: ${e.localizedMessage}", e)
    }

    override suspend fun changePassword(
        token: String,
        nueva: String
    ): Result<Unit> = runCatching {
        Log.d(TAG, "changePassword() → solicitando cambio...")
        withContext(Dispatchers.IO) { api.changePassword("Bearer $token", nueva) }
        Log.d(TAG, "changePassword() ← contraseña actualizada")
        Unit
    }.onFailure { e ->
        Log.e(TAG, "changePassword() – error: ${e.localizedMessage}", e)
    }
}
