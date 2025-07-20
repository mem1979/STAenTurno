package com.sta.staenturno.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys      // 👈 API de la versión 1.0.0 (deprecada en 1.1)

/**
 * Envuelve EncryptedSharedPreferences para guardar el token JWT de forma segura.
 *
 * ▸ Claves cifradas con AES-256-SIV.
 * ▸ Valores cifrados con AES-256-GCM.
 * ▸ La clave maestra (alias) se almacena en Android Keystore (Hardware-Backed si está disponible).
 *
 *  ───────────────────────────────────────────────────────────────
 *  • saveToken(token, expiraMs)  → guarda token + vencimiento
 *  • getValidToken()            → devuelve token si no caducó
 *  • clear()                    → borra todo (logout)
 *  ───────────────────────────────────────────────────────────────
 */
class TokenSecureStorage(context: Context) {

    companion object {
        private const val PREF_NAME  = "secure_token_prefs"
        private const val KEY_TOKEN  = "jwt_token"
        private const val KEY_EXPIRA = "jwt_expira"
    }

    // 1️⃣ Obtenemos (o creamos) el alias de la Master Key con AES-256-GCM
    private val masterKeyAlias: String =
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)   // API compatible con API 23+:contentReference[oaicite:5]{index=5}

    // 2️⃣ Instanciamos EncryptedSharedPreferences usando el alias
    private val prefs = EncryptedSharedPreferences.create(
        PREF_NAME,                                           // FileName
        masterKeyAlias,                                      // Alias de la key en Keystore
        context,                                             // Contexto
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /** Guarda el token y su expiración (en milisegundos epoch) */
    fun saveToken(token: String, expiraEpochMillis: Long) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_EXPIRA, expiraEpochMillis)
            .apply()
    }

    /**
     * Recupera el token **solo** si no ha caducado.
     * @return JWT o `null` cuando no existe o está vencido.
     */
    fun getValidToken(): String? {
        val token  = prefs.getString(KEY_TOKEN, null)
        val expira = prefs.getLong(KEY_EXPIRA, 0L)
        val vigente = System.currentTimeMillis() < expira
        return if (!token.isNullOrBlank() && vigente) token else null
    }

    /** Borra los datos almacenados (logout) */
    fun clear() = prefs.edit().clear().apply()
}
