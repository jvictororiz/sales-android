package br.com.joaov.home.di

import br.com.joaov.home.domain.usecase.HomeUseCase
import br.com.joaov.home.ui.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class HomeModule {

    companion object {
        fun loadModule() = module {
            viewModel { HomeViewModel(get(), get()) }

            factory { HomeUseCase(get()) }
        }
    }
}