// domain/repository/TurnoRepositoryImpl.kt
package com.sta.staenturno.domain.repository

import com.sta.staenturno.data.local.turno.TurnoDiaDao
import com.sta.staenturno.data.local.turno.TurnoDiaEntity
import com.sta.staenturno.data.remote.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class TurnoRepositoryImpl(
    private val api: TurnoSemanaApiService,
    private val dao: TurnoDiaDao
) : TurnoRepository {

    override suspend fun obtenerTurnoHoy(jwt: String): Result<DiaTurnoDto> =
        runCatching {
            val hoy = LocalDate.now().toString()          // yyyy-MM-dd

            // 1) ¿Hay cache para hoy?
            dao.getByFecha(hoy)?.let { return@runCatching it.toDto() }

            // 2) No hay cache → pedimos la semana
            val rem = withContext(Dispatchers.IO) {
                api.getSemana("Bearer $jwt")
            }

            // 3) Persistimos la semana
            val entidades = rem.dias.map { (fecha, dto) ->
                TurnoDiaEntity(
                    fecha = fecha,
                    desde = rem.desde,
                    hasta = rem.hasta,
                    laboral = dto.laboral,
                    descripcion = dto.descripcion,
                    horaInicio = dto.horaInicio,
                    horaFin = dto.horaFin
                )
            }
            dao.insertAll(entidades)
            dao.clearOutsideRange(rem.desde, rem.hasta)   // limpia semanas viejas

            // 4) Devolvemos el día actual (seguro existe ahora)
            rem.dias[hoy] ?: throw IllegalStateException("Sin datos para hoy")
        }

    /* Extensión para mapear Entity → DTO */
    private fun TurnoDiaEntity.toDto() = DiaTurnoDto(
        laboral = laboral,
        descripcion = descripcion,
        horaInicio = horaInicio,
        horaFin = horaFin
    )
}
