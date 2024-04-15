package br.com.joaov.order.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import br.com.joaov.order.ui.screen.OrderPage
import br.com.joaov.order.ui.viewmodel.OrderViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.addOrderFeatureGraph(popBackStack: () -> Unit) {
    navigation(
        route = "sale/{orderId}",
        startDestination = OrderFeature.RegisterOrder.route
    ) {
        composable(
            OrderFeature.RegisterOrder.route,
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId", "")?.toIntOrNull()
            val viewModel: OrderViewModel = koinViewModel {
                parametersOf(orderId)
            }
            OrderPage(
                viewModel = viewModel,
                popBackStack = popBackStack
            )
        }
    }
}