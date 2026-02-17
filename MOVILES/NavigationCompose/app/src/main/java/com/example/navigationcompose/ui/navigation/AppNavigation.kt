package com.example.navigationcompose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.navigationcompose.ui.screens.HomeScreen
import com.example.navigationcompose.ui.screens.gym.listadoEntrenamiento.ListaEntrenamientoScreen
import com.example.navigationcompose.ui.screens.login.LoginScreen
import com.example.navigationcompose.ui.screens.register.RegisterScreen

@Composable
fun AppNavigation(
    startDestination: Screen = Screen.Login
) {
    val backStack = rememberNavBackStack(startDestination)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Screen.Login> {
                LoginScreen(
                    onLoginSuccess = {
                        backStack.clear()
                        backStack.add(Screen.Home)
                    },
                    onNavigateToRegister = {
                        backStack.add(Screen.Register)
                    }
                )
            }
            entry<Screen.ListaEntrenamiento> {
                ListaEntrenamientoScreen(
                    onNavigateToDetail = { id ->
                        backStack.add(Screen.DetalleEntrenamiento(id))
                    }
                )
            }

            entry<Screen.Register> {
                RegisterScreen(
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onRegisterSuccess = {
                        backStack.removeLastOrNull()
                    }
                )
            }

            entry<Screen.Home> {
                HomeScreen(
                    onLogout = {
                        backStack.clear()
                        backStack.add(Screen.Login)
                    }
                )
            }

        }
    )
}

