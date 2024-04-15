package br.com.joaov.designsystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.joaov.designsystem.theme.ColorText

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BottomSheetOptions(
    visible: Boolean,
    onDismiss: () -> Unit,
    options: Map<String, () -> Unit>
) {
    val sheetState = rememberModalBottomSheetState()
    AnimatedVisibility(visible = visible) {
        ModalBottomSheet(
            modifier = Modifier.wrapContentHeight(),
            sheetState = sheetState,
            onDismissRequest = {
                onDismiss()
            },
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = "Selecione uma operação:",
                color = Color.Black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.padding(top = 36.dp))
            HorizontalDivider()
            options.toList().forEach { item ->
                Text(
                    modifier = Modifier
                        .height(50.dp)
                        .clickable {
                            item.second()
                        }
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .fillMaxWidth()

                        .padding(horizontal = 16.dp),
                    text = item.first,
                    color = ColorText,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 16.sp
                )
                HorizontalDivider()
            }
            Spacer(modifier = Modifier.padding(top = 10.dp))
        }
    }
}