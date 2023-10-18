package com.abrnoc.application.presentation.mainConnection.bottomScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import com.abrnoc.application.presentation.ui.theme.Blue1
import com.abrnoc.application.presentation.ui.theme.Ocean11
import com.abrnoc.application.presentation.ui.theme.Ocean13

@Composable
fun ShoppingItem(
    onClick: () -> Unit,
    item: ShoppingItem
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(15))
            .fillMaxWidth()
            .padding(start = 18.dp, end = 18.dp, bottom = 32.dp)
            .clickable {
                onClick()
            }
            .background(color = ApplicationTheme.colors.uiBackground.copy(alpha = 0.4f)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            if (item.isBestDeal)
            Text(
                text = "Best Deal",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(25.dp)
                    .background(color = Ocean13),
                textAlign = TextAlign.Center,

            )
            Row {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Text(
                            text = item.plan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = ApplicationTheme.colors.textPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            text = "${item.off}% OFF",
                            color = ApplicationTheme.colors.textSecondry,
                            fontSize = 8.sp,
                            modifier = Modifier
                                .background(color = ApplicationTheme.colors.uiBackground.copy(alpha = 0.8f))
                                .shadow(elevation = 2.dp)
                                .clip(RoundedCornerShape(20))
                                .padding(start = 8.dp, end = 4.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$",
                            fontSize = 15.sp,
                            color = Blue1,
                            modifier = Modifier.padding(
                                start = 12.dp, end = 4.dp
                            )
                        )
                        Text(
                            text = item.pricePerMonth,
                            color = Ocean11,
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                        )
                        Text(text = "  /month", fontSize = 8.sp, color = Blue1)
                    }

                }
                Spacer(modifier = Modifier.width(100.dp))
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    RadioButton(
                        selected = false, onClick = { /*TODO*/ },
//                    colors = RadioButtonDefaults.colors()
                    )
                    Text(text = if(item.save.isNotBlank()) "Save $${item.save}" else "", fontSize = 10.sp, color = Blue1)
                }
            }
        }
    }
}

@Preview
@Composable
private fun preview() {
    val item = ShoppingItem(
        plan = "1-Month Plan",
        pricePerMonth = "3.99",
        off = "0",
        save = "120",
        isBestDeal = true
    )
    AbrnocApplicationTheme {
        ShoppingItem(
            onClick = {},
            item
        )
    }
}