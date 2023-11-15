package com.abrnoc.application.presentation.screens.purchase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import com.abrnoc.application.presentation.ui.theme.Ocean13
import com.abrnoc.application.presentation.ui.theme.Ocean9

@Composable
fun RadioButtonWithImageRow(
    duration: String,
    selected: Boolean,
    onOptionSelected: () -> Unit,
    discount: String,
    isBestDeal: Boolean? = false,
    save: String,
    pricePerMonth: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (selected) {
            Ocean9.copy(alpha = 0.4f)
        } else {
            Color.White.copy(alpha = 0.4f)
        },
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .selectable(
                selected,
                onClick = onOptionSelected,
                role = Role.RadioButton
            )
            .wrapContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Text(
                    text = "Best Deal",
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(25.dp)
                        .background(color = Ocean13),
                    textAlign = TextAlign.Center,
                    )
                Text(
                    text = "plan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ApplicationTheme.colors.textPrimary,
                    modifier = Modifier.padding(8.dp)
                )
                Row {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "plan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ApplicationTheme.colors.textPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

            }
        }
    }
}

@Preview
@Composable
private fun PreviewRadio() {
    RadioButtonWithImageRow(
        duration = "1 month",
        selected = false,
        onOptionSelected = {},
        discount = "30%",
        isBestDeal = false,
        save = "$4",
        pricePerMonth = "12.99",
    )
}