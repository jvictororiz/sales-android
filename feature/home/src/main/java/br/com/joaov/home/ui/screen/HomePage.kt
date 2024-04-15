package br.com.joaov.home.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.joaov.designsystem.component.AutoResizeText
import br.com.joaov.designsystem.component.BottomSheetOptions
import br.com.joaov.designsystem.component.BottomSheetRegisterProduct
import br.com.joaov.designsystem.component.CustomAlertDialog
import br.com.joaov.designsystem.component.DashedDivider
import br.com.joaov.designsystem.component.FontSizeRange
import br.com.joaov.designsystem.theme.BlueLight
import br.com.joaov.designsystem.theme.ColorText
import br.com.joaov.designsystem.theme.GreenLight
import br.com.joaov.designsystem.theme.PrimaryColor
import br.com.joaov.designsystem.theme.PurpleDark
import br.com.joaov.designsystem.theme.SalesAndroidTheme
import br.com.joaov.home.R
import br.com.joaov.home.ui.state.HomeUiEvent
import br.com.joaov.home.ui.state.HomeUiState
import br.com.joaov.home.ui.state.OrderUi
import br.com.joaov.home.ui.state.ProductUi
import br.com.joaov.home.ui.state.SaleUi
import br.com.joaov.home.ui.theme.GreyLow
import br.com.joaov.home.ui.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomePage(
    viewModel: HomeViewModel = koinViewModel()
) {
    val stateUi by viewModel.uiState.collectAsState()
    val eventUi by viewModel.uiEvent.collectAsState()

    val lazyColumnListState = rememberLazyListState()
    val snackBarHostState = remember { SnackbarHostState() }

    when (val event = eventUi) {
        HomeUiEvent.ScrollEndClients -> {
            LaunchedEffect(stateUi.products) {
                lazyColumnListState.animateScrollToItem(stateUi.products.size)
            }
        }

        is HomeUiEvent.ShowMessageSnackBar -> {
            LaunchedEffect(eventUi) {
                snackBarHostState.showSnackbar(message = event.message)
            }
        }

        HomeUiEvent.Idle -> {}
    }

    val heightCard = 270.dp
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.tapOnRegisterNewOrder()
                }
            ) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val boxWidth = this.maxHeight
            HeaderView(stateUi, heightCard)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(boxWidth - (heightCard - 70.dp))
                    .background(color = Color.White, shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            ) {
                SalesHistoryView(
                    listOrder = stateUi.listOrders,
                    onDelete = { id ->
                        viewModel.deleteOrder(id)
                    },
                    onUpdate = { id ->
                       viewModel.updateOrder(id)
                    }
                )
            }
            ListProductRow(
                listItem = stateUi.products,
                lazyColumnListState = lazyColumnListState,
                onAddNewClient = {
                    viewModel.tapOnAddNewClient()
                },
                onDeleteProduct = { idProduct ->
                    viewModel.deleteProduct(idProduct)
                }
            )
        }

        BottomSheetRegisterProduct(
            modifier = Modifier.padding(paddingValues),
            visible = stateUi.showBottomSheetRegisterClient,
            onDismiss = {
                viewModel.dismissBottomSheetRegisterClient()
            },
            onAddNewProduct = { name, value ->
                viewModel.saveProduct(name, value)
            }
        )
    }
}


@Composable
fun SalesHistoryView(listOrder: List<OrderUi>, onDelete: (Int) -> Unit, onUpdate: (Int) -> Unit) {
    Column(
        modifier = Modifier.padding(top = 100.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            stringResource(R.string.history_sales),
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ColorText
        )
        Spacer(modifier = Modifier.height(20.dp))

        if (listOrder.isEmpty()) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(top = 60.dp)
                    .alpha(0.8F),
                painter = painterResource(R.drawable.empty_icon),
                contentDescription = stringResource(R.string.without_orders),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.without_orders),
                fontSize = 22.sp,
                color = Color.LightGray,
                lineHeight = 40.sp,
                textAlign = TextAlign.Center
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(listOrder) { orderUi ->
                ItemOrderView(orderUi, onDelete, onUpdate)
            }
            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemOrderView(orderUi: OrderUi, onDelete: (Int) -> Unit, onUpdate: (Int) -> Unit) {
    val showDialogDeleteOrder = remember { mutableStateOf(false) }
    val showBottomSheet = remember { mutableStateOf(false) }
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) -90F else 90F, label = ""
    )

    Column {
        BottomSheetOptions(
            visible = showBottomSheet.value,
            onDismiss = {
                showBottomSheet.value = false
            },
            mapOf(
                "Atualizar" to {
                    showBottomSheet.value = false
                    onUpdate.invoke(orderUi.id)
                },
                "Excluir" to {
                    showBottomSheet.value = false
                    showDialogDeleteOrder.value = true
                }
            )
        )
        CustomAlertDialog(
            onDismissRequest = { showDialogDeleteOrder.value = false },
            onConfirmation = {
                showDialogDeleteOrder.value = false
                onDelete(orderUi.id)
            },
            showDialog = showDialogDeleteOrder,
            dialogText = stringResource(R.string.confirmation_delete_order)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = {
                        expandedState = !expandedState
                    },
                    onLongClick = {
                        showBottomSheet.value = true
                    }
                )
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                )
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingCart,
                contentDescription = "contentDescription",
                modifier = Modifier
                    .size(30.dp)
                    .background(PrimaryColor, CircleShape)
                    .padding(8.dp),

                tint = Color.White
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                modifier = Modifier.weight(40F)
            ) {
                Text(
                    text = orderUi.nameClient,
                    fontSize = 14.sp,
                    color = ColorText,
                    fontWeight = FontWeight.ExtraBold,
                    style = LocalTextStyle.current.copy(lineHeight = 14.sp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = pluralStringResource(id = R.plurals.quantity_products, orderUi.countProducts, orderUi.countProducts),
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.weight(1F))
            Text(
                maxLines = 1,
                text = orderUi.totalValue,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GreenLight
            )
            Spacer(modifier = Modifier.width(2.dp))
            IconButton(
                onClick = {
                    expandedState = !expandedState
                }) {
                Icon(
                    modifier = Modifier.rotate(rotationState),
                    tint = ColorText,
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = ""
                )
            }
            Spacer(modifier = Modifier.width(2.dp))
        }

        AnimatedVisibility(
            visible = expandedState,
        ) {
            ExpandedProductView(orderUi.products)
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
    }
}

