package br.com.joaov.order.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.joaov.designsystem.component.CustomAlertDialog
import br.com.joaov.designsystem.component.CustomInput
import br.com.joaov.designsystem.theme.BlueLight
import br.com.joaov.designsystem.theme.ColorText
import br.com.joaov.designsystem.theme.GreenLight
import br.com.joaov.designsystem.theme.PrimaryColor
import br.com.joaov.designsystem.theme.PurpleDark
import br.com.joaov.designsystem.theme.SalesAndroidTheme
import br.com.joaov.order.R
import br.com.joaov.order.ui.screen.component.BottomSheetRegisterClient
import br.com.joaov.order.ui.state.ItemOrderUi
import br.com.joaov.order.ui.state.OrderUiState
import br.com.joaov.order.ui.state.ProductUi
import br.com.joaov.order.ui.viewmodel.OrderViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun OrderPage(
    viewModel: OrderViewModel = koinViewModel(),
) {
    val snackBarHostState = remember { SnackbarHostState() }

    val stateUi by viewModel.uiState.collectAsState()

    if (stateUi.snackBar.message.isNotEmpty()) {
        LaunchedEffect(stateUi.snackBar) {
            val job = launch {
                snackBarHostState.showSnackbar(message = stateUi.snackBar.message, duration = SnackbarDuration.Indefinite)
            }
            delay(1000)
            job.cancel()
        }
    }




    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(brush = Brush.verticalGradient(colors = listOf(PrimaryColor, PurpleDark)))
                .padding(bottom = 90.dp)
        )

        Column(modifier = Modifier.padding(paddingValues)) {
            TitleToolbar(stateUi.titleToolbar)
            Spacer(modifier = Modifier.padding(top = 8.dp))
//            InputNameClient(nameClient)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 12.dp)
            ) {
                ListProductsAdded(
                    stateUi.ordersList,
                    onAddItemProduct = { itemOrder ->
                        viewModel.incrementCountItemOrder(itemOrder)
                    },
                    onRemoveItemProduct = { itemOrder ->
                        viewModel.decrementCountItemOrder(itemOrder)
                    },
                    onDeleteProduct = { itemOrder ->
                        viewModel.deleteItemOrder(itemOrder)
                    })
                CustomSearchView(stateUi) { productUi ->
                    viewModel.addItemOrder(productUi)
                }
                ButtonFinish(
                    nameButton = stateUi.textButton,
                    onFinish = {
                        viewModel.validateFields()
                    })
            }
        }

        BottomSheetRegisterClient(
            modifier = Modifier.padding(paddingValues),
            visible = stateUi.showBottomSheetClient,
            onDismiss = {
                viewModel.onDismissBottomSheet()
            },
            onAddNewClient = { nameClient ->
                viewModel.saveOrderAndFinish(nameClient)
            }
        )
    }
}

