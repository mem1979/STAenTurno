// data/remote/FichajesApiService.kt
package com.sta.staenturno.data.remote

import com.sta.staenturno.data.local.remote.HistorialMensualResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface FichajesApiService {

    /** GET /asistencia/mes/{año}/{mes} – mes en 1-12 */
    @GET("asistencia/mes/{anio}/{mes}")
    suspend fun getHistorialMensual(
        @Header("Authorization") bearer: String,
        @Path("anio") anio: Int,
        @Path("mes")  mes: Int
    ): HistorialMensualResponse
}
