package com.example.appcomposelucia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appcomposelucia.domain.model.Pedido
import com.example.appcomposelucia.ui.componentes.BotonesActtion
import com.example.appcomposelucia.ui.theme.ComposeAppTheme
import com.example.appcomposelucia.ui.theme.Dimens
import com.example.appcomposelucia.ui.viewmodel.PedidoViewModel
import com.example.appcomposelucia.ui.viewmodel.UiEvent
import com.example.appcomposelucia.ui.viewmodel.PedidoState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeAppTheme {

                    UserFormScreenViewModel()

            }
        }
    }
}


@Composable
fun UserFormScreenViewModel(
    modifier: Modifier = Modifier,
    viewModel: PedidoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Observar eventos de un solo uso con lifecycle awareness
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }

                }
            }
        }
    }

    UserFormScreen(uiState = uiState,
        snackbarHostState = snackbarHostState,
        onChangePedido = { pedido -> viewModel.actualizarPedido(pedido) },
        onLimpiarFormulario = { viewModel.limpiarFormulario() },
        onNavegarSiguiente = { viewModel.navegarSiguiente() },
        onNavegarAnterior = { viewModel.navegarAnterior() },
        onGuardar = { viewModel.guardarPedido() },
        onBorrar = { viewModel.borrarPedido() },
        onActualizar = { viewModel.guardarCambiosPedido() },
    )






}


@Composable
fun UserFormScreen(modifier: Modifier = Modifier,
                   uiState : PedidoState,
                   snackbarHostState : SnackbarHostState = remember { SnackbarHostState() },
                   onChangePedido: (Pedido) -> Unit = {},
                   onLimpiarFormulario: () -> Unit = {},
                   onNavegarSiguiente: () -> Unit = {},
                   onNavegarAnterior: () -> Unit = {},
                   onGuardar: () -> Unit = {},
                   onBorrar: () -> Unit = {},
                   onActualizar: () -> Unit = {},
                   ) {

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(Dimens.paddingMedium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingLarge)
        ) {

            // Título
            Text(
                text = "Añadir Nuevo Pedido",
                fontSize = Dimens.textSizeTitle,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Nombre y Apellidos
            OutlinedTextField(
                value = uiState.pedidoActual.nomape,
                onValueChange = { onChangePedido(uiState.pedidoActual.copy(nomape = it)) },
                label = { Text("Nombre y Apellidos") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Email
            OutlinedTextField(
                value = uiState.pedidoActual.correo,
                onValueChange = { onChangePedido(uiState.pedidoActual.copy(correo = it)) },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSmall)
            ) {
                OutlinedTextField(
                    value = uiState.pedidoActual.telf,
                    onValueChange = { onChangePedido(uiState.pedidoActual.copy(telf = it)) },
                    label = { Text("Teléfono") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = uiState.pedidoActual.marca,
                    onValueChange = { onChangePedido(uiState.pedidoActual.copy(marca = it)) },
                    label = { Text("Marca") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }


            // Género
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Talla:",
                    fontSize = Dimens.textSizeMedium,
                    color = Color.Black,
                    modifier = Modifier.padding(end = Dimens.paddingSmall)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = uiState.pedidoActual.talla == "L",
                            onClick = { onChangePedido(uiState.pedidoActual.copy(talla = "L")) }
                        )
                        Text("L", modifier = Modifier.padding(end = Dimens.paddingSmall))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = uiState.pedidoActual.talla == "M",
                            onClick = { onChangePedido(uiState.pedidoActual.copy(talla = "M")) }
                        )
                        Text("M", modifier = Modifier.padding(end = Dimens.paddingSmall))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = uiState.pedidoActual.talla == "S",
                            onClick = { onChangePedido(uiState.pedidoActual.copy(talla = "S")) }
                        )
                        Text("S")
                    }
                }
            }

            // Comentarios
            OutlinedTextField(
                value = uiState.pedidoActual.comentario,
                onValueChange = { onChangePedido(uiState.pedidoActual.copy(comentario = it)) },
                label = { Text("Comentarios") },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.textAreaHeight),
                maxLines = 4
            )

            Botonera(
                indiceActual = uiState.indiceActual,
                size = uiState.pedidos.size,
                isEmpty = uiState.pedidos.isEmpty(),
                onLimpiarFormulario = onLimpiarFormulario,
                onNavegarSiguiente = onNavegarSiguiente,
                onNavegarAnterior = onNavegarAnterior,
                onGuardar = onGuardar,
                onBorrar = onBorrar,
                onActualizar = onActualizar
            )
        }
    }

}

@Composable
fun Botonera(
    indiceActual : Int,
    size : Int,
    isEmpty : Boolean,
    onLimpiarFormulario: () -> Unit,
    onNavegarSiguiente: () -> Unit,
    onNavegarAnterior: () -> Unit,
    onGuardar: () -> Unit,
    onBorrar: () -> Unit,
    onActualizar: () -> Unit
){


    // Botones de navegación
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onNavegarAnterior() },
            enabled = indiceActual > 0,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0099CC)),
            modifier = Modifier
                .weight(1f)
                .height(Dimens.buttonHeightSmall)
                .padding(end = Dimens.paddingExtraSmall)
        ) {
            Text("← Ant.", fontSize = Dimens.textSizeSmall)
        }

        Text(
            text = if (isEmpty) "0/0" else "${indiceActual + 1}/${size}",
            fontSize = Dimens.textSizeMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = Dimens.paddingSmall)
                .widthIn(min = 40.dp)
        )

        Button(
            onClick = { onNavegarSiguiente() },
            enabled = indiceActual < size - 1,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0099CC)),
            modifier = Modifier
                .weight(1f)
                .height(Dimens.buttonHeightSmall)
                .padding(start = Dimens.paddingExtraSmall)
        ) {
            Text("Sig. →", fontSize = Dimens.textSizeSmall)
        }
    }
    BotonesActtion(
        enableBorrar = !isEmpty,
        enableActualizar = !isEmpty,
        onGuardar = onGuardar,
        onLimpiarFormulario = onLimpiarFormulario,
        onBorrar = onBorrar,
        onActualizar = onActualizar,
    )



}


@Preview(showBackground = true)
@Composable
fun UserFormScreenPreview() {
    ComposeAppTheme {
        UserFormScreen(uiState = PedidoState(
            pedidos = listOf(
                Pedido(nomape = "Juan Pérez")
            ),
            indiceActual = 0,
            pedidoActual = Pedido(nomape = "Juan Pérez")
        ))
    }
}

