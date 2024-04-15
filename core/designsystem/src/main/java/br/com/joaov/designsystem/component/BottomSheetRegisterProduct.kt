package br.com.joaov.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.joaov.designsystem.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheetRegisterProduct(modifier: Modifier, visible: Boolean, onDismiss: () -> Unit, onAddNewProduct: (String, String) -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    AnimatedVisibility(visible = visible) {

        ModalBottomSheet(
            modifier = modifier,
            sheetState = sheetState,
            onDismissRequest = {
                onDismiss()
            },
        ) {
            var name by remember { mutableStateOf(TextFieldValue("")) }
            var value by remember { mutableStateOf(TextFieldValue("")) }

            Column(Modifier.padding(horizontal = 16.dp, 32.dp)) {
                Text(text = "Novo produto", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, lineHeight = 32.sp)
                Spacer(modifier = Modifier.height(35.dp))
                CustomInput(
                    modifier = Modifier.fillMaxWidth(),
                    startIcon = Icons.Filled.ShoppingCart,
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    hint = "Nome do produto",
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomInput(
                    modifier = Modifier.fillMaxWidth(),
                    startIcon = ImageVector.vectorResource(R.drawable.ic_money),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    value = value,
                    onValueChange = {
                        value = it
                    },
                    hint = "Valor unitário",
                )
                Spacer(modifier = Modifier.height(50.dp))
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onAddNewProduct(name.text, value.text)
                    }) {
                    Text("Salvar")
                }
            }
        }
    }
}