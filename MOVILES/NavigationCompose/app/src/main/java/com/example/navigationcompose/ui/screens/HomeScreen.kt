package com.example.navigationcompose.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.navigationcompose.common.Constantes
import com.example.navigationcompose.ui.navigation.Screen
import com.example.navigationcompose.ui.screens.dragonBall.DragonBallListScreen
import com.example.navigationcompose.ui.screens.gym.detalleEntrenamiento.DetalleEntrenamientoScreen
import com.example.navigationcompose.ui.screens.gym.listadoEntrenamiento.ListaEntrenamientoScreen
import com.example.navigationcompose.ui.screens.login.LogoutViewModel
import com.example.navigationcompose.ui.screens.secretos.SecretosMainScreen
import com.example.navigationcompose.ui.screens.secretos.lista.ListaSecretosScreen
import com.example.navigationcompose.ui.screens.secretos.ver.VerSecretoScreen
import com.example.navigationcompose.ui.screens.secretos.compartir.CompartirSecretoScreen
import com.example.navigationcompose.ui.screens.secretos.crear.CrearSecretoScreen
import com.example.navigationcompose.ui.theme.NavigationComposeTheme

@Composable
fun HomeScreen(onLogout: () -> Unit, logoutViewModel: LogoutViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute?.contains("ListaEntrenamiento") == true ||
                              currentRoute?.contains("DetalleEntrenamiento") == true,
                    onClick = {
                        navController.navigate(Screen.ListaEntrenamiento) {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, null) },
                    label = { Text(Constantes.TEXT_GYM) }
                )
                NavigationBarItem(
                    selected = currentRoute?.contains("ApiExterna") == true,
                    onClick = {
                        navController.navigate(Screen.ApiExterna) {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(imageVector = Icons.Default.Public, contentDescription = null) },
                    label = { Text(Constantes.TEXT_DRAGON_BALL) }
                )
                NavigationBarItem(
                    selected = currentRoute?.contains("Secreto") == true,
                    onClick = {
                        navController.navigate(Screen.SecretosMain) {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                    label = { Text(Constantes.TEXT_SECRETOS) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { 
                        logoutViewModel.logout {
                            onLogout()
                        }
                     },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, null) },
                    label = { Text(Constantes.TEXT_SALIR) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Home> {
                WelcomeContent()
            }

            composable<Screen.ListaEntrenamiento> {
                ListaEntrenamientoScreen(onNavigateToDetail = { id ->
                    navController.navigate(Screen.DetalleEntrenamiento(id))
                })
            }
            composable<Screen.DetalleEntrenamiento> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.DetalleEntrenamiento>()
                DetalleEntrenamientoScreen(
                    id = route.id,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<Screen.ApiExterna> { DragonBallListScreen() }

            composable<Screen.SecretosMain> {
                SecretosMainScreen(
                    onListaSecretos = {
                        navController.navigate(Screen.ListaSecretos)
                    },
                    onCrearSecreto = {
                        navController.navigate(Screen.CrearSecreto)
                    },
                    onLogout = {
                        logoutViewModel.logout {
                            onLogout()
                        }
                    }
                )
            }

            composable<Screen.ListaSecretos> {
                ListaSecretosScreen(
                    onBack = { navController.popBackStack() },
                    onVerSecreto = { id ->
                        navController.navigate(Screen.VerSecreto(id))
                    },
                    onCompartirSecreto = { id ->
                        navController.navigate(Screen.CompartirSecreto(id))
                    }
                )
            }

            composable<Screen.CrearSecreto> {
                CrearSecretoScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable<Screen.VerSecreto> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.VerSecreto>()
                VerSecretoScreen(
                    secretoId = route.id,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<Screen.CompartirSecreto> { backStackEntry ->
                val route = backStackEntry.toRoute<Screen.CompartirSecreto>()
                CompartirSecretoScreen(
                    secretoId = route.secretoId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun WelcomeContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = Constantes.TEXT_BIENVENIDO,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = Constantes.TEXT_BIENVENIDO_FITNESS,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = Constantes.TEXT_MENU_EXPLORAR,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "💪 ${Constantes.TEXT_GYM}: ${Constantes.TEXT_GYM_DESCRIPCION}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🐉 ${Constantes.TEXT_DRAGON_BALL}: ${Constantes.TEXT_DRAGON_BALL_DESCRIPCION}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
@Preview(showBackground = true, device = Devices.PIXEL_4, showSystemUi = true)
@Composable
fun HomeCheckPreview() {
    HomeScreen(onLogout = {})
}

@Preview(showBackground = true, device = Devices.PIXEL_4, showSystemUi = true)
@Composable
fun HomeBottomBarPreview() {
    NavigationComposeTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                        label = { Text(Constantes.TEXT_GYM) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = { Icon(Icons.Default.Public, contentDescription = null) },
                        label = { Text(Constantes.TEXT_DRAGON_BALL) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text(Constantes.TEXT_PERFIL) }
                    )
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(Constantes.TEXT_CONTENIDO_PANTALLA)
            }
        }
    }
}