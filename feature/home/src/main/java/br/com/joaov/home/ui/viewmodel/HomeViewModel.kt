package br.com.joaov.home.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.joaov.designsystem.extension.moneyToDouble
import br.com.joaov.designsystem.extension.toMoney
import br.com.joaov.home.domain.usecase.HomeUseCase
import br.com.joaov.home.ui.state.HomeUiEvent
import br.com.joaov.home.ui.state.HomeUiState
import br.com.joaov.home.ui.state.OrderUi
import br.com.joaov.home.ui.state.ProductUi
import br.com.joaov.home.ui.state.SaleUi
import br.com.joaov.navigation.Feature
import br.com.joaov.navigation.FeatureNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeUseCase: HomeUseCase,
    private val navigation: FeatureNavigation
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableStateFlow<HomeUiEvent>(HomeUiEvent.Idle)
    val uiEvent: StateFlow<HomeUiEvent> = _uiEvent.asStateFlow()

    init {
        getTotalValue()
        getAllProducts()
        getAllOrders()
    }

    private fun getTotalValue() = viewModelScope.launch(Dispatchers.IO) {
        homeUseCase.getValueTotal()
            .distinctUntilChanged()
            .collect { totalValue ->
                _uiState.update { state ->
                    state.copy(totalValue = totalValue)
                }
            }
    }

    private fun getAllProducts() = viewModelScope.launch(Dispatchers.IO) {
        homeUseCase.getAllProducts()
            .distinctUntilChanged()
            .collect { listProduct ->
                _uiState.update { state ->
                    state.copy(products = listProduct.map { productModel ->
                        ProductUi(productModel.uid, productModel.name, productModel.unitValue.toMoney())
                    })
                }
            }
    }

    private fun getAllOrders() = viewModelScope.launch(Dispatchers.IO) {
        homeUseCase.getAllOrders()
            .distinctUntilChanged()
            .collect { listOrders ->
                val listOrdersUi = listOrders.asReversed().map { orderModel ->
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
                _uiState.update { state ->
                    state.copy(listOrders = listOrdersUi)
                }
            }
    }

    fun saveProduct(name: String, unitValue: String) = viewModelScope.launch(Dispatchers.IO) {
        homeUseCase.saveProduct(name, unitValue.moneyToDouble())
            .catch { exception ->
                _uiEvent.update {
                    HomeUiEvent.ShowMessageSnackBar(exception.message.toString())
                }
            }
            .collect {
                _uiState.update { it.copy(showBottomSheetRegisterClient = false) }
                _uiEvent.update { HomeUiEvent.ScrollEndClients }
            }
    }

    fun deleteProduct(idProduct: Int) = viewModelScope.launch(Dispatchers.IO) {
        homeUseCase.deleteProduct(idProduct)
        _uiEvent.update { HomeUiEvent.ShowMessageSnackBar("Produto deletado com sucesso!") }
    }

    fun deleteOrder(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        homeUseCase.deleteOrder(id)
    }

    fun updateOrder(id: Int) {
        navigation.navigateTo(Feature.Sale(id))
    }

    fun dismissBottomSheetRegisterClient() {
        _uiState.update { it.copy(showBottomSheetRegisterClient = false) }
    }

    fun tapOnAddNewClient() {
        _uiState.update { it.copy(showBottomSheetRegisterClient = true) }
    }

    fun tapOnRegisterNewOrder() {
        navigation.navigateTo(Feature.Sale())
    }
}