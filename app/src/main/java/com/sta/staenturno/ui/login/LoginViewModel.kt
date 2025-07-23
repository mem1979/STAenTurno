package com.sta.staenturno.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sta.staenturno.data.local.DeviceIdProvider
import com.sta.staenturno.domain.repository.AuthRepository
import com.sta.staenturno.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Maneja el flujo de inicio de sesión.
 *
 * • Valida campos vacíos.
 * • Llama al repositorio; guarda el token en Storage (lo hace el repo).
 * • Si la contraseña es la predeterminada ("1234"), activa
 *   `requiereCambio = true` para que la UI navegue a la pantalla
 *   de cambio de contraseña.
 */
class LoginViewModel(
    private val repo: AuthRepository,
    private val deviceIdProvider: DeviceIdProvider
) : ViewModel() {

    /* -------------------- State -------------------- */

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    /* -------------------- Intents -------------------- */

    fun onUsuarioChange(value: String) {
        _uiState.value = _uiState.value.copy(usuario = value, error = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun onLoginClick() {
        val state = _uiState.value
        if (state.usuario.isBlank() || state.password.isBlank()) {
            showError("Completa usuario y contraseña.")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(loading = true, error = null)

            val result = repo.login(
                usuario  = state.usuario,
                pass     = state.password,
                deviceId = deviceIdProvider.getDeviceId()
            )

            _uiState.value = if (result.isSuccess) {
                val body = result.getOrThrow()
                state.copy(
                    loading        = false,
                    error          = null,
                    success        = true,
                    token          = body.token,
                    requiereCambio = body.passwordDefault
                )
            } else {
                state.copy(
                    loading = false,
                    error   = result.exceptionOrNull()?.toUserMessage() ?: "Error desconocido"
                )
            }
        }
    }

    /* -------------------- Helpers -------------------- */

    /** Expone un error y cancela el progreso. */
    fun showError(msg: String) {
        _uiState.value = _uiState.value.copy(
            loading = false,
            error   = msg
        )
    }
}

