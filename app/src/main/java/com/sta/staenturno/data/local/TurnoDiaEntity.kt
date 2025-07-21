// data/local/turno/TurnoDiaEntity.kt
package com.sta.staenturno.data.local.turno

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Guarda un solo día de turno en cache.
 * La clave primaria es fecha = yyyy-MM-dd.
 */
@Entity(tableName = "turno_dia")
data class TurnoDiaEntity(
    @PrimaryKey val fecha: String,
    val desde: String,          // fecha desde del lote (semana)
    val hasta: String,          // fecha hasta
    val laboral: Boolean,
    val descripcion: String?,
    val horaInicio: String?,
    val horaFin: String?
)
