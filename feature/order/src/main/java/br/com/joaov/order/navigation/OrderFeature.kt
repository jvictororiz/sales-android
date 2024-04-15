package br.com.joaov.order.navigation

sealed class OrderFeature(val route: String) {
    data object RegisterOrder : OrderFeature("registerOrder")
}