package br.com.joaov.persistence.data.datasource

import br.com.joaov.persistence.data.dao.OrderDao
import br.com.joaov.persistence.data.dao.ProductDao
import br.com.joaov.persistence.data.dao.SaleDao
import br.com.joaov.persistence.data.mapper.toProductEntity
import br.com.joaov.persistence.data.mapper.toProductModel
import br.com.joaov.persistence.data.model.OrderEntity
import br.com.joaov.persistence.data.model.ProductEntity
import br.com.joaov.persistence.data.model.SaleEntity
import br.com.joaov.persistence.data.repository.HomeRepository
import br.com.joaov.persistence.domain.model.OrderModel
import br.com.joaov.persistence.domain.model.SaleModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Date

internal class LocalDataSource(
    private val orderDao: OrderDao,
    private val productDao: ProductDao,
    private val saleDao: SaleDao
) : HomeRepository {

    override suspend fun getAllProducts() = productDao.getAll().map { response ->
        response.map { entity ->
            entity.toProductModel()
        }
    }

    override suspend fun getNextId(): Flow<Int> = flow {
        emit(orderDao.getNextId() + 1)
    }

    override suspend fun getAllOrder(): Flow<List<OrderModel>> {
        return orderDao.getAll().map {
            it.map { orderWithProducts ->
                OrderModel(
                    uid = orderWithProducts.orderEntity.uidOrder,
                    nameClient = orderWithProducts.orderEntity.clientName,
                    valueTotal = orderWithProducts.orderEntity.totalValue,
                    listProducts = orderWithProducts.salesEntity.map { saleEntity ->
                        SaleModel(
                            uid = saleEntity.uidSale,
                            quantity = saleEntity.quantity,
                            productModel = saleEntity.product.toProductModel()
                        )
                    }
                )
            }
        }
    }

    override suspend fun getAllOrderById(id: Int): Flow<OrderModel> {
        return orderDao.getById(id).map { orderWithProducts ->
            OrderModel(
                uid = orderWithProducts.orderEntity.uidOrder,
                nameClient = orderWithProducts.orderEntity.clientName,
                valueTotal = orderWithProducts.orderEntity.totalValue,
                listProducts = orderWithProducts.salesEntity.map { saleEntity ->
                    SaleModel(
                        uid = saleEntity.uidSale,
                        quantity = saleEntity.quantity,
                        productModel = saleEntity.product.toProductModel()
                    )
                }
            )
        }
    }

    override suspend fun getValueTotalSale(): Flow<Double> {
        return saleDao.getAllSales().map { sales ->
            sales.sumOf {
                it.quantity * it.product.unitValue
            }
        }
    }

    override suspend fun deleteOrder(id: Int) {
        orderDao.delete(id)
    }

    override suspend fun insertProduct(name: String, unitPrice: Double) {
        productDao.insertAll(ProductEntity(name = name, unitValue = unitPrice))
    }

    override suspend fun insertOrder(order: OrderModel) {
        val orderEntity = OrderEntity(uidOrder = order.uid, clientName = order.nameClient, timestamp = Date().time, totalValue = order.valueTotal)

        val idOrder = orderDao.insertOrder(orderEntity)
        val sales = order.listProducts.map { sale ->
            SaleEntity(foreignKeyOrder = idOrder.toInt(), product = sale.productModel.toProductEntity(), quantity = sale.quantity)
        }
        saleDao.insertSale(sales)

    }

    override suspend fun updateOrder(order: OrderModel) {
        order.uid?.let { uid ->
            saleDao.deleteById(uid)
            insertOrder(order)
        }
    }

    override suspend fun deleteProduct(idProduct: Int) {
        productDao.deleteProduct(idProduct)
    }
}