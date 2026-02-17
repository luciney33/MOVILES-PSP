package com.example.navigationcompose.ui.screens.gym.detalleEntrenamiento

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.domain.model.Ejercicio
import com.example.navigationcompose.ui.theme.NavigationComposeTheme

@Composable
fun DetalleEntrenamientoScreen(
    viewModel: DetalleViewModel = hiltViewModel(),
    onBack: () -> Unit,
    id: Long
) {
    LaunchedEffect(id) {
        viewModel.cargarDetalle(id)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()

    DetalleEntrenamientoContent(
        state = state,
        onNombreChange = viewModel::onNombreChange,
        onDescChange = viewModel::onDescChange,
        onSave = { viewModel.guardar(); onBack() }
    )
}

@Composable
fun DetalleEntrenamientoContent(
    state: DetalleState,
    onNombreChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
            else add(GifDecoder.Factory())
        }
        .build()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = onNombreChange,
                    label = { Text(Constantes.TEXT_NOMBRE_ENTRENAMIENTO) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.descripcion,
                    onValueChange = onDescChange,
                    label = { Text(Constantes.TEXT_DESCRIPCION) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(Constantes.TEXT_EJERCICIOS_RUTINA, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.weight(1f)) {
            items(state.ejercicios) { ejercicio ->
                EjercicioItem(ejercicio, imageLoader)
            }
        }

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (state.id == 0L) Constantes.TEXT_BUTTON_CREAR else Constantes.TEXT_BUTTON_ACTUALIZAR)
        }
    }
}
@Composable
fun EjercicioItem(ejercicio: Ejercicio, imageLoader: ImageLoader) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = "${Constantes.URL_BASE_EMULATOR}${ejercicio.imageUrl}",
                contentDescription = ejercicio.nombre,
                imageLoader = imageLoader,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.padding(start = 12.dp)) {
                Text(text = ejercicio.nombre, fontWeight = FontWeight.Bold)
                Text(text = ejercicio.descripcion, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4, showSystemUi = true)
@Composable
fun DetalleEntrenamientoPreview() {
    NavigationComposeTheme {
        DetalleEntrenamientoContent(
            state = DetalleState(
                id = 1,
                nombre = Constantes.TEXT_RUTINA_EJEMPLO,
                descripcion = Constantes.TEXT_NOTAS_PROGRESION
            ),
            onNombreChange = {},
            onDescChange = {},
            onSave = {}
        )
    }
}
