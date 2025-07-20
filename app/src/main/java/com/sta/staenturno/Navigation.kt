package com.sta.staenturno

import androidx.compose.foundation.layout.*
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
        composable("main") { SaludoInicial() }

        /* ---------- Demo de verificación ---------- */
        composable("verifyDemo") { VerifyDemoScreen(nav) }
    }
}

/* ---------- Placeholder de la pantalla principal ---------- */
@Composable
fun SaludoInicial() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = "¡Bienvenido a STAenTurno!",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}
