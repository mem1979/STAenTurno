package com.sta.staenturno.util

import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

/**
 * Convierte cualquier excepción en un mensaje claro para el usuario final.
 */
fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> when (code()) {
        400 -> "Solicitud inválida. Verifica tu dispositivo o los datos enviados."
        401 -> "Usuario o contraseña incorrectos."
        403 -> "Acceso denegado. No tienes permiso para continuar."
        404 -> "El usuario no existe o no está habilitado."
        500 -> "Error interno del servidor. Intenta más tarde."
        else -> "Error del servidor (código ${code()})."
    }

    is UnknownHostException, is IOException ->
        "Sin conexión a Internet. Revisa tus datos o Wi-Fi."

    else -> localizedMessage ?: "Error inesperado."
}

class UserCancelledException : Exception("La verificación fue cancelada.")
class BiometricMismatchException : Exception("Huella o PIN incorrectos.")