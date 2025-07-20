package com.sta.staenturno.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sta.staenturno.data.local.DeviceIdProvider
import com.sta.staenturno.data.local.TokenSecureStorage
import com.sta.staenturno.data.remote.AuthApiService
import com.sta.staenturno.data.remote.RetrofitModule
import com.sta.staenturno.domain.repository.AuthRepositoryImpl
import com.sta.staenturno.ui.verification.IdentityVerifier

@SuppressLint("ContextCastToActivity")
@Composable
fun LoginScreen(nav: NavController) {

    /* ---------- DI manual ---------- */
    val ctx = LocalContext.current as FragmentActivity
    val vm: LoginViewModel = viewModel {
        val api  = RetrofitModule.retrofit.create(AuthApiService::class.java)
        val repo = AuthRepositoryImpl(api, TokenSecureStorage(ctx))
        LoginViewModel(repo, DeviceIdProvider(ctx))
    }

    val state by vm.uiState.collectAsState()

    /* ---------- Navegación según resultado del login ---------- */

    /**
     * 1) Si el backend indica que la contraseña es la predeterminada (`requiereCambio`)
     *    navegamos a la pantalla de cambio de contraseña.
     * 2) De lo contrario disparamos la verificación biométrica.
     * 3) Al terminar la verificación con éxito se navega a "main".
     */
    var verificationDone by remember { mutableStateOf(false) }

    /* → Cambio de contraseña */
    LaunchedEffect(state.requiereCambio, state.token) {
        if (state.success && state.requiereCambio && state.token != null) {
            nav.navigate("changePass/${state.token}") {
                popUpTo("login") { inclusive = false }
            }
        }
    }

    /* → Verificación biométrica (solo si NO requiere cambio) */
    LaunchedEffect(state.success) {
        if (state.success && !state.requiereCambio && !verificationDone) {
            IdentityVerifier.verify(
                activity   = ctx,
                onSuccess  = {
                    verificationDone = true
                    nav.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onError    = vm::showError     // vuelve a mostrar error en la misma pantalla
            )
        }
    }

    /* ---------- UI ---------- */
    Scaffold { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {

            OutlinedTextField(
                value = state.usuario,
                onValueChange = vm::onUsuarioChange,
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = vm::onPasswordChange,
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = vm::onLoginClick,
                enabled = !state.loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Ingresar")
                }
            }

            state.error?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
