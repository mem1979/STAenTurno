// data/remote/TurnoSemanaApiService.kt
package com.sta.staenturno.data.remote      // ← cambiar aquí (quitamos ".local")

import retrofit2.http.GET
import retrofit2.http.Header

interface TurnoSemanaApiService {
    @GET("turno/semana")
    suspend fun getSemana(
        @Header("Authorization") bearer: String
    ): TurnoSemanaResponse
}
