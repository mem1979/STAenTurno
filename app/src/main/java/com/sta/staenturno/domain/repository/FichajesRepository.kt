// domain/repository/FichajesRepository.kt
package com.sta.staenturno.domain.repository

import com.sta.staenturno.data.local.remote.HistorialMensualResponse

interface FichajesRepository {
    suspend fun historialMensual(
        token: String,
        anio: Int,
        mes: Int
    ): Result<HistorialMensualResponse>
}
