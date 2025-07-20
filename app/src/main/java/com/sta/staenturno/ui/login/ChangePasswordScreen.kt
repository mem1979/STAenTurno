package com.sta.staenturno.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sta.staenturno.data.local.TokenSecureStorage
import com.sta.staenturno.data.remote.AuthApiService
import com.sta.staenturno.data.remote.RetrofitModule
import com.sta.staenturno.domain.repository.AuthRepositoryImpl
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(nav: NavController, token: String) {

    /* ---------- DI mínimo ---------- */
    val ctx = LocalContext.current
    val repo = remember {
        val api  = RetrofitModule.retrofit.create(AuthApiService::class.java)
        AuthRepositoryImpl(api, TokenSecureStorage(ctx))
    }
    val scope = rememberCoroutineScope()

    /* ---------- UI state ---------- */
    var pass1   by remember { mutableStateOf("") }
    var pass2   by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error   by remember { mutableStateOf<String?>(null) }

    /* ---------- UI ---------- */
    Scaffold { pad ->
        Column(
            Modifier
                .padding(pad)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {

            Text("Cambia tu contraseña", style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = pass1,
                onValueChange = { pass1 = it },
                label = { Text("Nueva contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = pass2,
                onValueChange = { pass2 = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    error = null
                    when {
                        pass1.length < 8  -> error = "Debe tener al menos 8 caracteres"
                        pass1 != pass2    -> error = "Las contraseñas no coinciden"
                        else -> {
                            loading = true
                            scope.launch {
                                val r = repo.changePassword(token, pass1)
                                loading = false
                                if (r.isSuccess) {
                                    /* ✅ Éxito: mensaje y vuelta a login */
                                    Toast.makeText(
                                        ctx,
                                        "Contraseña actualizada con éxito.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    nav.navigate("login") {
                                        popUpTo(0)          // limpia back-stack
                                        launchSingleTop = true
                                    }
                                } else {
                                    error = r.exceptionOrNull()?.localizedMessage
                                        ?: "Error al actualizar"
                                }
                            }
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Actualizar contraseña")
                }
            }

            error?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