@Composable
fun ExpandedProductView(products: List<SaleUi>) {

    Column(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)

    ) {
        products.forEach { item ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1F),
                ) {
                    AutoResizeText(
                        text = item.name,
                        fontSizeRange = FontSizeRange(
                            min = 8.sp,
                            max = 14.sp,
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            )
                        )
                    )
                    Text(
                        text = item.description,
                        fontSize = 12.sp,
                        maxLines = 2,
                        color = Color.Gray,
                        textAlign = TextAlign.Start,
                        lineHeight = 16.sp
                    )
                }
                DashedDivider(
                    modifier = Modifier
                        .weight(1F)
                        .padding(horizontal = 16.dp)
                )
                Text(text = item.totalSale, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = ColorText)
            }
        }
    }
}

@Composable
private fun BoxWithConstraintsScope.ListProductRow(
    lazyColumnListState: LazyListState,
    listItem: List<ProductUi>,
    onAddNewClient: () -> Unit,
    onDeleteProduct: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier.Companion
            .align(Alignment.TopCenter)
            .padding(top = 140.dp)
            .fillMaxWidth()
            .height(180.dp),
        state = lazyColumnListState,
        contentPadding = PaddingValues(20.dp)

    ) {

        item {
            AddButtonView(onAddNewClient)
        }

        items(listItem) { clientUi ->
            ItemProductView(
                productUi = clientUi,
                onDeleteProduct = onDeleteProduct
            )
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemProductView(productUi: ProductUi, onDeleteProduct: (Int) -> Unit) {
    val showDialog = remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxHeight()
            .padding(8.dp)
            .combinedClickable(
                onClick = { },
                onLongClick = {
                    showDialog.value = true
                }
            )
            .width(180.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(22.dp),
        colors = CardDefaults.cardColors(GreyLow),

        ) {

        Column(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CustomAlertDialog(
                onDismissRequest = { showDialog.value = false },
                onConfirmation = {
                    showDialog.value = false
                    onDeleteProduct(productUi.id)
                },
                showDialog = showDialog,
                dialogText = stringResource(R.string.confirmation_delete_product)
            )
            Text(
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                text = productUi.name, textAlign = TextAlign.Center, style = TextStyle(
                    color = ColorText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Valor unitário:", textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
            )
            Spacer(modifier = Modifier.height(6.dp))

            productUi.totalValue?.let { totalValue ->
                Text(
                    text = totalValue,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        color = PrimaryColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            } ?: run {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            }

        }
    }
}

@Composable
fun AddButtonView(onAddNewClient: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(70.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onAddNewClient,
        elevation = CardDefaults.cardElevation(22.dp),
        colors = CardDefaults.cardColors(BlueLight),

        ) {

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(painterResource(R.drawable.ic_add), "Adicionar produto")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Novo Produto",
                textAlign = TextAlign.Center,
                color = ColorText,
                style = TextStyle(color = Color.Black, fontWeight = FontWeight.Bold)
            )
        }
    }
}


@Composable
fun HeaderView(stateUi: HomeUiState, heightCard: Dp) {
    Box(
        Modifier
            .height(heightCard)
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PrimaryColor,
                        PurpleDark
                    )
                )
            )
    ) {
        Column(
            Modifier.padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.title_dashboard),
                color = Color.White,
                letterSpacing = 2.sp,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                textAlign = TextAlign.Center,
                text = stringResource(id = R.string.total_value),
                color = Color.White,
                fontSize = 14.sp
            )
            stateUi.totalValue?.let { totalValue ->
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = totalValue,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp, color = Color.White
                )
            } ?: run {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(30.dp))
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    SalesAndroidTheme {
        HomePage()
    }
}