@Composable
private fun BoxScope.ButtonFinish(nameButton: String, onFinish: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(PrimaryColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .align(Alignment.BottomCenter),
        onClick = {
            onFinish()
        }) {
        Text(text = nameButton)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListProductsAdded(
    list: List<ItemOrderUi>,
    onDeleteProduct: (Int) -> Unit,
    onAddItemProduct: (Int) -> Unit,
    onRemoveItemProduct: (Int) -> Unit,
) {
    val showDialog = remember { mutableStateOf(false) }

    AnimatedVisibility(
        modifier = Modifier.padding(top = 180.dp),
        visible = list.isEmpty()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(top = 60.dp)
                    .alpha(0.8F),
                painter = painterResource(R.drawable.ic_empty_cart),
                contentDescription = stringResource(R.string.orders_empty),
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.orders_empty),
                fontSize = 22.sp,
                color = Color.Gray,
                lineHeight = 40.sp,
                textAlign = TextAlign.Center
            )
        }


    }
    LazyColumn(
        modifier = Modifier.padding(top = 150.dp)
    ) {

        itemsIndexed(list) { index, item ->

            CustomAlertDialog(
                onDismissRequest = { showDialog.value = false },
                onConfirmation = {
                    showDialog.value = false
                    onDeleteProduct(index)
                },
                showDialog = showDialog,
                dialogText = "Tem certeza que deseja deletar este produto da lista?"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .combinedClickable(
                        onClick = { },
                        onLongClick = {
                            showDialog.value = true
                        }
                    ),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(22.dp),
                colors = CardDefaults.cardColors(Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = item.nameProduct, fontWeight = FontWeight.ExtraBold, color = ColorText, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Valor unitário: ${item.valueUnit}", color = Color.LightGray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                modifier = Modifier
                                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(180.dp))
                                    .background(
                                        color = BlueLight,
                                        shape = RoundedCornerShape(180.dp)
                                    )
                                    .width(32.dp)
                                    .height(32.dp),
                                onClick = {
                                    if (item.quantity == 1) {
                                        showDialog.value = true
                                    } else {
                                        item.quantity--
                                        onRemoveItemProduct(index)
                                    }
                                }
                            ) {
                                Icon(
                                    tint = Color.White,
                                    contentDescription = "Click ",
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_remove)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = pluralStringResource(R.plurals.items_count, item.quantity, item.quantity))
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(
                                modifier = Modifier
                                    .shadow(elevation = 5.dp, shape = RoundedCornerShape(180.dp))
                                    .background(
                                        color = BlueLight,
                                        shape = RoundedCornerShape(180.dp)
                                    )
                                    .width(32.dp)
                                    .height(32.dp),
                                onClick = {
                                    item.quantity++
                                    onAddItemProduct(index)
                                }
                            ) {
                                Icon(
                                    tint = Color.White,
                                    contentDescription = "",
                                    imageVector = ImageVector.vectorResource(R.drawable.ic_add)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    Text(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        text = item.totalValue,
                        fontWeight = FontWeight.ExtraBold,
                        color = GreenLight,
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun TitleToolbar(titleToolbar: String) {
    Text(
        text = titleToolbar,
        color = Color.White,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    )
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun CustomSearchView(
    state: OrderUiState,
    onSelectItem: (ProductUi) -> Unit
) {
    var queryTextField by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    val queryText = queryTextField.text
    val listFilter = state.productList.filter { it.nameProduct.lowercase().contains(queryText.lowercase()) }

    Card(
        modifier = Modifier
            .heightIn(max = 450.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(22.dp),
        colors = CardDefaults.cardColors(Color.White),
    ) {
        CustomInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(9.dp),
            startIcon = Icons.Filled.Search,
            trailingIcon = {
                Icon(Icons.Default.Clear,
                    contentDescription = "clear text",
                    modifier = Modifier
                        .clickable {
                            queryTextField = TextFieldValue("")
                        }
                )
            },
            hint = "Nome do produto",
            value = queryTextField
        ) {
            queryTextField = it
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Quantidade de produtos", fontSize = 8.sp)
                Text(pluralStringResource(id = R.plurals.quantity_products, count = state.countProducts, state.countProducts), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = ColorText)
            }
            VerticalDivider(modifier = Modifier.height(40.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Total de itens", fontSize = 8.sp)
                Text(pluralStringResource(id = R.plurals.items_count, count = state.countItems, state.countItems), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = ColorText)
            }
            VerticalDivider(modifier = Modifier.height(40.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Valor total", fontSize = 8.sp)
                Text(state.totalValue, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = GreenLight)
            }
        }

        AnimatedVisibility(visible = listFilter.isNotEmpty() && queryText.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                stickyHeader {
                    HorizontalDivider()
                }
                items(listFilter) { item ->
                    Row(
                        modifier = Modifier
                            .clickable {
                                queryTextField = TextFieldValue()
                                onSelectItem(item)
                            }
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier
                                .height(50.dp)
                                .padding(16.dp),
                            fontSize = 16.sp,
                            text = item.nameProduct,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.weight(1F))
                        Text(
                            modifier = Modifier
                                .height(50.dp)
                                .padding(16.dp),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            text = item.valueUnit,
                            color = Color.Black
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    SalesAndroidTheme {
        OrderPage()
    }
}