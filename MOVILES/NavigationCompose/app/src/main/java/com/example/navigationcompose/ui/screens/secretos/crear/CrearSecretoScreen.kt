package com.example.navigationcompose.ui.screens.secretos.crear

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.navigationcompose.common.Constantes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearSecretoScreen(
    onBack: () -> Unit,
    viewModel: CrearSecretoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.success) {
        if (state.success) {
            Toast.makeText(context, Constantes.TEXT_SECRETO_CREADO_EXITO, Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    CrearSecretoScreenContent(
        contenido = state.contenido,
        isLoading = state.isLoading,
        error = state.error,
        onContenidoChange = { viewModel.onContenidoChange(it) },
        onBack = onBack,
        onGuardar = { viewModel.crearSecreto() }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CrearSecretoScreenContent(
    contenido: String,
    isLoading: Boolean,
    error: String?,
    onContenidoChange: (String) -> Unit,
    onBack: () -> Unit,
    onGuardar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Constantes.TEXT_CREAR_SECRETO) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, Constantes.TEXT_DESCRIPCION_VOLVER)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (error != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Text(
                text = "El secreto se cifrará automáticamente con tu clave privada",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = contenido,
                onValueChange = onContenidoChange,
                label = { Text(Constantes.TEXT_INGRESE_CONTENIDO) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                enabled = !isLoading,
                maxLines = 10,
                isError = contenido.isBlank() && error != null
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading
                ) {
                    Text(Constantes.TEXT_CANCELAR)
                }

                Button(
                    onClick = onGuardar,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading && contenido.isNotBlank()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(Constantes.TEXT_GUARDAR)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CrearSecretoScreenPreview() {
    MaterialTheme {
        CrearSecretoScreenContent(
            contenido = "Este es el contenido secreto que será cifrado automáticamente",
            isLoading = false,
            error = null,
            onContenidoChange = {},
            onBack = {},
            onGuardar = {}
        )
    }
}

