package com.example.navigationcompose.ui.screens.secretos.compartir

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.domain.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompartirSecretoScreen(
    secretoId: Long,
    onBack: () -> Unit,
    viewModel: CompartirSecretoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state.success) {
        if (state.success) {
            Toast.makeText(context, Constantes.TEXT_SECRETO_COMPARTIDO_EXITO, Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    CompartirSecretoScreenContent(
        usuarios = state.usuarios,
        usuariosSeleccionados = state.usuariosSeleccionados,
        isLoading = state.isLoading,
        isLoadingUsuarios = state.isLoadingUsuarios,
        error = state.error,
        onUsuarioToggle = { viewModel.toggleUsuarioSeleccionado(it) },
        onBack = onBack,
        onCompartir = { viewModel.compartir() }
    )
}

@Preview(showBackground = true)
@Composable
fun CompartirSecretoScreenPreview() {
    MaterialTheme {
        CompartirSecretoScreenContent(
            usuarios = listOf(
                Usuario(1, "usuario1", "juan@email.com", "Juan Pérez", "USER", null, null, false),
                Usuario(2, "usuario2", "maria@email.com", "María García", "USER", null, null, false),
                Usuario(3, "usuario3", "carlos@email.com", "Carlos López", "USER", null, null, false),
                Usuario(4, "usuario4", "ana@email.com", "Ana Martínez", "USER", null, null, false)
            ),
            usuariosSeleccionados = setOf(1, 3),
            isLoading = false,
            isLoadingUsuarios = false,
            error = null,
            onUsuarioToggle = {},
            onBack = {},
            onCompartir = {}
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompartirSecretoScreenContent(
    usuarios: List<Usuario>,
    usuariosSeleccionados: Set<Long>,
    isLoading: Boolean,
    isLoadingUsuarios: Boolean,
    error: String?,
    onUsuarioToggle: (Long) -> Unit,
    onBack: () -> Unit,
    onCompartir: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Constantes.TEXT_COMPARTIR) },
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
        ) {
            if (error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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

            if (isLoadingUsuarios) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = Constantes.TEXT_SELECCIONAR_USUARIO,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(usuarios) { usuario ->
                            val isSelected = usuariosSeleccionados.contains(usuario.id)

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onUsuarioToggle(usuario.id) },
                                colors = if (isSelected)
                                    CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                else
                                    CardDefaults.cardColors()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = null
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = usuario.nombre,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = usuario.username,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = onCompartir,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading && usuariosSeleccionados.isNotEmpty()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Share, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(Constantes.TEXT_COMPARTIR)
                        }
                    }
                }
            }
        }
    }
}
