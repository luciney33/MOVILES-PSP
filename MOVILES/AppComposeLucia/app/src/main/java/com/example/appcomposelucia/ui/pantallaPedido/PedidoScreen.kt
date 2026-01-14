package com.example.appcomposelucia.ui.pantallaPedido

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.appcomposelucia.common.Constantes
import com.example.appcomposelucia.domain.model.Pedido
import com.example.appcomposelucia.ui.common.UiEvent
import com.example.appcomposelucia.ui.componentes.BotonesActtion
import com.example.appcomposelucia.ui.theme.ComposeAppTheme
import com.example.appcomposelucia.ui.theme.Dimens

@Composable
fun PedidoScreenViewModel(
    viewModel: PedidoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

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

    PedidoFormScreen(uiState = uiState,
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
fun PedidoFormScreen(modifier: Modifier = Modifier,
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

            Text(
                text = Constantes.AyADIR_NUEVO_PEDIDO,
                fontSize = Dimens.textSizeTitle,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.pedidoActual.nomape,
                onValueChange = { onChangePedido(uiState.pedidoActual.copy(nomape = it)) },
                label = { Text(Constantes.NOMBRE_Y_APELLIDOS) },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.pedidoActual.correo,
                onValueChange = { onChangePedido(uiState.pedidoActual.copy(correo = it)) },
                label = { Text(Constantes.EMAIL) },
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
                    label = { Text(Constantes.Telefono) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = uiState.pedidoActual.marca,
                    onValueChange = { onChangePedido(uiState.pedidoActual.copy(marca = it)) },
                    label = { Text(Constantes.MARCA) },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }



            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Constantes.TALLA,
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
                            selected = uiState.pedidoActual.talla == Constantes.L,
                            onClick = { onChangePedido(uiState.pedidoActual.copy(talla = Constantes.L)) }
                        )
                        Text(Constantes.L, modifier = Modifier.padding(end = Dimens.paddingSmall))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = uiState.pedidoActual.talla == Constantes.M,
                            onClick = { onChangePedido(uiState.pedidoActual.copy(talla = Constantes.M)) }
                        )
                        Text(Constantes.M, modifier = Modifier.padding(end = Dimens.paddingSmall))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = uiState.pedidoActual.talla == Constantes.S,
                            onClick = { onChangePedido(uiState.pedidoActual.copy(talla = Constantes.S)) }
                        )
                        Text(Constantes.S)
                    }
                }
            }

            OutlinedTextField(
                value = uiState.pedidoActual.comentario,
                onValueChange = { onChangePedido(uiState.pedidoActual.copy(comentario = it)) },
                label = { Text(Constantes.COMENTARIOS) },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.textAreaHeight),
                maxLines = 4
            )

            Botonera(
                indiceActual = uiState.indiceActual,
                size = uiState.totalPedidos,
                isEmpty = uiState.totalPedidos == 0,
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
            Text(Constantes._ANT_, fontSize = Dimens.textSizeSmall)
        }

        Text(
            text = if (isEmpty) Constantes.EMPTY_STRING else "${indiceActual + 1}/${size}",
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
            Text(Constantes.SIG_, fontSize = Dimens.textSizeSmall)
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
fun PedidoFormScreenPreview() {
    ComposeAppTheme {
        PedidoFormScreen(uiState = PedidoState(
            totalPedidos = 1,
            indiceActual = 0,
            pedidoActual = Pedido(nomape = Constantes.JUAN_PEREZ)
        ))
    }
}