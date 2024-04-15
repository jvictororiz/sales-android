package br.com.joaov.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import br.com.joaov.home.domain.usecase.HomeUseCase
import br.com.joaov.home.ui.state.HomeUiEvent
import br.com.joaov.home.ui.state.HomeUiState
import br.com.joaov.home.ui.state.toOrderUi
import br.com.joaov.home.ui.state.toProductUi
import br.com.joaov.home.ui.viewmodel.HomeViewModel
import br.com.joaov.navigation.FeatureNavigation
import br.com.joaov.persistence.domain.model.OrderModel
import br.com.joaov.persistence.domain.model.ProductModel
import br.com.joaov.persistence.domain.model.SaleModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val homeUseCase: HomeUseCase = mockk(relaxed = true)
    private val navigation: FeatureNavigation = mockk(relaxed = true)

    private lateinit var viewModel: HomeViewModel
    private var listState: MutableList<HomeUiState> = mutableListOf()
    private var listEvent: MutableList<HomeUiEvent> = mutableListOf()

    @Before
    fun before() {
        listState = mutableListOf()
        listEvent = mutableListOf()
    }

    private fun initViewModel() = runTest(mainRule.testDispatcher) {
        viewModel = HomeViewModel(homeUseCase, navigation, mainRule.testDispatcher)
        viewModel.uiState.asLiveData().observeForever {
            listState.add(it)
        }
        viewModel.uiEvent.asLiveData().observeForever {
            listEvent.add(it)
        }
    }

    private fun verificationInit() = runTest(mainRule.testDispatcher) {
        val expectedTotalValue = "R$ 12,00"
        val expectedProducts = getStubProducts()
        val expectedOrders = getStubOrdersModel()
        coEvery { homeUseCase.getValueTotal() } returns flowOf(expectedTotalValue)
        coEvery { homeUseCase.getAllProducts() } returns flowOf(expectedProducts)
        coEvery { homeUseCase.getAllOrders() } returns flowOf(expectedOrders)
        val expectedHomeUiState = HomeUiState(
            totalValue = expectedTotalValue,
            products = expectedProducts.toProductUi(),
            showBottomSheetRegisterClient = false,
            listOrders = expectedOrders.toOrderUi()
        )

        initViewModel()
        assert(listState[0] == expectedHomeUiState)
    }

    @Test
    fun `when save product success then scrollEnd list products`() = runTest(mainRule.testDispatcher) {
        verificationInit()
        val expectedNameProduct = "Produto 3"
        val expectedPriceProduct = "5"

        coEvery { homeUseCase.saveProduct(expectedNameProduct, 5.0) } returns flowOf(null)

        viewModel.saveProduct(expectedNameProduct, expectedPriceProduct)

        assert(listEvent[0] == HomeUiEvent.Idle)
        assert(listEvent[1] == HomeUiEvent.ScrollEndClients)
    }

    @Test
    fun `when save product error then ShowMessageSnackBar`() = runTest(mainRule.testDispatcher) {
        verificationInit()
        val expectedMessageError = "Mensagem de erro"

        coEvery { homeUseCase.saveProduct("", 1.0) } returns flow { throw Exception(expectedMessageError) }

        viewModel.saveProduct("", "1")

        assert(listEvent[0] == HomeUiEvent.Idle)
        assert(listEvent[1] == HomeUiEvent.ShowMessageSnackBar(expectedMessageError))
    }

    @Test
    fun `when delete product success then show message snackBar`() = runTest(mainRule.testDispatcher) {
        verificationInit()
        val expectedId = 1

        coEvery { homeUseCase.deleteProduct(expectedId) } returns Unit

        viewModel.deleteProduct(expectedId)

        assert(listEvent[0] == HomeUiEvent.Idle)
        assert(listEvent[1] == HomeUiEvent.ShowMessageSnackBar("Produto deletado com sucesso!"))
    }

    @Test
    fun `when delete order success then show message snackBar`() = runTest(mainRule.testDispatcher) {
        verificationInit()
        val expectedId = 1

        coEvery { homeUseCase.deleteProduct(expectedId) } returns Unit

        viewModel.deleteOrder(expectedId)

        assert(listEvent[0] == HomeUiEvent.Idle)
        assert(listEvent[1] == HomeUiEvent.ShowMessageSnackBar("Pedido deletado com sucesso!"))
    }

    @Test
    fun `when update order then navigate to orderPage`() = runTest(mainRule.testDispatcher) {
        verificationInit()
        val expectedId = 1

        viewModel.updateOrder(expectedId)

       coVerify { navigation.navigateTo(any()) }
    }
    @Test
    fun `when dismissBottomSheet then update state bottomSheet`() = runTest(mainRule.testDispatcher) {
        verificationInit()

        viewModel.dismissBottomSheetRegisterClient()

       assert(listState[0] == listState.first().copy(showBottomSheetRegisterClient = false))
    }
    @Test
    fun `when tapOnAddNewClient then update state bottomSheet`() = runTest(mainRule.testDispatcher) {
        verificationInit()

        viewModel.tapOnAddNewClient()

       assert(listState[1] == listState.first().copy(showBottomSheetRegisterClient = true))
    }

    private fun getStubProducts() = listOf(
        ProductModel(1, "Produto 1", 6.0),
        ProductModel(1, "Produto 2", 6.0),
    )

    private fun getStubOrdersModel(): List<OrderModel> {
        val listSaleModel = listOf(
            SaleModel(1, 2, ProductModel(1, "Produto 1", 6.0))
        )
        return listOf(
            OrderModel(1, "João", 12.0, listSaleModel),
        )
    }

}