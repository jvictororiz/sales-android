package br.com.joaov.order.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.joaov.designsystem.extension.addLast
import br.com.joaov.designsystem.extension.moneyToDouble
import br.com.joaov.designsystem.extension.toMoney
import br.com.joaov.navigation.Feature
import br.com.joaov.navigation.FeatureNavigation
import br.com.joaov.order.domain.usecase.OrderUseCase
import br.com.joaov.order.ui.state.ItemOrderUi
import br.com.joaov.order.ui.state.OrderUiState
import br.com.joaov.order.ui.state.ProductUi
import br.com.joaov.order.ui.state.ShowMessageSnackBar
import br.com.joaov.order.ui.state.toOrderUi
import br.com.joaov.order.ui.state.toProductUi
import br.com.joaov.persistence.domain.model.OrderModel
import br.com.joaov.persistence.domain.model.ProductModel
import br.com.joaov.persistence.domain.model.SaleModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderId: Int?,
    private val navigation: FeatureNavigation,
    private val orderUseCase: OrderUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private var listItems = mutableListOf<ItemOrderUi>()

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(textButton = if (orderId == null) "Finalizar pedido" else "Atualizar pedido")
        }
        getAllProducts()

        if (orderId != null) {
            getCurrentOrder()
        } else {
            getNextOrderId()
        }

    }

    private fun getCurrentOrder() = viewModelScope.launch(dispatcher) {
        orderUseCase.getAllOrdersById(orderId ?: 0).collect { orderModel ->
            _uiState.value.currentOrder = orderModel

            _uiState.update { state -> state.copy(titleToolbar = "Pedido (${orderModel.uid})") }

            listItems = orderModel.toOrderUi().toMutableList()
            refreshItemsOrder()
        }
    }

    private fun getNextOrderId() = viewModelScope.launch(dispatcher) {
        orderUseCase.getNextId().collect { nextId ->
            _uiState.value.currentOrder?.uid = nextId
            _uiState.update { state ->
                state.copy(
                    titleToolbar = "Novo Pedido ($nextId)"
                )
            }
        }
    }

    private fun getAllProducts() = viewModelScope.launch(dispatcher) {
        orderUseCase.getAllProducts().collect { listProduct ->
            _uiState.update { state ->
                state.copy(
                    productList = listProduct.toProductUi()
                )
            }
        }
    }

    fun addItemOrder(productUi: ProductUi) {
        listItems.addLast(
            ItemOrderUi(
                idProduct = productUi.idProduct,
                nameProduct = productUi.nameProduct,
                quantity = 1,
                valueUnit = productUi.valueUnit,
                totalValue = productUi.valueUnit
            )
        )
        refreshItemsOrder()
    }

    fun incrementCountItemOrder(index: Int) {
        listItems[index].apply {
            quantity++
            refreshTotal()
        }
        refreshItemsOrder()
    }

    fun decrementCountItemOrder(index: Int) {
        listItems[index].apply {
            quantity--
            refreshTotal()
        }
        refreshItemsOrder()
    }

    fun deleteItemOrder(index: Int) {
        listItems.removeAt(index)
        refreshItemsOrder()
    }

    fun validateFields() = viewModelScope.launch(dispatcher) {
        val isNewProduct = orderId == null
        val orderModel = OrderModel(
            uid = orderId,
            nameClient = "",
            valueTotal = _uiState.value.totalValue.moneyToDouble(),
            listProducts = listItems.map { itemOrderUi ->
                SaleModel(
                    quantity = itemOrderUi.quantity,
                    productModel = ProductModel(
                        name = itemOrderUi.nameProduct,
                        unitValue = itemOrderUi.valueUnit.moneyToDouble()
                    )
                )
            }
        )
        orderUseCase.validateOrders(orderModel).catch { error ->
            _uiState.update { state ->
                state.copy(snackBar = ShowMessageSnackBar(error.message.toString()))
            }
        }.collect {
            if (isNewProduct) {
                _uiState.update { state ->
                    state.copy(
                        showBottomSheetClient = true,
                        currentOrder = orderModel
                    )
                }
            } else {
                val clientName = _uiState.value.currentOrder?.nameClient
                _uiState.value.currentOrder = orderModel
                saveOrderAndFinish(clientName.orEmpty())
            }
        }
    }

    fun saveOrderAndFinish(nameClient: String) = viewModelScope.launch(dispatcher) {
        val isNewOrder = orderId == null
        onDismissBottomSheet()
        _uiState.value.currentOrder?.let { orderModel ->
            orderModel.nameClient = nameClient
            val currentOperation = if (isNewOrder) orderUseCase.saveOrder(orderModel) else orderUseCase.updateOrder(orderModel)
            currentOperation.catch { error ->
                _uiState.update { state ->
                    state.copy(snackBar = ShowMessageSnackBar(error.message.toString()))
                }
            }.collect {
                navigation.navigateTo(Feature.Home())
            }
        }

    }

    private fun refreshItemsOrder() {
        _uiState.update { state ->
            state.copy(
                totalValue = listItems.sumOf { it.totalValue.moneyToDouble() }.toMoney(),
                countItems = listItems.sumOf { it.quantity },
                countProducts = listItems.size,
                ordersList = listItems.map { it.copy() }
            )
        }
    }

    fun onDismissBottomSheet() {
        _uiState.update { state -> state.copy(showBottomSheetClient = false) }
    }
}