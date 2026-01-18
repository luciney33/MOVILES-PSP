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
import androidx.compose.foundation.horizontalScroll
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
import com.example.appcomposelucia.ui.common.DeviceConfiguration
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
fun PedidoFormScreen(
    modifier: Modifier = Modifier,
    uiState: PedidoState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onChangePedido: (Pedido) -> Unit = {},
    onLimpiarFormulario: () -> Unit = {},
    onNavegarSiguiente: () -> Unit = {},
    onNavegarAnterior: () -> Unit = {},
    onGuardar: () -> Unit = {},
    onBorrar: () -> Unit = {},
    onActualizar: () -> Unit = {},
    isLandscapeView: Boolean? = null
) {
    val deviceConfig = DeviceConfiguration.fromCurrentWindow()
    val isLandscape = isLandscapeView ?: when(deviceConfig) {
        DeviceConfiguration.MOBILE_LANDSCAPE,
        DeviceConfiguration.TABLET_LANDSCAPE -> true
        else -> false
    }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (isLandscape) {
            PedidoFormLandscape(
                modifier = modifier,
                paddingValues = paddingValues,
                uiState = uiState,
                onChangePedido = onChangePedido,
                onLimpiarFormulario = onLimpiarFormulario,
                onNavegarSiguiente = onNavegarSiguiente,
                onNavegarAnterior = onNavegarAnterior,
                onGuardar = onGuardar,
                onBorrar = onBorrar,
                onActualizar = onActualizar
            )
        } else {
            PedidoFormPortrait(
                modifier = modifier,
                paddingValues = paddingValues,
                uiState = uiState,
                onChangePedido = onChangePedido,
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
fun PedidoFormPortrait(
    modifier: Modifier = Modifier,
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    uiState: PedidoState,
    onChangePedido: (Pedido) -> Unit,
    onLimpiarFormulario: () -> Unit,
    onNavegarSiguiente: () -> Unit,
    onNavegarAnterior: () -> Unit,
    onGuardar: () -> Unit,
    onBorrar: () -> Unit,
    onActualizar: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues)
            .padding(Dimens.paddingMedium)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLarge)
    ) {
        TituloPedido()
        FormularioCampos(
            uiState = uiState,
            onChangePedido = onChangePedido
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
@Composable
fun PedidoFormLandscape(
    modifier: Modifier = Modifier,
    paddingValues: androidx.compose.foundation.layout.PaddingValues,
    uiState: PedidoState,
    onChangePedido: (Pedido) -> Unit,
    onLimpiarFormulario: () -> Unit,
    onNavegarSiguiente: () -> Unit,
    onNavegarAnterior: () -> Unit,
    onGuardar: () -> Unit,
    onBorrar: () -> Unit,
    onActualizar: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(paddingValues)
            .padding(8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TituloPedido()
            CampoNombreApellidos(
                valor = uiState.pedidoActual.nomape,
                onValueChange = { onChangePedido(uiState.pedidoActual.copy(nomape = it)) },
                isCompact = true
            )
            CampoEmail(
                valor = uiState.pedidoActual.correo,
                onValueChange = { onChangePedido(uiState.pedidoActual.copy(correo = it)) },
                isCompact = true
            )
            CampoComentarios(
                valor = uiState.pedidoActual.comentario,
                onValueChange = { onChangePedido(uiState.pedidoActual.copy(comentario = it)) },
                isCompact = true
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
        Column(
            modifier = Modifier
                .weight(0.5f)
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CampoTelefono(
                valor = uiState.pedidoActual.telf,
                onValueChange = { onChangePedido(uiState.pedidoActual.copy(telf = it)) },
                modifier = Modifier.fillMaxWidth(),
                isCompact = true
            )
            CampoMarca(
                valor = uiState.pedidoActual.marca,
                onValueChange = { onChangePedido(uiState.pedidoActual.copy(marca = it)) },
                modifier = Modifier.fillMaxWidth(),
                isCompact = true
            )
            SelectorTalla(
                tallaSeleccionada = uiState.pedidoActual.talla,
                onTallaChange = { onChangePedido(uiState.pedidoActual.copy(talla = it)) }
            )
        }
    }
}
@Composable
fun TituloPedido() {
    Text(
        text = Constantes.AyADIR_NUEVO_PEDIDO,
        fontSize = Dimens.textSizeTitle,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}
@Composable
fun FormularioCampos(
    uiState: PedidoState,
    onChangePedido: (Pedido) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLarge)
    ) {
        CampoNombreApellidos(
            valor = uiState.pedidoActual.nomape,
            onValueChange = { onChangePedido(uiState.pedidoActual.copy(nomape = it)) }
        )
        CampoEmail(
            valor = uiState.pedidoActual.correo,
            onValueChange = { onChangePedido(uiState.pedidoActual.copy(correo = it)) }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSmall)
        ) {
            CampoTelefono(
                valor = uiState.pedidoActual.telf,
                onValueChange = { onChangePedido(uiState.pedidoActual.copy(telf = it)) },
                modifier = Modifier.weight(1f)
            )
            CampoMarca(
                valor = uiState.pedidoActual.marca,
                onValueChange = { onChangePedido(uiState.pedidoActual.copy(marca = it)) },
                modifier = Modifier.weight(1f)
            )
        }
        SelectorTalla(
            tallaSeleccionada = uiState.pedidoActual.talla,
            onTallaChange = { onChangePedido(uiState.pedidoActual.copy(talla = it)) }
        )
        CampoComentarios(
            valor = uiState.pedidoActual.comentario,
            onValueChange = { onChangePedido(uiState.pedidoActual.copy(comentario = it)) }
        )
    }
}
@Composable
fun CampoNombreApellidos(
    valor: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(Constantes.NOMBRE_Y_APELLIDOS) },
        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .then(if (isCompact) Modifier.height(60.dp) else Modifier)
    )
}
@Composable
fun CampoEmail(
    valor: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(Constantes.EMAIL) },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .then(if (isCompact) Modifier.height(60.dp) else Modifier)
    )
}
@Composable
fun CampoTelefono(
    valor: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(Constantes.Telefono) },
        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
        singleLine = true,
        modifier = modifier.then(if (isCompact) Modifier.height(60.dp) else Modifier)
    )
}
@Composable
fun CampoMarca(
    valor: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(Constantes.MARCA) },
        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
        singleLine = true,
        modifier = modifier.then(if (isCompact) Modifier.height(60.dp) else Modifier)
    )
}
@Composable
fun SelectorTalla(
    tallaSeleccionada: String,
    onTallaChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = Constantes.TALLA,
            fontSize = Dimens.textSizeMedium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = tallaSeleccionada == Constantes.L,
                    onClick = { onTallaChange(Constantes.L) }
                )
                Text(
                    Constantes.L,
                    modifier = Modifier.padding(end = Dimens.paddingSmall)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = tallaSeleccionada == Constantes.M,
                    onClick = { onTallaChange(Constantes.M) }
                )
                Text(
                    Constantes.M,
                    modifier = Modifier.padding(end = Dimens.paddingSmall)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = tallaSeleccionada == Constantes.S,
                    onClick = { onTallaChange(Constantes.S) }
                )
                Text(Constantes.S)
            }
        }
    }
}
@Composable
fun CampoComentarios(
    valor: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(Constantes.COMENTARIOS) },
        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
        modifier = modifier
            .fillMaxWidth()
            .height(if (isCompact) 80.dp else Dimens.textAreaHeight),
        maxLines = if (isCompact) 3 else 4
    )
}
@Composable
fun Botonera(
    indiceActual: Int,
    size: Int,
    isEmpty: Boolean,
    onLimpiarFormulario: () -> Unit,
    onNavegarSiguiente: () -> Unit,
    onNavegarAnterior: () -> Unit,
    onGuardar: () -> Unit,
    onBorrar: () -> Unit,
    onActualizar: () -> Unit
) {
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
fun PedidoFormScreenPortraitPreview() {
    ComposeAppTheme {
        PedidoFormScreen(
            uiState = PedidoState(
                totalPedidos = 1,
                indiceActual = 0,
                pedidoActual = Pedido(nomape = Constantes.JUAN_PEREZ)
            )
        )
    }
}
@Preview(showBackground = true, device = "spec:width=640dp,height=360dp,dpi=160,orientation=landscape")
@Composable
fun PedidoFormScreenLandScapePreview() {
    ComposeAppTheme {
        PedidoFormScreen(
            uiState = PedidoState(
                totalPedidos = 1,
                indiceActual = 0,
                pedidoActual = Pedido(nomape = Constantes.JUAN_PEREZ)
            ),
            isLandscapeView = true
        )
    }
}


