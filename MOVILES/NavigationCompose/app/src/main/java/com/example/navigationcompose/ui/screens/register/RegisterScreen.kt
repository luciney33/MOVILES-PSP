package com.example.navigationcompose.ui.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.ui.common.UiEvent
import com.example.navigationcompose.ui.theme.NavigationComposeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.ShowError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }
                is UiEvent.RegisterSuccess -> {
                    snackbarHostState.showSnackbar(
                        message = Constantes.REGISTRO_EXITOSO_MENSAJE,
                        duration = SnackbarDuration.Short
                    )
                    onRegisterSuccess()
                }
                else -> Unit
            }
        }
    }

    LaunchedEffect(state.isRegisterSuccessful) {
        if (state.isRegisterSuccessful) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Constantes.TEXT_TITULO_REGISTRO) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Constantes.TEXT_DESCRIPCION_VOLVER)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        RegisterScreenContent(
            state = state,
            paddingValues = paddingValues,
            onUsernameChange = { viewModel.onEvent(RegisterEvent.UserChanged(username = it)) },
            onEmailChange = { viewModel.onEvent(RegisterEvent.UserChanged(email = it)) },
            onNombreChange = { viewModel.onEvent(RegisterEvent.UserChanged(nombre = it)) },
            onPasswordChange = { viewModel.onEvent(RegisterEvent.UserChanged(password = it)) },
            onConfirmPasswordChange = { viewModel.onEvent(RegisterEvent.UserChanged(confirmPassword = it)) },
            onRegisterClick = { viewModel.onEvent(RegisterEvent.Register) },
            onNavigateBack = onNavigateBack
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    NavigationComposeTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RegisterScreenContent(
                state = RegisterState(),
                paddingValues = PaddingValues(0.dp),
                onUsernameChange = {},
                onEmailChange = {},
                onNombreChange = {},
                onPasswordChange = {},
                onConfirmPasswordChange = {},
                onRegisterClick = {},
                onNavigateBack = {}
            )
        }
    }
}

@Composable
private fun RegisterScreenContent(
    state: RegisterState,
    paddingValues: PaddingValues,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onNombreChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = Constantes.TEXT_CREAR_CUENTA,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            label = { Text(Constantes.TEXT_LABEL_USUARIO) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text(Constantes.TEXT_LABEL_EMAIL) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.nombre,
            onValueChange = onNombreChange,
            label = { Text(Constantes.TEXT_LABEL_NOMBRE_COMPLETO) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        var passwordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = { Text(Constantes.TEXT_LABEL_PASSWORD) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible)
                            Constantes.TEXT_OCULTAR_PASSWORD
                        else
                            Constantes.TEXT_MOSTRAR_PASSWORD
                    )
                }
            },
            enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        var confirmPasswordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = state.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text(Constantes.TEXT_LABEL_CONFIRMAR_PASSWORD) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (confirmPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onRegisterClick()
                }
            ),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmPasswordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible)
                            Constantes.TEXT_OCULTAR_PASSWORD
                        else
                            Constantes.TEXT_MOSTRAR_PASSWORD
                    )
                }
            },
            enabled = !state.isLoading
        )

        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !state.isLoading &&
                    state.username.isNotBlank() &&
                    state.email.isNotBlank() &&
                    state.nombre.isNotBlank() &&
                    state.password.isNotBlank() &&
                    state.confirmPassword.isNotBlank()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(Constantes.TEXT_BUTTON_REGISTRARSE)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onNavigateBack,
            enabled = !state.isLoading
        ) {
            Text(Constantes.TEXT_YA_TIENE_CUENTA)
        }
    }
}

