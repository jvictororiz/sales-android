package br.com.joaov.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import br.com.joaov.home.ui.screen.HomePage
import br.com.joaov.navigation.Feature

fun NavGraphBuilder.addHomeFeatureGraph( popBackStack: () -> Unit) {
    navigation(
        route = Feature.Home().route,
        startDestination = HomeFeature.HomePage.route
    ) {
        composable(HomeFeature.HomePage.route) {
            HomePage()
        }
    }
}