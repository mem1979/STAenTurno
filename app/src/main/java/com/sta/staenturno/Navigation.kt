package com.sta.staenturno

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sta.staenturno.ui.historial.HistorialScreen
import com.sta.staenturno.ui.login.ChangePasswordScreen
import com.sta.staenturno.ui.login.LoginScreen
import com.sta.staenturno.ui.verification.VerifyDemoScreen

/**
 * Controlador de navegación de toda la app.
 *
 * Rutas disponibles:
 *   • login                → Pantalla de inicio de sesión
 *   • changePass/{token}   → Cambio de contraseña por defecto
 *   • verifyDemo           → Demo de verificación biométrica (opcional)
 *   • main                 → Pantalla principal (placeholder)
 */
@Composable
fun Navigation(modifier: Modifier = Modifier) {

    val nav: NavHostController = rememberNavController()

    NavHost(
        navController    = nav,
        startDestination = "login",
        modifier         = modifier
    ) {
        /* ---------- HISTORIAL FICHADAS---------- */
        composable("historial") { HistorialScreen(nav) }

        /* ---------- Login ---------- */
        composable("login") { LoginScreen(nav) }

        /* ---------- Cambio de contraseña ---------- */
        composable(
            route = "changePass/{token}",
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { backStack ->
            val token = backStack.arguments!!.getString("token")!!
            ChangePasswordScreen(nav, token)
        }

        /* ---------- Pantalla principal ---------- */
        composable("main") { SaludoInicial(nav) }

        /* ---------- Demo de verificación ---------- */
        composable("verifyDemo") { VerifyDemoScreen(nav) }
    }
}

/* ---------- Placeholder de la pantalla principal ---------- */
@Composable
fun SaludoInicial(nav: NavHostController) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("¡Bienvenido a STAenTurno!")
        Spacer(Modifier.height(24.dp))
        Button(onClick = { nav.navigate("historial") }) {
            Text("Ver historial mensual")
        }
    }
}
