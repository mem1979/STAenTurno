package com.sta.staenturno.ui.verification

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity         // ✅ Import correcto
import androidx.navigation.NavController

@Composable
fun VerifyDemoScreen(nav: NavController) {

    // El contexto de Compose es nuestra MainActivity (FragmentActivity)
    val ctx = LocalContext.current as FragmentActivity

    var status by remember { mutableStateOf<String?>(null) }

    Scaffold { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    IdentityVerifier.verify(
                        activity   = ctx,
                        onSuccess  = { status = "✅ Verificación OK" },
                        onError    = { status = "❌ $it" }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Verificar identidad") }

            status?.let {
                Spacer(Modifier.height(24.dp))
                Text(it)
            }
        }
    }
}


