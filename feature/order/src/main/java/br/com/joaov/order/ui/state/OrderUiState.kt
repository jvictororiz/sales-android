package br.com.joaov.order.ui.state

import br.com.joaov.designsystem.extension.moneyToDouble
import br.com.joaov.designsystem.extension.toMoney
import br.com.joaov.persistence.domain.model.OrderModel
import java.util.UUID
import kotlin.random.Random

data class OrderUiState(
    val textButton: String = "",
    val totalValue: String = "R$0,00",
    val countItems: Int = 0,
    val countProducts: Int = 0,
    val showBottomSheetClient: Boolean = false,
    val productList: List<ProductUi> = emptyList(),
    val ordersList: List<ItemOrderUi> = emptyList(),
    var currentOrder: OrderModel? = null,
    val snackBar: ShowMessageSnackBar = ShowMessageSnackBar()
) {

}

data class ShowMessageSnackBar(
    val message: String = "",
    val id: UUID = UUID.randomUUID()
)

data class ItemOrderUi(
    val idProduct: Int,
    val nameProduct: String,
    val valueUnit: String,
    var totalValue: String,
    var quantity: Int,
) {
    fun refreshTotal() {
        totalValue = (valueUnit.moneyToDouble() * quantity).toMoney()
    }
}

data class ProductUi(
    val nameProduct: String,
    val valueUnit: String,
    val idProduct: Int
)