package com.sta.staenturno.ui.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sta.staenturno.data.local.TokenSecureStorage
import com.sta.staenturno.data.remote.FichajesApiService
import com.sta.staenturno.data.remote.RetrofitModule
import com.sta.staenturno.domain.repository.FichajesRepositoryImpl
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(nav: NavController) {

    /* ---------- DI exprés ---------- */
    val ctx = LocalContext.current
    val vm: HistorialViewModel = viewModel {
        val api  = RetrofitModule.retrofit.create(FichajesApiService::class.java)
        val repo = FichajesRepositoryImpl(api)
        HistorialViewModel(repo, TokenSecureStorage(ctx))
    }
    val state by vm.state.collectAsState()

    /* ---------- UI ---------- */
    Scaffold(
        topBar = {
            TopAppBar(
                /* Título reactivo */
                title = {
                    val locale = Locale.getDefault()
                    val mesTxt = state.mesActual.month
                        .getDisplayName(TextStyle.FULL, locale)
                        .replaceFirstChar { it.uppercase(locale) }
                    Text("$mesTxt ${state.mesActual.year}")
                },
                navigationIcon = {
                    IconButton(onClick = { nav.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = vm::prevMes) {
                        Icon(Icons.Default.ChevronLeft, "Mes anterior")
                    }
                    IconButton(
                        onClick = vm::nextMes,
                        enabled = state.mesActual < YearMonth.now()
                    ) {
                        Icon(Icons.Default.ChevronRight, "Mes siguiente")
                    }
                }
            )
        }
    ) { pad ->

        when {
            state.loading -> Box(
                Modifier.fillMaxSize().padding(pad),
                Alignment.Center
            ) { CircularProgressIndicator() }

            state.error != null -> Box(
                Modifier.fillMaxSize().padding(pad),
                Alignment.Center
            ) { Text(state.error!!) }

            else -> {
                /* ------- Preparamos filas ------- */
                val filas = remember(state.dias) {
                    val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    state.dias.toSortedMap().map { (fechaIso, fichadas) ->
                        val ent = fichadas.firstOrNull { it.tipo == "ENTRADA" }?.hora ?: "–"
                        val sal = fichadas.lastOrNull  { it.tipo == "SALIDA"  }?.hora ?: "–"
                        val fechaPretty = LocalDate.parse(fechaIso).format(fmt)
                        Triple(fechaPretty, ent, sal)
                    }.reversed()   // fechas descendente
                }

                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(pad)
                        .padding(horizontal = 12.dp)
                ) {
                    /* Cabecera */
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(vertical = 6.dp)
                        ) {
                            CeldaCabecera("Fecha", 0.40f)
                            CeldaCabecera("Hora Entrada", 0.30f)
                            CeldaCabecera("Hora Salida", 0.30f)
                        }
                    }

                    /* Filas de días */
                    items(filas) { (fecha, ent, sal) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CeldaDato(fecha, 0.40f)
                            CeldaDato(ent,   0.30f)
                            CeldaDato(sal,   0.30f)
                        }
                    }
                }
            }
        }
    }
}

/* ---------- Helpers de estilo ---------- */

@Composable
private fun RowScope.CeldaCabecera(text: String, peso: Float) =
    Text(
        text,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.weight(peso)
    )

@Composable
private fun RowScope.CeldaDato(text: String, peso: Float) =
    Text(
        text,
        modifier = Modifier.weight(peso)
    )
