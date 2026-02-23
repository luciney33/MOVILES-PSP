package com.example.navigationcompose.ui.screens.secretos.lista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.domain.model.Secreto
import com.example.navigationcompose.domain.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaSecretosScreen(
    onBack: () -> Unit,
    onVerSecreto: (Long) -> Unit,
    onCompartirSecreto: (Long) -> Unit,
    viewModel: ListaSecretosViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ListaSecretosScreenContent(
        secretos = state.secretos,
        isLoading = state.isLoading,
        error = state.error,
        onBack = onBack,
        onVerSecreto = onVerSecreto,
        onCompartirSecreto = onCompartirSecreto,
        onRetry = { viewModel.cargarSecretos() }
    )
}

@Preview(showBackground = true)
@Composable
fun ListaSecretosScreenPreview() {
    MaterialTheme {
        ListaSecretosScreenContent(
            secretos = listOf(
                Secreto(
                    id = 1,
                    autor = Usuario(1, "usuario1", "juan@email.com", "Juan Pérez", "USER"),
                    contenidoDescifrado = null,
                    compartidoCon = listOf(
                        Usuario(2, "usuario2", "maria@email.com", "María García", "USER")
                    ),
                    esAutor = true,
                    firmaValida = null
                ),
                Secreto(
                    id = 2,
                    autor = Usuario(2, "usuario2", "maria@email.com", "María García", "USER"),
                    contenidoDescifrado = null,
                    compartidoCon = emptyList(),
                    esAutor = false,
                    firmaValida = null
                )
            ),
            isLoading = false,
            error = null,
            onBack = {},
            onVerSecreto = {},
            onCompartirSecreto = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListaSecretosScreenEmptyPreview() {
    MaterialTheme {
        ListaSecretosScreenContent(
            secretos = emptyList(),
            isLoading = false,
            error = null,
            onBack = {},
            onVerSecreto = {},
            onCompartirSecreto = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListaSecretosScreenLoadingPreview() {
    MaterialTheme {
        ListaSecretosScreenContent(
            secretos = emptyList(),
            isLoading = true,
            error = null,
            onBack = {},
            onVerSecreto = {},
            onCompartirSecreto = {},
            onRetry = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListaSecretosScreenContent(
    secretos: List<Secreto>,
    isLoading: Boolean,
    error: String?,
    onBack: () -> Unit,
    onVerSecreto: (Long) -> Unit,
    onCompartirSecreto: (Long) -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Secretos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, Constantes.TEXT_DESCRIPCION_VOLVER)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRetry) {
                            Text("Reintentar")
                        }
                    }
                }
                secretos.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            Constantes.TEXT_NO_SECRETOS,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            Constantes.TEXT_CREAR_PRIMER_SECRETO,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(secretos) { secreto ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    "Secreto #${secreto.id}",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { onVerSecreto(secreto.id) },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Visibility, null, Modifier.size(18.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Ver")
                                        }

                                        if (secreto.esAutor) {
                                            OutlinedButton(
                                                onClick = { onCompartirSecreto(secreto.id) },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Share, null, Modifier.size(18.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text("Compartir")
                                            }
                                        }
                                    }

                                    if (secreto.compartidoCon.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "Compartido con ${secreto.compartidoCon.size} usuario(s)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

