package br.com.joaov.home.ui.state

data class HomeUiState(
    val totalValue: String? = null,
    val products: List<ProductUi> = listOf(),
    val showBottomSheetRegisterClient: Boolean = false,
    val listOrders: List<OrderUi> = emptyList()
)

data class OrderUi(
    val id: Int = 0,
    val nameClient: String = "",
    val totalValue: String = "",
    val countProducts: Int = 0,
    val products: List<SaleUi> = emptyList()
)

data class SaleUi(
    val name: String = "",
    val description: String = "",
    val totalSale: String = ""
)

data class ProductUi(
    val id: Int,
    val name: String,
    val totalValue: String?
)