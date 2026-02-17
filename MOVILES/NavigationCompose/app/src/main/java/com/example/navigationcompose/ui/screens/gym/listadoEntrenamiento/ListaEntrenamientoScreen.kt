package com.example.navigationcompose.ui.screens.gym.listadoEntrenamiento

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.domain.model.Entrenamiento
import com.example.navigationcompose.ui.theme.NavigationComposeTheme

@Composable
fun ListaEntrenamientoScreen(
    onNavigateToDetail: (Long) -> Unit,
    viewModel: ListaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ListaEntrenamientoContent(
        state = state,
        onNavigateToDetail = onNavigateToDetail,
        onDelete = viewModel::eliminar
    )
}

@Composable
fun ListaEntrenamientoContent(
    state: ListaState,
    onNavigateToDetail: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToDetail(0L) }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(state.entrenamientos) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onNavigateToDetail(item.id) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(item.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(item.descripcion, style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = { onDelete(item.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4, showSystemUi = true)
@Composable
fun ListaEntrenamientoPreview() {
    val mockEntrenamientos = listOf(
        Entrenamiento(1, Constantes.TEXT_EMPUJE, Constantes.TEXT_ENFOQUE_FUERZA, emptyList()),
        Entrenamiento(2, Constantes.TEXT_TRACCION, Constantes.TEXT_HIPERTROFIA, emptyList()),
        Entrenamiento(3, Constantes.TEXT_PIERNA, Constantes.TEXT_DIA_PESADO, emptyList())
    )

    NavigationComposeTheme {
        ListaEntrenamientoContent(
            state = ListaState(entrenamientos = mockEntrenamientos),
            onNavigateToDetail = {},
            onDelete = {}
        )
    }
}
