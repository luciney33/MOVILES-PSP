# Conversión de App de Entrenamiento a Jetpack Compose

## 📋 Índice
1. [Activity Principal](#1-activity-principal)
2. [Navegación](#2-navegación)
3. [Menú Entrenamiento](#3-menú-entrenamiento)
4. [Menú Progreso](#4-menú-progreso)
5. [Home y Sesiones](#5-home-y-sesiones)
6. [Items/Cards Reutilizables](#6-itemscards-reutilizables)
7. [Dependencias](#7-dependencias)

---

## 1. Activity Principal

### MainActivity.kt
```kotlin
package com.example.navigation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navigation.ui.home.HomeScreen
import com.example.navigation.ui.menuEntrenamiento.listaEntrenamiento.ListaEntrenamientoScreen
import com.example.navigation.ui.menuProgreso.ejercicioProgreso.EjercicioProgresoScreen
import com.example.navigation.ui.theme.NavigationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavigationTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val bottomNavItems = listOf(
        BottomNavItem(
            route = "home",
            icon = Icons.Default.FitnessCenter,
            label = "Inicio"
        ),
        BottomNavItem(
            route = "entrenamientos",
            icon = Icons.Default.FitnessCenter,
            label = "Entrenamientos"
        ),
        BottomNavItem(
            route = "progreso",
            icon = Icons.Default.TrendingUp,
            label = "Progreso"
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FitTracker") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { 
                            it.route == item.route 
                        } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            composable("home") { 
                HomeScreen(
                    onNavigateToDetalleSesion = { sesionId ->
                        navController.navigate("detalle_sesion/$sesionId")
                    }
                )
            }
            composable("entrenamientos") { 
                ListaEntrenamientoScreen(
                    onNavigateToSesion = { entrenamientoId ->
                        navController.navigate("sesion_entrenamiento/$entrenamientoId")
                    }
                )
            }
            composable("progreso") { 
                EjercicioProgresoScreen(
                    onNavigateToRegistro = { ejercicioId ->
                        navController.navigate("registro_progreso/$ejercicioId")
                    }
                )
            }
            composable("sesion_entrenamiento/{entrenamientoId}") { backStackEntry ->
                val entrenamientoId = backStackEntry.arguments?.getString("entrenamientoId")?.toIntOrNull() ?: 0
                SesionEntrenamientoScreen(
                    entrenamientoId = entrenamientoId,
                    onNavigateToDetalle = { ejercicioId ->
                        navController.navigate("detalle_entrenamiento/$ejercicioId")
                    },
                    onFinalizarSesion = {
                        navController.popBackStack()
                    }
                )
            }
            composable("detalle_entrenamiento/{ejercicioId}") { backStackEntry ->
                val ejercicioId = backStackEntry.arguments?.getString("ejercicioId")?.toIntOrNull() ?: 0
                DetalleEntrenamientoScreen(
                    ejercicioId = ejercicioId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable("detalle_sesion/{sesionId}") { backStackEntry ->
                val sesionId = backStackEntry.arguments?.getString("sesionId")?.toIntOrNull() ?: 0
                DetalleSesionScreen(
                    sesionId = sesionId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable("registro_progreso/{ejercicioId}") { backStackEntry ->
                val ejercicioId = backStackEntry.arguments?.getString("ejercicioId")?.toIntOrNull() ?: 0
                RegistroProgresoScreen(
                    ejercicioId = ejercicioId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)
```

---

## 2. Navegación

### Theme.kt
```kotlin
package com.example.navigation.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    surface = Color(0xFFFAFAFA),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5)
)

@Composable
fun NavigationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
```

---

## 3. Menú Entrenamiento

### 3.1 Lista de Entrenamientos

#### ListaEntrenamientoScreen.kt
```kotlin
package com.example.navigation.ui.menuEntrenamiento.listaEntrenamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ListaEntrenamientoScreen(
    viewModel: ListaEntrenamientoViewModel = hiltViewModel(),
    onNavigateToSesion: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.entrenamientos.isEmpty() -> {
                Text(
                    text = "No hay entrenamientos disponibles",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.entrenamientos) { entrenamiento ->
                        EntrenamientoCard(
                            entrenamiento = entrenamiento,
                            onClick = { onNavigateToSesion(entrenamiento.id) }
                        )
                    }
                }
            }
        }
    }
}
```

### 3.2 Sesión de Entrenamiento

#### SesionEntrenamientoScreen.kt
```kotlin
package com.example.navigation.ui.menuEntrenamiento.sesionEntrenamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SesionEntrenamientoScreen(
    entrenamientoId: Int,
    viewModel: SesionEntrenamientoViewModel = hiltViewModel(),
    onNavigateToDetalle: (Int) -> Unit,
    onFinalizarSesion: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(entrenamientoId) {
        viewModel.loadSesion(entrenamientoId)
        viewModel.startTimer()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = state.nombreEntrenamiento,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.tiempo,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Lista de ejercicios
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.ejercicios) { ejercicio ->
                EjercicioSesionCard(
                    ejercicio = ejercicio,
                    onActualizarSerie = { onNavigateToDetalle(ejercicio.ejercicioId) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Botón Finalizar
        Button(
            onClick = {
                viewModel.finalizarSesion()
                onFinalizarSesion()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Finalizar Sesión",
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}
```

### 3.3 Detalle de Entrenamiento

#### DetalleEntrenamientoScreen.kt
```kotlin
package com.example.navigation.ui.menuEntrenamiento.detalleEntrenamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DetalleEntrenamientoScreen(
    ejercicioId: Int,
    viewModel: DetalleEntrenamientoViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(ejercicioId) {
        viewModel.loadEjercicio(ejercicioId)
    }
    
    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Card de información
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = state.nombre,
                        onValueChange = { viewModel.updateNombre(it) },
                        label = { Text("Nombre del ejercicio") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = state.volumen,
                        onValueChange = { viewModel.updateVolumen(it) },
                        label = { Text("Volumen (kg)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = state.notas,
                        onValueChange = { viewModel.updateNotas(it) },
                        label = { Text("Notas") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Ejercicios",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Lista de ejercicios editables
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.ejercicios) { ejercicio ->
                    EjercicioEditableCard(
                        ejercicio = ejercicio,
                        onSeriesChange = { viewModel.updateSeries(ejercicio.id, it) },
                        onRepeticionesChange = { viewModel.updateRepeticiones(ejercicio.id, it) },
                        onEliminar = { viewModel.eliminarEjercicio(ejercicio.id) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón Actualizar
            Button(
                onClick = {
                    viewModel.actualizarEntrenamiento()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Actualizar",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
```

---

## 4. Menú Progreso

### 4.1 Ejercicio Progreso

#### EjercicioProgresoScreen.kt
```kotlin
package com.example.navigation.ui.menuProgreso.ejercicioProgreso

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EjercicioProgresoScreen(
    viewModel: EjercicioProgresoViewModel = hiltViewModel(),
    onNavigateToRegistro: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadProgresos()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.progresos.isEmpty() -> {
                Text(
                    text = "No hay ejercicios registrados",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.progresos) { progreso ->
                        ProgresoCard(
                            progreso = progreso,
                            onClick = { onNavigateToRegistro(progreso.ejercicioId) }
                        )
                    }
                }
            }
        }
    }
}
```

### 4.2 Registro de Progreso

#### RegistroProgresoScreen.kt
```kotlin
package com.example.navigation.ui.menuProgreso.registroProgreso

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RegistroProgresoScreen(
    ejercicioId: Int,
    viewModel: RegistroProgresoViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(state.mensaje) {
        state.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            if (state.guardadoExitoso) {
                onNavigateBack()
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.fecha,
                onValueChange = { viewModel.updateFecha(it) },
                label = { Text("Fecha") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.CalendarToday, contentDescription = null)
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = state.peso,
                onValueChange = { viewModel.updatePeso(it) },
                label = { Text("Peso (kg)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = state.grasa,
                onValueChange = { viewModel.updateGrasa(it) },
                label = { Text("Grasa corporal (%)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { viewModel.guardarProgreso() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Guardar Progreso",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}
```

---

## 5. Home y Sesiones

### 5.1 Home Screen

#### HomeScreen.kt
```kotlin
package com.example.navigation.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetalleSesion: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadHistorial()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Bienvenido a FitTracker",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Historial de Sesiones",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            state.sesiones.isEmpty() -> {
                Text(
                    text = "No hay sesiones registradas",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.sesiones) { sesion ->
                        SesionHistorialCard(
                            sesion = sesion,
                            onClick = { onNavigateToDetalleSesion(sesion.id) }
                        )
                    }
                }
            }
        }
    }
}
```

### 5.2 Detalle de Sesión

#### DetalleSesionScreen.kt
```kotlin
package com.example.navigation.ui.sesion.DetalleSesion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DetalleSesionScreen(
    sesionId: Int,
    viewModel: DetalleSesionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    
    LaunchedEffect(sesionId) {
        viewModel.loadSesion(sesionId)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Card Resumen
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = state.nombreEntrenamiento,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = state.fecha,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Duración",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = state.duracion,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Ejercicios",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = state.ejerciciosCompletados,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Column {
                        Text(
                            text = "Volumen",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = state.volumenTotal,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Ejercicios Realizados",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.ejercicios) { ejercicio ->
                EjercicioDetalleSesionCard(ejercicio = ejercicio)
            }
        }
    }
}
```

---

## 6. Items/Cards Reutilizables

### 6.1 Entrenamiento Card

```kotlin
@Composable
fun EntrenamientoCard(
    entrenamiento: Entrenamiento,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = entrenamiento.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = entrenamiento.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${entrenamiento.numeroEjercicios} ejercicios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
```

### 6.2 Ejercicio Sesión Card

```kotlin
@Composable
fun EjercicioSesionCard(
    ejercicio: SesionEjercicio,
    onActualizarSerie: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = ejercicio.nombreEjercicio,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Series: ${ejercicio.series}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Reps: ${ejercicio.repeticiones}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Vol: ${ejercicio.volumenKg} kg",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = onActualizarSerie,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar Serie")
            }
        }
    }
}
```

### 6.3 Ejercicio Editable Card

```kotlin
@Composable
fun EjercicioEditableCard(
    ejercicio: SesionEjercicio,
    onSeriesChange: (String) -> Unit,
    onRepeticionesChange: (String) -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ejercicio.nombreEjercicio,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = ejercicio.series.toString(),
                    onValueChange = onSeriesChange,
                    label = { Text("Series") },
                    modifier = Modifier.weight(1f)
                )
                
                OutlinedTextField(
                    value = ejercicio.repeticiones.toString(),
                    onValueChange = onRepeticionesChange,
                    label = { Text("Reps") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
```

### 6.4 Progreso Card

```kotlin
@Composable
fun ProgresoCard(
    progreso: Progreso,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = progreso.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = progreso.grupoMuscular,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Último",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = progreso.ultimoPeso,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column {
                    Text(
                        text = "Récord",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = progreso.record,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
```

### 6.5 Sesión Historial Card

```kotlin
@Composable
fun SesionHistorialCard(
    sesion: SesionHistorial,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = sesion.fecha,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = sesion.nombreEntrenamiento,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = sesion.duracion,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${sesion.ejerciciosCompletados} ejercicios",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${sesion.volumenTotal} kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
```

### 6.6 Ejercicio Detalle Sesión Card

```kotlin
@Composable
fun EjercicioDetalleSesionCard(
    ejercicio: EjercicioDetalleSesion
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = ejercicio.nombreEjercicio,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Series: ${ejercicio.series}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Reps: ${ejercicio.repeticiones}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Volumen: ${ejercicio.volumenKg} kg",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (ejercicio.notas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = ejercicio.notas,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

### 6.7 Serie Item

```kotlin
@Composable
fun SerieItem(
    serie: Serie,
    onPesoChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onCompletadaChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = serie.completada,
            onCheckedChange = onCompletadaChange
        )
        
        Text(
            text = "Serie ${serie.numero}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(80.dp)
        )
        
        OutlinedTextField(
            value = serie.pesoKg.toString(),
            onValueChange = onPesoChange,
            label = { Text("Peso") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        
        OutlinedTextField(
            value = serie.repeticiones.toString(),
            onValueChange = onRepsChange,
            label = { Text("Reps") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
    }
}
```

---

## 7. Dependencias

### build.gradle.kts (Module: app)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.navigation"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.navigation"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.01.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.8.2")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-android-compiler:2.48.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}

kapt {
    correctErrorTypes = true
}
```

---

## 📝 Notas Importantes

### Migración de ViewModels

Los ViewModels **NO cambian** en Compose. Siguen siendo los mismos que en XML, solo cambia cómo los consumes:

```kotlin
// En XML usabas:
private val viewModel: ListaEntrenamientoViewModel by viewModels()

// En Compose usas:
@Composable
fun MiScreen(
    viewModel: ListaEntrenamientoViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
}
```

### Migración de Estados

```kotlin
// Tus States (UiState) NO cambian
data class ListaEntrenamientoState(
    val entrenamientos: List<Entrenamiento> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// Solo cambias cómo los observas:
// XML: viewModel.state.observe(viewLifecycleOwner) { state -> }
// Compose: val state by viewModel.state.collectAsState()
```

### Migración de Navegación

```kotlin
// XML: Navigation Component con nav_graph.xml
// Compose: Navigation Compose en el NavHost

NavHost(navController, startDestination = "home") {
    composable("home") { HomeScreen() }
    composable("detalle/{id}") { backStackEntry ->
        val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
        DetalleScreen(id = id)
    }
}
```

### Repository y Room

**NO cambian**. Room, DAOs, Repositories, UseCases permanecen exactamente iguales. Solo cambia la UI.

### Recursos (strings.xml, colors.xml)

```kotlin
// En Compose accedes a recursos así:
stringResource(R.string.mi_texto)
colorResource(R.color.mi_color)
painterResource(R.drawable.mi_icono)
```

### Material 3

Compose usa Material 3 por defecto. Los componentes XML de Material se convierten en:

| XML | Compose |
|-----|---------|
| MaterialButton | Button() |
| MaterialCard | Card() |
| TextInputLayout + EditText | OutlinedTextField() |
| MaterialTextView | Text() |
| RecyclerView | LazyColumn() |
| ConstraintLayout | Column/Row + Modifier |

### Adapters

**NO EXISTEN** en Compose. Se reemplazan por LazyColumn + items:

```kotlin
LazyColumn {
    items(lista) { item ->
        ItemCard(item)
    }
}
```

---

## ✅ Checklist de Migración

- [ ] Actualizar `build.gradle.kts` con dependencias de Compose
- [ ] Crear `Theme.kt` con Material 3
- [ ] Migrar MainActivity a Compose
- [ ] Convertir cada Fragment a `@Composable`
- [ ] Convertir cada item XML a `@Composable` Card
- [ ] Reemplazar RecyclerViews por LazyColumn
- [ ] Adaptar navegación con Navigation Compose
- [ ] Mantener ViewModels, States, Repositories sin cambios
- [ ] Probar navegación entre pantallas
- [ ] Verificar integración con Hilt

---

## 🎯 Ventajas de Compose sobre XML

1. **Menos código**: No necesitas Adapters, ViewHolders, ViewBinding
2. **Type-safe**: Todo es Kotlin, sin conversiones de tipos
3. **Recomposición automática**: Cuando el estado cambia, la UI se actualiza automáticamente
4. **Preview en tiempo real**: Puedes ver tus composables sin ejecutar la app
5. **Menos archivos**: No necesitas XML + Kotlin, todo es Kotlin
6. **Mejor manejo de estado**: collectAsState() es más simple que observe()

---

¡Listo! Este documento contiene toda tu aplicación convertida a Jetpack Compose. Puedes copiar y pegar cada sección en tu nuevo proyecto Compose. 🚀

