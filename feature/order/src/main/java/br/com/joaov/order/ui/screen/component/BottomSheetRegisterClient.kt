package br.com.joaov.order.ui.screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.joaov.designsystem.component.CustomInput

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheetRegisterClient(
    modifier: Modifier,
    visible: Boolean,
    onDismiss: () -> Unit,
    onAddNewClient: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    AnimatedVisibility(visible = visible) {

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                onDismiss()
            },
        ) {
            var name by remember { mutableStateOf(TextFieldValue("")) }

            Column(Modifier.padding(horizontal = 16.dp, 32.dp)) {
                Text(text = "Para finalizar seu pedido, informe-nos o nome do cliente", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, lineHeight = 32.sp)
                Spacer(modifier = Modifier.height(35.dp))
                CustomInput(
                    modifier = Modifier.fillMaxWidth(),
                    startIcon = Icons.Filled.ShoppingCart,
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    hint = "Nome do cliente",
                )
                Spacer(modifier = Modifier.height(50.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onAddNewClient(name.text)
                    }) {
                    Text("Salvar")
                }
            }
        }
    }
}