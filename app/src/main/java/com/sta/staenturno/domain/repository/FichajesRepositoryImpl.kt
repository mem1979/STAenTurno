// domain/repository/FichajesRepositoryImpl.kt
package com.sta.staenturno.domain.repository

import com.sta.staenturno.data.local.remote.HistorialMensualResponse
import com.sta.staenturno.data.remote.FichajesApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FichajesRepositoryImpl(
    private val api: FichajesApiService
) : FichajesRepository {

    override suspend fun historialMensual(
        token: String,
        anio: Int,
        mes: Int
    ): Result<HistorialMensualResponse> = runCatching {
        withContext(Dispatchers.IO) {
            api.getHistorialMensual("Bearer $token", anio, mes)
        }
    }
}
