package com.sta.staenturno.ui.login

/**
 * Estado inmutable que representa la pantalla de Login.
 */
data class LoginUiState(
    val usuario: String        = "",
    val password: String       = "",
    val loading:  Boolean      = false,
    val error:    String?      = null,

    /* ---------- Datos de sesión ---------- */
    val token:          String? = null,   // JWT devuelto por /auth/login
    val requiereCambio: Boolean = false,  // true si la contraseña era "1234"
    val success:        Boolean = false   // true cuando el login HTTP fue 200
)
