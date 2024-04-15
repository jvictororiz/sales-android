package br.com.joaov.home.domain.usecase

import br.com.joaov.designsystem.extension.toMoney
import br.com.joaov.persistence.data.repository.HomeRepository
import br.com.joaov.persistence.domain.model.OrderModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class HomeUseCase(
    private val homeRepository: HomeRepository
) {

    suspend fun getAllProducts() = homeRepository.getAllProducts()

    suspend fun saveProduct(name: String, unitValue: Double) = flow<Any?> {
        if (name.isEmpty() && name.length <= 3) {
            throw Exception("Campo 'nome' está inválido")
        }
        if (unitValue <= 0) {
            throw Exception("Campo 'Valor unitário' está inválido")
        }
        homeRepository.insertProduct(name, unitValue)
        emit(null)
    }

    suspend fun getAllOrders(): Flow<List<OrderModel>> {
        return homeRepository.getAllOrder()
    }

    suspend fun deleteOrder(id: Int) {
        homeRepository.deleteOrder(id)
    }

    suspend fun deleteProduct(idProduct: Int) {
        return homeRepository.deleteProduct(idProduct)
    }

    suspend fun getValueTotal(): Flow<String> = homeRepository.getValueTotalSale().map {
        it.toMoney()
    }
}