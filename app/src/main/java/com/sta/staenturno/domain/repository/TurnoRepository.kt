package com.sta.staenturno.domain.repository

import com.sta.staenturno.data.remote.DiaTurnoDto      // ← corrige aquí

interface TurnoRepository {
    suspend fun obtenerTurnoHoy(jwt: String): Result<DiaTurnoDto>
}
