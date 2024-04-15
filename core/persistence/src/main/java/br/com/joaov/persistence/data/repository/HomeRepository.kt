package br.com.joaov.persistence.data.repository

import br.com.joaov.persistence.domain.model.OrderModel
import br.com.joaov.persistence.domain.model.ProductModel
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getAllProducts(): Flow<List<ProductModel>>
    suspend fun getAllOrder(): Flow<List<OrderModel>>
    suspend fun getAllOrderById(id:Int): Flow<OrderModel>
    suspend fun insertProduct(name: String, unitPrice: Double)
    suspend fun insertOrder(order: OrderModel)
    suspend fun deleteProduct(idProduct: Int)
    suspend fun getValueTotalSale(): Flow<Double>
    suspend fun deleteOrder(id: Int)
    suspend fun updateOrder(order: OrderModel)
    suspend fun getNextId(): Flow<Int>
}