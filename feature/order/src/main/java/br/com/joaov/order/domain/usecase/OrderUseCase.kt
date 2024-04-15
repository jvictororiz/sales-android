package br.com.joaov.order.domain.usecase

import br.com.joaov.persistence.data.repository.HomeRepository
import br.com.joaov.persistence.domain.model.OrderModel
import kotlinx.coroutines.flow.flow

class OrderUseCase(
    private val homeRepository: HomeRepository
) {

    suspend fun getAllProducts() = homeRepository.getAllProducts()

    suspend fun getAllOrdersById(orderId: Int) = homeRepository.getAllOrderById(orderId)

    suspend fun saveOrder(orderModel: OrderModel) = flow<Any?> {
        if (orderModel.nameClient.isEmpty() && orderModel.nameClient.length < 3) {
            throw Exception("Nome do cliente inválido")
        }
        if (orderModel.listProducts.isEmpty()) {
            throw Exception("É necessário ter pelo menos um produto adicionado")
        }
        homeRepository.insertOrder(orderModel)
        emit(null)
    }

    suspend fun updateOrder(orderModel: OrderModel) = flow<Any?> {
        if (orderModel.listProducts.isEmpty()) {
            throw Exception("É necessário ter pelo menos um produto adicionado")
        }
        homeRepository.updateOrder(orderModel)
        emit(null)
    }

    fun validateOrders(orderModel: OrderModel) = flow {
        if (orderModel.listProducts.isEmpty()) {
            throw Exception("É necessário ter pelo menos um produto adicionado")
        } else {
            emit(orderModel)
        }
    }
}