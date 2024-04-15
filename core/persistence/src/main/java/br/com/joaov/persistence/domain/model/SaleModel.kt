package br.com.joaov.persistence.domain.model

data class SaleModel(
    val uid: Int = 0,
    val quantity: Int,
    val productModel: ProductModel
) {
    fun getTotalValue() = quantity * productModel.unitValue
}