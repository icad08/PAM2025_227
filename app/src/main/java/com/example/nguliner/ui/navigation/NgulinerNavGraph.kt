package com.example.nguliner.ui.navigation

import androidx.compose.remote.creation.profile.Profile
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nguliner.ui.screen.admin.FormMenuScreen
import com.example.nguliner.ui.screen.auth.LoginScreen
import com.example.nguliner.ui.screen.auth.RegisterScreen
import com.example.nguliner.ui.screen.detail.DetailScreen
import com.example.nguliner.ui.screen.home.DashboardScreen
import com.example.nguliner.ui.screen.home.LandingScreen
import com.example.nguliner.ui.screen.home.ShopDetailScreen
import com.example.nguliner.ui.screen.profile.ProfileScreen

@Composable
fun NgulinerNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Landing.route
    ) {
        // 1. Landing Screen
        composable(route = Screen.Landing.route) {
            LandingScreen(
                onMitraClick = { navController.navigate(Screen.Login.route) },
                onGuestClick = { navController.navigate(Screen.Dashboard.route) }
            )
        }

        // 2. Login Screen
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Landing.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. Register Screen
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // 4. Dashboard Screen
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(
                onFabClick = {
                    navController.navigate(Screen.FormMenu.createRoute(null))
                },
                onItemClick = { menuId ->
                    navController.navigate(Screen.Detail.createRoute(menuId))
                },
                onShopClick = { shopId, shopName ->
                    navController.navigate(Screen.ShopDetail.createRoute(shopId, shopName))
                },
                //  Implementasi onProfileClick
                onProfileClick = {
                    // Cek: Kalau User Login -> Ke Profil
                    if (com.google.firebase.auth.FirebaseAuth.getInstance().currentUser != null) {
                        navController.navigate(Screen.Profile.route)
                    } else {
                        // Kalau Guest -> Lempar balik ke Landing (Login)
                        navController.navigate(Screen.Landing.route) {
                            popUpTo(0) // Hapus history biar bersih
                        }
                    }
                }
            )
        }

        // 5. Form Menu Screen
        composable(
            route = Screen.FormMenu.route,
            arguments = listOf(
                navArgument("menuId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val menuId = backStackEntry.arguments?.getString("menuId")

            FormMenuScreen(
                menuId = menuId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 6. Detail Menu Screen
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("menuId") { type = NavType.StringType })
        ) { backStackEntry ->
            val menuId = backStackEntry.arguments?.getString("menuId") ?: ""
            DetailScreen(
                menuId = menuId,
                navigateBack = { navController.popBackStack() },
                onEditClick = { id ->
                    navController.navigate(Screen.FormMenu.createRoute(id))
                }
            )
        }

        // 7. Shop Detail Screen
        composable(
            route = Screen.ShopDetail.route,
            arguments = listOf(
                navArgument("shopId") { type = NavType.StringType },
                navArgument("shopName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
            val shopName = backStackEntry.arguments?.getString("shopName") ?: "Warung"

            ShopDetailScreen(
                shopId = shopId,
                shopName = shopName,
                onNavigateBack = { navController.popBackStack() },
                onItemClick = { menuId ->
                    navController.navigate(Screen.Detail.createRoute(menuId))
                }
            )
        }

        // 8. [BARU] Profile Screen
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    // Logout -> Balik ke Landing & Hapus semua history
                    navController.navigate(Screen.Landing.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}