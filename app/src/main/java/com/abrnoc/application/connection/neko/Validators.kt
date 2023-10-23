/******************************************************************************
 *                                                                            *
 * Copyright (C) 2021 by nekohasekai <contact-sagernet@sekai.icu>             *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 *  (at your option) any later version.                                       *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program. If not, see <http://www.gnu.org/licenses/>.       *
 *                                                                            *
 ******************************************************************************/

package com.abrnoc.application.connection.neko

import androidx.annotation.RawRes
import com.abrnoc.application.R
import io.nekohasekai.sagernet.ftm.AbstractBean
import io.nekohasekai.sagernet.ftm.http.HttpBean
import io.nekohasekai.sagernet.ftm.hysteria.HysteriaBean
import io.nekohasekai.sagernet.ftm.shadowsocks.ShadowsocksBean
import io.nekohasekai.sagernet.ftm.shadowsocksr.ShadowsocksRBean
import io.nekohasekai.sagernet.ftm.socks.SOCKSBean
import io.nekohasekai.sagernet.ftm.trojan.TrojanBean
import io.nekohasekai.sagernet.ftm.v2ray.StandardV2RayBean
import io.nekohasekai.sagernet.ftm.v2ray.VMessBean


interface ValidateResult
object ResultSecure : ValidateResult
object ResultLocal : ValidateResult
class ResultDeprecated(@RawRes val textRes: Int) : ValidateResult
class ResultInsecure(@RawRes val textRes: Int) : ValidateResult
class ResultInsecureText(val text: String) : ValidateResult

val ssSecureList = "(gcm|poly1305)".toRegex()

fun io.nekohasekai.sagernet.ftm.AbstractBean.isInsecure(): ValidateResult {
    if (serverAddress.isIpAddress()) {
        if (serverAddress.startsWith("127.") || serverAddress.startsWith("::")) {
            return ResultLocal
        }
    }
    if (this is io.nekohasekai.sagernet.ftm.shadowsocks.ShadowsocksBean) {
        if (plugin.isBlank() || PluginConfiguration(plugin).selected == "obfs-local") {
            if (!method.contains(ssSecureList)) {
                return ResultInsecure(R.raw.shadowsocks_stream_cipher)
            }
        }
    } else if (this is io.nekohasekai.sagernet.ftm.shadowsocksr.ShadowsocksRBean) {
        return ResultInsecure(R.raw.shadowsocksr)
    } else if (this is io.nekohasekai.sagernet.ftm.http.HttpBean) {
        if (!isTLS()) return ResultInsecure(R.raw.not_encrypted)
    } else if (this is io.nekohasekai.sagernet.ftm.socks.SOCKSBean) {
        if (!isTLS()) return ResultInsecure(R.raw.not_encrypted)
    } else if (this is io.nekohasekai.sagernet.ftm.v2ray.VMessBean) {
        if (security in arrayOf("", "none")) {
            if (encryption in arrayOf("none", "zero")) {
                return ResultInsecure(R.raw.not_encrypted)
            }
        }
        if (type == "kcp" && mKcpSeed.isBlank()) {
            return ResultInsecure(R.raw.mkcp_no_seed)
        }
        if (allowInsecure) return ResultInsecure(R.raw.insecure)
        if (alterId > 0) return ResultDeprecated(R.raw.vmess_md5_auth)
    } else if (this is io.nekohasekai.sagernet.ftm.hysteria.HysteriaBean) {
        if (allowInsecure) return ResultInsecure(R.raw.insecure)
    } else if (this is io.nekohasekai.sagernet.ftm.trojan.TrojanBean) {
        if (security in arrayOf("", "none")) return ResultInsecure(R.raw.not_encrypted)
        if (allowInsecure) return ResultInsecure(R.raw.insecure)
    } else if (this is NekoBean) {
        val hint = sharedStorage.optString("insecureHint")
        if (hint.isNotBlank()) return ResultInsecureText(hint)
    }
    return ResultSecure
}

fun io.nekohasekai.sagernet.ftm.v2ray.StandardV2RayBean.isTLS(): Boolean {
    return security == "tls"
}

fun io.nekohasekai.sagernet.ftm.v2ray.StandardV2RayBean.setTLS(boolean: Boolean) {
    security = if (boolean) "tls" else ""
}
