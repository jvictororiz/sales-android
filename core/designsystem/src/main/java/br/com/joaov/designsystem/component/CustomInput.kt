package br.com.joaov.designsystem.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import br.com.joaov.designsystem.theme.PrimaryColor


@Composable
fun CustomInput(
    modifier: Modifier = Modifier,
    startIcon: ImageVector? = null,
    endIcon: ImageVector? = null,
    hint: String = "",
    value: TextFieldValue,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions? = null,
    onValueChange: (TextFieldValue) -> Unit,
) {
    return OutlinedTextField(
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = PrimaryColor
        ),
        maxLines = 1,
        value = value,
        leadingIcon = {
            startIcon?.let {
                Icon(imageVector = it, contentDescription = "")
            }
        },
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions ?: KeyboardOptions(keyboardType = KeyboardType.Text),
        onValueChange = onValueChange,
        label = { Text(text = hint) },
    )
}