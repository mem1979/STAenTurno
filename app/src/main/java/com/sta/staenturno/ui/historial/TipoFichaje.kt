// ui/historial/TipoFichaje.kt
package com.sta.staenturno.ui.historial

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Tipos de fichaje que llegan como texto desde el backend.
 * Cada uno expone un icono Material 3 para la UI.
 */
enum class TipoFichaje(val icon: ImageVector) {
    ENTRADA      (Icons.AutoMirrored.Filled.Login),
    SALIDA       (Icons.AutoMirrored.Filled.Logout),
    PAUSA_INICIO (Icons.Default.Pause),
    PAUSA_FIN    (Icons.Default.PlayArrow),
    UBICACION    (Icons.Default.MyLocation),
    MANUAL       (Icons.Default.Edit);

    companion object {
        /** Devuelve el enum; si llega algo desconocido => UBICACION. */
        fun from(text: String) = runCatching { valueOf(text) }.getOrElse { UBICACION }
    }
}
