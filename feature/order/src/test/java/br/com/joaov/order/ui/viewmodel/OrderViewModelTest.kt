package br.com.joaov.order.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import br.com.joaov.designsystem.extension.toMoney
import br.com.joaov.navigation.FeatureNavigation
import br.com.joaov.order.MainDispatcherRule
import br.com.joaov.order.domain.usecase.OrderUseCase
import br.com.joaov.order.ui.state.ItemOrderUi
import br.com.joaov.order.ui.state.OrderUiState
import br.com.joaov.order.ui.state.ProductUi
import br.com.joaov.order.ui.state.toOrderUi
import br.com.joaov.order.ui.state.toProductUi
import br.com.joaov.persistence.domain.model.OrderModel
import br.com.joaov.persistence.domain.model.ProductModel
import br.com.joaov.persistence.domain.model.SaleModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val orderUseCase: OrderUseCase = mockk(relaxed = true)
    private val navigation: FeatureNavigation = mockk(relaxed = true)

    private lateinit var viewModel: OrderViewModel
    private var listState: MutableList<OrderUiState> = mutableListOf()

    @Before
    fun before() {
        listState = mutableListOf()
    }

    private fun initViewModel(orderId: Int?) =  runTest(mainRule.testDispatcher) {
        viewModel = OrderViewModel(orderId, navigation, orderUseCase, mainRule.testDispatcher)

        viewModel.uiState.asLiveData().observeForever {
            listState.add(it)
        }
    }

    @Test
    fun `when init viewmodel how edition then update state to edition`() = runTest {
        val expectedProducts = getStubProducts()
        val expectedOrder = getStubOrderModel()
        coEvery { orderUseCase.getAllProducts() } returns flowOf(expectedProducts)
        coEvery { orderUseCase.getAllOrdersById(1) } returns flowOf(expectedOrder)
        initViewModel(orderId = 1)

        assert(
            listState[0] == OrderUiState().copy(
                textButton = "Atualizar pedido",
                titleToolbar = "Pedido (1)",
                currentOrder = expectedOrder,
                productList = expectedProducts.toProductUi(),
                ordersList = expectedOrder.toOrderUi(),
                totalValue = expectedOrder.valueTotal.toMoney(),
                countItems = 2,
                countProducts = 1
            )
        )
    }

    @Test
    fun `when init viewmodel how create then update state to new product`() = runTest {
        val expectedProducts = getStubProducts()
        coEvery { orderUseCase.getAllProducts() } returns flowOf(expectedProducts)
        coEvery { orderUseCase.getNextId() } returns flowOf(5)
        initViewModel(orderId = null)

        assert(
            listState[0] == OrderUiState().copy(
                textButton = "Finalizar pedido",
                titleToolbar = "Novo Pedido (5)",
                productList = expectedProducts.toProductUi(),
            )
        )
    }

    @Test
    fun `when addItemOrder then refresh uiState`() = runTest(mainRule.testDispatcher) {
        val expectedProducts = getStubProducts()
        val expectedProductAdd = getProductStub()
        coEvery { orderUseCase.getAllProducts() } returns flowOf(expectedProducts)
        coEvery { orderUseCase.getNextId() } returns flowOf(5)
        initViewModel(orderId = null)
        viewModel.addItemOrder(expectedProductAdd)

        assert(
            listState[1] == OrderUiState().copy(
                textButton = "Finalizar pedido",
                titleToolbar = "Novo Pedido (5)",
                productList = expectedProducts.toProductUi(),
                totalValue = expectedProductAdd.valueUnit,
                countProducts =1,
                countItems = 1,
                ordersList = listOf(ItemOrderUi(expectedProductAdd.idProduct, expectedProductAdd.nameProduct, expectedProductAdd.valueUnit, expectedProductAdd.valueUnit, 1))
            )
        )
    }

    private fun getStubProducts() = listOf(
        ProductModel(1, "Produto 1", 6.0),
        ProductModel(2, "Produto 2", 6.0),
    )

    private fun getProductStub() = ProductUi(
        nameProduct = "Produto 3", 12.0.toMoney(), idProduct = 3
    )
    private fun getStubOrderModel(): OrderModel {
        val listSaleModel = listOf(
            SaleModel(1, 2, ProductModel(1, "Produto 1", 6.0))
        )
        return OrderModel(1, "João", 12.0, listSaleModel)
    }

}