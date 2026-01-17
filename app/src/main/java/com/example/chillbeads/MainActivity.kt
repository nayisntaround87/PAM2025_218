package com.example.chillbeads

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chillbeads.dashboard.DashboardScreen
import com.example.chillbeads.login.LoginScreen
import com.example.chillbeads.product.AddProductScreen
import com.example.chillbeads.product.EditProductScreen
import com.example.chillbeads.product.ProductListScreen
import com.example.chillbeads.ui.splash.SplashScreen
import com.example.chillbeads.ui.theme.ChillBeadsTheme
import com.example.chillbeads.viewmodel.AuthViewModel
import com.example.chillbeads.viewmodel.ProductViewModel
import androidx.compose.runtime.collectAsState
import com.example.chillbeads.viewmodel.AuthState
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChillBeadsTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val productViewModel: ProductViewModel = viewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen() }
        composable("login") {
            val authState by authViewModel.authState.collectAsState()
            LoginScreen(
                onLogin = { email, pass -> authViewModel.login(email, pass) },
                authState = authState,
                onDismissError = { authViewModel.resetAuthState() }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                onLogout = { authViewModel.logout() },
                onNavigateToProductList = { navController.navigate("productList") }
            )
        }
        composable("productList") {
            ProductListScreen(
                onAddProduct = { navController.navigate("addProduct") },
                onProductClick = { productId -> navController.navigate("editProduct/$productId") },
                onDeleteProduct = { productId -> productViewModel.deleteProduct(productId) },
                onLogout = { authViewModel.logout() },
                onNavigateBack = { navController.popBackStack() }, // Tambahkan ini
                productViewModel = productViewModel
            )
        }
        composable("addProduct") {
            AddProductScreen(
                onProductAdded = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() },
                productViewModel = productViewModel
            )
        }
        composable(
            route = "editProduct/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) {
            val productId = it.arguments?.getString("productId") ?: ""
            EditProductScreen(
                productId = productId,
                onProductUpdated = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() },
                productViewModel = productViewModel
            )
        }
    }

    val currentNavRoute by navController.currentBackStackEntryAsState()
    
    LaunchedEffect(isLoggedIn) {
        val route = currentNavRoute?.destination?.route
        if (isLoggedIn && route == "login") {
            navController.navigate("dashboard") {
                popUpTo("login") { inclusive = true }
            }
        } 
        else if (!isLoggedIn && route != "login" && route != "splash") {
            navController.navigate("login") {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
        }
    }
    
    LaunchedEffect(Unit) {
        delay(3000L)
        if (navController.currentDestination?.route == "splash") {
            val destination = if (isLoggedIn) "dashboard" else "login"
            navController.navigate(destination) {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}
