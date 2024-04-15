package br.com.joaov.navigation

import javax.crypto.SealedObject

sealed class Feature(open val route: String) {
    open class Home : Feature("home")
    open class Sale(idOrder: Int? = null) : Feature("sale/${idOrder}")

}

abstract class FeatureRoute(val graph: String) {
    abstract fun route(): SealedObject
}