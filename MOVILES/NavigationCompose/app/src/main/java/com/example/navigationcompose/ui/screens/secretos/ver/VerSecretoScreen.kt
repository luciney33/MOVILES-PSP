package com.example.navigationcompose.ui.screens.secretos.ver

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.domain.model.Secreto
import com.example.navigationcompose.domain.model.SecretoDescifrado
import com.example.navigationcompose.domain.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerSecretoScreen(
    secretoId: Long,
    onBack: () -> Unit,
    viewModel: VerSecretoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRevokeDialog by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(state.deleteSuccess) {
        if (state.deleteSuccess) {
            Toast.makeText(context, Constantes.TEXT_SECRETO_ELIMINADO_EXITO, Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Constantes.TEXT_VER_SECRETO) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, Constantes.TEXT_DESCRIPCION_VOLVER)
                    }
                },
                actions = {
                    if (state.secretoDescifrado?.secreto?.esAutor == true) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, Constantes.TEXT_ELIMINAR)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                state.error != null -> {
                    ErrorCard(
                        error = state.error!!,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }

                state.secretoDescifrado != null -> {
                    PantallaContenido(
                        secretoDescifrado = state.secretoDescifrado!!,
                        onRevocarAcceso = { usuarioId -> showRevokeDialog = usuarioId }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Secreto") },
            text = { Text("¿Estás seguro de que deseas eliminar este secreto? Esta acción no se puede deshacer y todos los usuarios perderán acceso.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarSecreto()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    showRevokeDialog?.let { usuarioId ->
        AlertDialog(
            onDismissRequest = { showRevokeDialog = null },
            title = { Text(Constantes.TEXT_REVOCAR_ACCESO) },
            text = { Text("¿Deseas revocar el acceso a este usuario? Ya no podrá ver este secreto.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.revocarAcceso(usuarioId)
                        showRevokeDialog = null
                    }
                ) {
                    Text("Revocar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRevokeDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ErrorCard(
    error: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun PantallaContenido(
    secretoDescifrado: SecretoDescifrado,
    onRevocarAcceso: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = if (secretoDescifrado.firmaValida)
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            else
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (secretoDescifrado.firmaValida) Icons.Default.CheckCircle
                    else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (secretoDescifrado.firmaValida)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    if (secretoDescifrado.firmaValida) Constantes.TEXT_FIRMA_VALIDA
                    else Constantes.TEXT_FIRMA_INVALIDA,
                    color = if (secretoDescifrado.firmaValida)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    Constantes.TEXT_AUTOR,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    secretoDescifrado.secreto.autor.nombre,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    Constantes.TEXT_CONTENIDO_SECRETO,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    secretoDescifrado.contenidoPlano,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        if (secretoDescifrado.secreto.compartidoCon.isNotEmpty()) {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        Constantes.TEXT_COMPARTIDO_CON,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(8.dp))

                    secretoDescifrado.secreto.compartidoCon.forEach { usuario ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, null, Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(usuario.nombre)
                            }

                            if (secretoDescifrado.secreto.esAutor) {
                                IconButton(onClick = { onRevocarAcceso(usuario.id) }) {
                                    Icon(
                                        Icons.Default.RemoveCircle,
                                        Constantes.TEXT_REVOCAR_ACCESO,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                        if (usuario != secretoDescifrado.secreto.compartidoCon.lastOrNull()) {
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun VerSecretoScreenContenidoPreview() {
    MaterialTheme {
        PantallaContenido(
            secretoDescifrado = SecretoDescifrado(
                secreto = Secreto(
                    id = 1,
                    autor = Usuario(1, "usuario1", "juan@email.com", "Juan Pérez", "USER"),
                    contenidoDescifrado = null,
                    compartidoCon = listOf(
                        Usuario(2, "usuario2", "maria@email.com", "María García", "USER"),
                        Usuario(3, "usuario3", "carlos@email.com", "Carlos López", "USER")
                    ),
                    esAutor = true,
                    firmaValida = true
                ),
                contenidoPlano = "Este es el contenido secreto descifrado. Información confidencial que solo deben ver personas autorizadas.",
                firmaValida = true
            ),
            onRevocarAcceso = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VerSecretoScreenFirmaInvalidaPreview() {
    MaterialTheme {
        PantallaContenido(
            secretoDescifrado = SecretoDescifrado(
                secreto = Secreto(
                    id = 1,
                    autor = Usuario(1, "usuario1", "juan@email.com", "Juan Pérez", "USER"),
                    contenidoDescifrado = null,
                    compartidoCon = emptyList(),
                    esAutor = false,
                    firmaValida = false
                ),
                contenidoPlano = "Contenido con firma inválida",
                firmaValida = false
            ),
            onRevocarAcceso = {}
        )
    }
}
