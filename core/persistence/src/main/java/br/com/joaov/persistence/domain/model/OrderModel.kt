package br.com.joaov.persistence.domain.model

data class OrderModel(
    var uid: Int? = null,
    var nameClient: String,
    val valueTotal: Double,
    val listProducts: List<SaleModel>
)