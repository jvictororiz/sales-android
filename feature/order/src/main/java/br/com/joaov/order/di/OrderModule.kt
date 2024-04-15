package br.com.joaov.order.di

import br.com.joaov.order.domain.usecase.OrderUseCase
import br.com.joaov.order.ui.viewmodel.OrderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

class OrderModule {

    companion object {
        fun loadModule() = module {
            viewModel { params -> OrderViewModel(params.getOrNull(), get(), get()) }

            factory { OrderUseCase(get()) }
        }
    }
}