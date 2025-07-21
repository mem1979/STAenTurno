// data/remote/TurnoSemanaResponse.kt
package com.sta.staenturno.data.remote      // ← mismo cambio

data class TurnoSemanaResponse(
    val desde: String,
    val hasta: String,
    val dias : Map<String, DiaTurnoDto>
)

data class DiaTurnoDto(
    val laboral: Boolean,
    val descripcion: String? = null,
    val horaInicio : String? = null,
    val horaFin    : String? = null
)
