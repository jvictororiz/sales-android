package br.com.joaov.home.ui.state

import br.com.joaov.designsystem.extension.toMoney
import br.com.joaov.persistence.domain.model.OrderModel
import br.com.joaov.persistence.domain.model.ProductModel


fun List<ProductModel>.toProductUi() = map { productModel ->
    ProductUi(productModel.uid, productModel.name, productModel.unitValue.toMoney())
}

fun List<OrderModel>.toOrderUi() = map { orderModel ->
    OrderUi(id = orderModel.uid ?: -1,
        nameClient = orderModel.nameClient,
        totalValue = orderModel.valueTotal.toMoney(),
        countProducts = orderModel.listProducts.size,
        products = orderModel.listProducts.map { sale ->
            val description = if (sale.quantity > 1) "${sale.quantity} itens de ${sale.productModel.unitValue.toMoney()}" else "1 item"
            val totalMoney = (sale.quantity * sale.productModel.unitValue).toMoney()
            SaleUi(name = sale.productModel.name, description = description, totalSale = totalMoney)
        })
}