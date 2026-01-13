package com.example.nguliner.ui.navigation

sealed class Screen(val route: String) {
    object Landing : Screen("landing")
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")

    // Rute Form Menu (Bisa Edit/Tambah)
    object FormMenu : Screen("form_menu?menuId={menuId}") {
        fun createRoute(menuId: String? = null): String {
            return if (menuId != null) "form_menu?menuId=$menuId" else "form_menu"
        }
    }

    // Rute Detail Menu
    object Detail : Screen("detail/{menuId}") {
        fun createRoute(menuId: String) = "detail/$menuId"
    }

    // Rute Shop Detail
    object ShopDetail : Screen("shop_detail/{shopId}/{shopName}") {
        fun createRoute(shopId: String, shopName: String) = "shop_detail/$shopId/$shopName"
    }

    // [BARU] Tambahkan ini biar error hilang!
    object Profile : Screen("profile")
}