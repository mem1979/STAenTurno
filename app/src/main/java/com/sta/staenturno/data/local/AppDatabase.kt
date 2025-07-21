// data/local/AppDatabase.kt
package com.sta.staenturno.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sta.staenturno.data.local.turno.TurnoDiaDao
import com.sta.staenturno.data.local.turno.TurnoDiaEntity

/**
 * Base de datos Room que almacena únicamente la tabla de “turnos de la semana”.
 * Si más adelante necesitas guardar otras entidades, añádelas al array `entities`.
 */
@Database(
    entities = [TurnoDiaEntity::class],
    version = 1,
    exportSchema = false        // evita generar carpetas de schema
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun turnoDao(): TurnoDiaDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        /**
         * Devuelve una única instancia (singleton) de la BD.
         */
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: build(context).also { INSTANCE = it }
            }

        private fun build(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "sta.db"
            ).build()
    }
}
