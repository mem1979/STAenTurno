package com.sta.staenturno.ui.verification

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

/**
 * Verificador de identidad:
 *   1) Prioriza biometría (huella / rostro).
 *   2) Si no hay hardware o biometrías enroladas, usa la credencial
 *      del dispositivo (patrón, PIN o contraseña).
 */
object IdentityVerifier {

    private const val BIOMETRIC_ONLY =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK

    private const val DEVICE_CREDENTIAL =
        BiometricManager.Authenticators.DEVICE_CREDENTIAL

    /* ---------------------------------------------------------------- */

    fun verify(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val bm = BiometricManager.from(activity)

        when (bm.canAuthenticate(BIOMETRIC_ONLY)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                // Hardware + biometrías disponibles → biometría pura
                launchPrompt(
                    activity      = activity,
                    allowedAuth   = BIOMETRIC_ONLY,
                    title         = "Confirma con tu biometría",
                    negativeText  = "Cancelar",
                    onSuccess     = onSuccess,
                    onError       = onError
                )
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED,
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                // Probamos credencial del dispositivo
                if (bm.canAuthenticate(DEVICE_CREDENTIAL) ==
                    BiometricManager.BIOMETRIC_SUCCESS
                ) {
                    launchPrompt(
                        activity     = activity,
                        allowedAuth  = DEVICE_CREDENTIAL,
                        title        = "Desbloquea tu dispositivo",
                        negativeText = "Cancelar",
                        onSuccess    = onSuccess,
                        onError      = onError
                    )
                } else {
                    onError("Sin métodos de autenticación disponibles")
                }
            }
            else -> onError("Autenticación no disponible")
        }
    }

    /* ---------------------------------------------------------------- */

    private fun launchPrompt(
        activity: FragmentActivity,
        allowedAuth: Int,
        title: String,
        negativeText: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val prompt   = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) = onSuccess()

                override fun onAuthenticationError(
                    code: Int, errString: CharSequence
                ) = onError(errString.toString())

                override fun onAuthenticationFailed() =
                    onError("Intento no reconocido")
            }
        )

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setAllowedAuthenticators(allowedAuth)
            .setNegativeButtonText(negativeText)
            .build()

        prompt.authenticate(info)
    }
}
