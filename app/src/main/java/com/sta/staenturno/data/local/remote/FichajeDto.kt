// data/local/remote/FichajeDto.kt
package com.sta.staenturno.data.local.remote

/**
 * Información de una fichada puntual.
 */
data class FichajeDto(
    val hora: String,   // “08:11”
    val tipo: String    // ENTRADA, SALIDA, UBICACION…
)

/**
 * Respuesta completa del endpoint /asistencia/mes.
 *
 *  ejemplo de payload:
 *  {
 *    "empleado": "jperez",
 *    "mes": "2025-07",
 *    "dias": {
 *       "2025-07-01": [ { "hora": "08:02", "tipo": "ENTRADA" }, … ],
 *       …
 *    }
 *  }
 */
data class HistorialMensualResponse(
    val empleado: String,
    val mes: String,
    val dias: Map<String, List<FichajeDto>>
)
