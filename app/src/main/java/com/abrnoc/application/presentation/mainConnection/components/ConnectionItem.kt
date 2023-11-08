package com.abrnoc.application.presentation.mainConnection.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.abrnoc.application.presentation.ui.theme.AbrnocApplicationTheme
import com.abrnoc.application.presentation.ui.theme.ApplicationTheme
import com.abrnoc.application.presentation.ui.theme.Ocean4
import com.abrnoc.application.presentation.utiles.countryFlagUrl
import com.abrnoc.application.presentation.viewModel.model.DefaultConfig
import io.nekohasekai.sagernet.R

@Composable
fun ConnectionItem(defaultConfig: DefaultConfig? = null,
                   onClick: () -> Unit,
                   totalSize: Int,
                   pingViewModel: PingViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val ping by pingViewModel.ping.collectAsState()
    val pingDrawabl = remember {
        mutableIntStateOf(R.drawable.ping_four_bars)
    }
    val iconResourceMap = mapOf(
        "ping_one_bars" to R.drawable.ping_one_bars,
        "ping_two_bars" to R.drawable.ping_two_bars,
        "ping_three_bars" to R.drawable.ping_three_bars,
        "ping_four_bars" to R.drawable.ping_four_bars
        // Add mappings for all the icons you have
    )
    pingViewModel.startPing(totalSize)
    LaunchedEffect(key1 = ping) {
            pingDrawabl.intValue = iconResourceMap[defaultConfig?.id?.toInt()?.let { ping[it] }]!!
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .fillMaxWidth()
            .clickable {
                onClick()
            },
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
            val svgImageUrl = countryFlagUrl(countryCode)
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(svgImageUrl)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentDescription = "flag icon image",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(32.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop,
                loading = {
                    CircularProgressIndicator()
                }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = defaultConfig?.country ?: "",
                    color = ApplicationTheme.colors.textPrimary
                )
                Row {
                    Image(
                        painter = painterResource(id = pingDrawabl.intValue),
                        contentDescription = ""
                    )
                    Text(
                        text = defaultConfig?.protocol ?: "",
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
        ConnectionItem(onClick = {}, totalSize = 1)
    }
}

