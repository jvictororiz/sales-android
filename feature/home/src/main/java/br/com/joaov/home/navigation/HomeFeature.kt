package br.com.joaov.home.navigation

import br.com.joaov.navigation.Feature

open class HomeFeature(override val route: String = "") : Feature.Home() {
    object HomePage : HomeFeature("homePage")
}