package br.com.joaov.navigation.di

import br.com.joaov.navigation.FeatureNavigation
import org.koin.dsl.module

class NavigationModule {

    companion object {
        fun loadModule() = module {
            single { FeatureNavigation()  }
        }
    }
}