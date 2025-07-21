// ui/turno/TurnoViewModel.kt
package com.sta.staenturno.ui.turno

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sta.staenturno.data.remote.DiaTurnoDto
import com.sta.staenturno.domain.repository.TurnoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/* ---------- UI State ---------- */
data class TurnoUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val data: DiaTurnoDto? = null
)

/* ---------- ViewModel ---------- */
class TurnoViewModel(
    private val repo: TurnoRepository,
    private val jwt: String
) : ViewModel() {

    private val _ui = MutableStateFlow(TurnoUiState())
    val ui: StateFlow<TurnoUiState> = _ui

    init {
        obtenerTurnoHoy()
    }

    private fun obtenerTurnoHoy() = viewModelScope.launch {
        _ui.value = TurnoUiState(loading = true)
        val res = repo.obtenerTurnoHoy(jwt)
        _ui.value = if (res.isSuccess) {
            TurnoUiState(data = res.getOrNull())
        } else {
            TurnoUiState(error = res.exceptionOrNull()?.localizedMessage ?: "Error")
        }
    }
}
