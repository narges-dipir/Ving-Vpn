package com.abrnoc.application.presentation.mainConnection.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.abrnoc.application.R
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import com.abrnoc.application.presentation.ui.theme.Ocean4
import com.abrnoc.application.repository.model.DefaultConfig

@Composable
fun ConnectionItem(defaultConfig: DefaultConfig? = null) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.padding(start = 8.dp)) {
//            Image(
//                painter = painterResource(id = R.drawable.us_flag),
//                contentDescription = "icon flag",
//                modifier = Modifier.clip(CircleShape)
//            )
            val parts = defaultConfig?.flag?.split('/')
            val countryCode = parts?.get(2)
            val svgImageUrl = "https://raw.githubusercontent.com/hampusborgos/country-flags/main/svg/$countryCode.svg"
            println(" the image is  url  $svgImageUrl")
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(svgImageUrl)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "flag icon image",
                modifier = Modifier.clip(CircleShape)
                    .size(32.dp)
                    .aspectRatio(1f),
                loading = {
                    CircularProgressIndicator()
                }
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(start = 12.dp)) {
                Text(text = defaultConfig!!.country, color = ApplicationTheme.colors.textPrimary)
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.bar_icon),
                        contentDescription = ""
                    )
                    Text(
                        text = "0.ms",
                        modifier = Modifier.padding(start = 32.dp),
                        color = ApplicationTheme.colors.textSecondry
                    )
                }
            }
        }
        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = ApplicationTheme.colors.iconInteractiveInactive,
                contentColor = ApplicationTheme.colors.iconInteractiveInactive
            )

        ) {
            Row {
                Text(text = "Change", color = Ocean4)
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "change",
                    tint = Ocean4
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AbrnocApplicationTheme {
        val df = DefaultConfig(
            address = "172.86.76.146",
            port = 443,
            password = "3dc90e78b18c5e9c6f382dd3b42891d3",
            security = "tls",
            fingerprint = "chrome",
            alpn = "http/1.1",
            sni = "zire.ml",
            type = "TROJAN",
            country = "Luxembourg",
            flag = "proxy/flag/lu",
            url = "trojan://3dc90e78b18c5e9c6f382dd3b42891d3@172.86.76.146:443?security=tls&alpn=http/1.1&headerType=none&fp=chrome&type=tcp&sni=zire.ml",
            protocol = "Trojan"
        )
        ConnectionItem()
    }
}