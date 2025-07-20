package com.sta.staenturno.data.local

import android.content.Context
import android.provider.Settings

/**
 * Provee el identificador único del dispositivo (ANDROID_ID).
 * Este valor se genera cuando el dispositivo se enciende por primera vez
 * y solo cambia tras un “factory reset”, por lo que es suficientemente estable
 * para usarlo como X-Device-ID en nuestras peticiones.
 *
 * Uso típico (en un ViewModel, por ejemplo):
 * ```kotlin
 * val deviceId = DeviceIdProvider(context).getDeviceId()
 * ```
 */
class DeviceIdProvider(
    private val context: Context   // Inyectaremos el contexto de aplicación
) {

    /** Obtiene el ANDROID_ID o devuelve "" si por algún motivo es null. */
    fun getDeviceId(): String =
        Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: ""
}

