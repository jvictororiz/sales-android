package br.com.joaov.order.ui.state

import br.com.joaov.designsystem.extension.toMoney
import br.com.joaov.persistence.domain.model.OrderModel
import br.com.joaov.persistence.domain.model.ProductModel


fun List<ProductModel>.toProductUi() = map { productModel ->
    ProductUi(
        nameProduct = productModel.name,
        valueUnit = productModel.unitValue.toMoney(),
        idProduct = productModel.uid,
    )
}

fun OrderModel.toOrderUi() = listProducts.map { sale ->
    ItemOrderUi(
        idProduct = sale.productModel.uid,
        totalValue = sale.getTotalValue().toMoney(),
        nameProduct = sale.productModel.name,
        valueUnit = sale.productModel.unitValue.toMoney(),
        quantity = sale.quantity
    )
}