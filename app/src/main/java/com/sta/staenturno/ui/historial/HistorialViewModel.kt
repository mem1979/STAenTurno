package com.sta.staenturno.ui.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sta.staenturno.data.local.TokenSecureStorage
import com.sta.staenturno.data.local.remote.FichajeDto
import com.sta.staenturno.domain.repository.FichajesRepository
import com.sta.staenturno.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

data class HistorialUiState(
    val loading: Boolean = false,
    val error:   String? = null,
    val dias:    Map<String, List<FichajeDto>> = emptyMap(),
    val mesActual: YearMonth = YearMonth.now()
)

class HistorialViewModel(
    private val repo: FichajesRepository,
    private val storage: TokenSecureStorage
) : ViewModel() {

    private val _state = MutableStateFlow(HistorialUiState())
    val state: StateFlow<HistorialUiState> = _state

    /* -------- API para la UI -------- */
    fun prevMes() = cargarMes(_state.value.mesActual.minusMonths(1))
    fun nextMes() = cargarMes(_state.value.mesActual.plusMonths(1))

    /** Devuelve algo como “Julio 2025”. */
    fun mesTitulo(): String = _state.value.mesActual
        .month.getDisplayName(TextStyle.FULL, Locale.getDefault()).replaceFirstChar { it.uppercase() } +
            " " + _state.value.mesActual.year

    /* -------- Init -------- */
    init { cargarMes(_state.value.mesActual) }

    /* -------- Interno -------- */
    private fun cargarMes(target: YearMonth) {
        val token = storage.getValidToken() ?: run {
            _state.value = _state.value.copy(error = "Sesión caducada")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                loading   = true,
                error     = null,
                mesActual = target
            )

            val r = repo.historialMensual(token, target.year, target.monthValue)
            _state.value = if (r.isSuccess) {
                _state.value.copy(loading = false, dias = r.getOrThrow().dias)
            } else {
                _state.value.copy(
                    loading = false,
                    error   = r.exceptionOrNull()?.toUserMessage() ?: "Error al cargar"
                )
            }
        }
    }
}

