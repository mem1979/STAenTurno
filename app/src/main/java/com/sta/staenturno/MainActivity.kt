package com.sta.staenturno

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity          // ✅ FragmentActivity hereda de ComponentActivity
import androidx.activity.compose.setContent
import com.sta.staenturno.ui.theme.STAenTurnoTheme

/**
 * Entrada principal de la aplicación.
 */
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            STAenTurnoTheme {
                Navigation()                          // NavHost con Login / Main / VerifyDemo
            }
        }
    }
}

