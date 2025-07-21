// data/local/turno/TurnoDiaDao.kt
package com.sta.staenturno.data.local.turno

import androidx.room.*

@Dao
interface TurnoDiaDao {

    /** Devuelve la fila de la fecha pedida (yyyy-MM-dd) */
    @Query("SELECT * FROM turno_dia WHERE fecha = :fecha LIMIT 1")
    suspend fun getByFecha(fecha: String): TurnoDiaEntity?

    /** Inserta o reemplaza la lista completa de la semana */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dias: List<TurnoDiaEntity>)

    /** Borra todo lo que no pertenezca al rango [desde–hasta] */
    @Query("DELETE FROM turno_dia WHERE fecha < :desde OR fecha > :hasta")
    suspend fun clearOutsideRange(desde: String, hasta: String)
}
