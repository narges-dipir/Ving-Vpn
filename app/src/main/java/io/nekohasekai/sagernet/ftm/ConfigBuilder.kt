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

package io.nekohasekai.sagernet.ftm

import android.widget.Toast
import com.github.shadowsocks.plugin.PluginConfiguration
import com.github.shadowsocks.plugin.PluginManager
import io.nekohasekai.sagernet.IPv6Mode
import io.nekohasekai.sagernet.Key
import io.nekohasekai.sagernet.R
import io.nekohasekai.sagernet.SagerNet
import io.nekohasekai.sagernet.bg.VpnService
import io.nekohasekai.sagernet.database.DataStore
import io.nekohasekai.sagernet.database.ProxyEntity
import io.nekohasekai.sagernet.database.SagerDatabase
import io.nekohasekai.sagernet.ftm.gson.gson
import io.nekohasekai.sagernet.ftm.hysteria.isMultiPort
import io.nekohasekai.sagernet.ftm.ssh.SSHBean
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.BrowserForwarderObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.DNSOutboundConfigurationObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.DnsObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.DokodemoDoorInboundConfigurationObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.FreedomOutboundConfigurationObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.GrpcObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.HTTPInboundConfigurationObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.HTTPOutboundConfigurationObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.HttpObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.InboundObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.KcpObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.LazyInboundConfigurationObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.LazyOutboundConfigurationObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.OutboundObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.PolicyObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.QuicObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.ReverseObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.RoutingObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.SSHOutbountConfigurationObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.ShadowsocksOutboundConfigurationObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.SocksInboundConfigurationObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.SocksOutboundConfigurationObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.StreamSettingsObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.TLSObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.TcpObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.TrojanOutboundConfigurationObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.UTLSObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.VMessOutboundConfigurationObject
import io.nekohasekai.sagernet.ftm.v2ray.V2RayConfig.WebSocketObject
import io.nekohasekai.sagernet.ktx.isIpAddress
import io.nekohasekai.sagernet.ktx.mkPort
import io.nekohasekai.sagernet.utils.PackageCache
import moe.matsuri.nya.DNS.applyDNSNetworkSettings
import moe.matsuri.nya.neko.Plugins
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

const val TAG_SOCKS = "socks"
const val TAG_HTTP = "http"
const val TAG_TRANS = "trans"

const val TAG_DIRECT = "direct"
const val TAG_BYPASS = "bypass"
const val TAG_BLOCK = "block"

const val TAG_DNS_IN = "dns-in"
const val TAG_DNS_OUT = "dns-out"

const val LOCALHOST = "127.0.0.1"

class V2rayBuildResult(
    var config: String,
    var index: List<IndexEntity>,
    var wsPort: Int,
    var outboundTags: List<String>,
    var outboundTagsCurrent: List<String>,
    var outboundTagsAll: Map<String, ProxyEntity>,
    var bypassTag: String,
    val dumpUid: Boolean,
    val tryDomains: List<String>,
) {
    data class IndexEntity(var chain: LinkedHashMap<Int, ProxyEntity>)
}

fun buildV2RayConfig(
    proxy: ProxyEntity,
    forTest: Boolean = false,
): io.nekohasekai.sagernet.ftm.V2rayBuildResult {
    val outboundTags = ArrayList<String>()
    val outboundTagsCurrent = ArrayList<String>()
    val outboundTagsAll = HashMap<String, ProxyEntity>()
    val globalOutbounds = ArrayList<Long>()

    fun ProxyEntity.resolveChain(): MutableList<ProxyEntity> {
        val bean = requireBean()
        if (bean is io.nekohasekai.sagernet.ftm.internal.ChainBean) {
            val beans = SagerDatabase.proxyDao.getEntities(bean.proxies)
            val beansMap = beans.associateBy { it.id }
            val beanList = ArrayList<ProxyEntity>()
            for (proxyId in bean.proxies) {
                val item = beansMap[proxyId] ?: continue
                beanList.addAll(item.resolveChain())
            }
            return beanList.asReversed()
        }
        return mutableListOf(this)
    }

    val proxies = proxy.resolveChain()
    val extraRules = if (forTest) listOf() else SagerDatabase.rulesDao.enabledRules()
    val extraProxies =
        if (forTest) {
            mapOf()
        } else {
            SagerDatabase.proxyDao.getEntities(
                extraRules.mapNotNull { rule ->
                    rule.outbound.takeIf { it > 0 && it != proxy.id }
                }.toHashSet().toList()
            ).map { it.id to it.resolveChain() }.toMap()
        }

    val uidListDNSRemote = mutableListOf<Int>()
    val uidListDNSDirect = mutableListOf<Int>()
    val domainListDNSRemote = mutableListOf<String>()
    val domainListDNSDirect = mutableListOf<String>()
    val bypassDNSBeans = hashSetOf<io.nekohasekai.sagernet.ftm.AbstractBean>()

    val allowAccess = DataStore.allowAccess
    val bind = if (!forTest && allowAccess) "0.0.0.0" else LOCALHOST

    var remoteDns = DataStore.remoteDns.split("\n")
        .mapNotNull { dns -> dns.trim().takeIf { it.isNotBlank() && !it.startsWith("#") } }
    var directDNS = DataStore.directDns.split("\n")
        .mapNotNull { dns -> dns.trim().takeIf { it.isNotBlank() && !it.startsWith("#") } }
    val enableDnsRouting = DataStore.enableDnsRouting
    val useFakeDns = DataStore.enableFakeDns && !forTest
    val trafficSniffing = DataStore.trafficSniffing
    val indexMap = ArrayList<V2rayBuildResult.IndexEntity>()
    val requireHttp = !forTest && DataStore.requireHttp
    val requireTransproxy = if (forTest) false else DataStore.requireTransproxy
    val ipv6Mode = if (forTest) IPv6Mode.ENABLE else DataStore.ipv6Mode
    val resolveDestination = DataStore.resolveDestination
    val trafficStatistics = !forTest && DataStore.profileTrafficStatistics
    val tryDomains = mutableListOf<String>()
    var dumpUid = false

    return V2RayConfig().apply {
        dns = DnsObject().apply {
            fallbackStrategy = "disabled_if_any_match"

            hosts = DataStore.hosts.split("\n")
                .filter { it.isNotBlank() }
                .associate { it.substringBefore(" ") to it.substringAfter(" ") }
                .toMutableMap()

            if (useFakeDns) {
                fakedns = mutableListOf()
                fakedns.add(
                     V2RayConfig.FakeDnsObject().apply {
                        ipPool = "${VpnService.FAKEDNS_VLAN4_CLIENT}/15"
                        poolSize = 65535
                    }
                )
                remoteDns = listOf("fakedns")
            }

            servers = mutableListOf()

            servers.addAll(
                remoteDns.map {
                     DnsObject.StringOrServerObject().apply {
                        valueY = DnsObject.ServerObject().apply {
                            address = it
                            applyDNSNetworkSettings(false)
                        }
                    }
                }
            )

            when (ipv6Mode) {
                IPv6Mode.DISABLE -> {
                    queryStrategy = "UseIPv4"
                }

                IPv6Mode.ONLY -> {
                    queryStrategy = "UseIPv6"
                }
            }
        }

        log = V2RayConfig.LogObject().apply {
            loglevel = if (DataStore.enableLog) "debug" else "error"
        }

        policy = PolicyObject().apply {
            levels = mapOf(
                // dns
                "1" to PolicyObject.LevelPolicyObject().apply {
                    connIdle = 30
                }
            )

            if (trafficStatistics) {
                system = PolicyObject.SystemPolicyObject().apply {
                    statsOutboundDownlink = true
                    statsOutboundUplink = true
                }
            }
        }
        inbounds = mutableListOf()

        if (!forTest) {
            inbounds.add(
                InboundObject().apply {
                    tag =  TAG_SOCKS
                    listen = bind
                    port = DataStore.socksPort
                    protocol = "socks"
                    settings = LazyInboundConfigurationObject(
                        this,
                        SocksInboundConfigurationObject().apply {
                            auth = "noauth"
                            udp = true
                        }
                    )
                    if (trafficSniffing || useFakeDns) {
                        sniffing = InboundObject.SniffingObject().apply {
                            enabled = true
                            destOverride = when {
                                useFakeDns && !trafficSniffing -> listOf("fakedns")
                                useFakeDns -> listOf("fakedns", "http", "tls", "quic")
                                else -> listOf("http", "tls", "quic")
                            }
                            metadataOnly = useFakeDns && !trafficSniffing
                            routeOnly = true
                        }
                    }
                }
            )
        }

        if (requireHttp) {
            inbounds.add(
                InboundObject().apply {
                    tag =  TAG_HTTP
                    listen = bind
                    port = DataStore.httpPort
                    protocol = "http"
                    settings = LazyInboundConfigurationObject(
                        this,
                        HTTPInboundConfigurationObject().apply {
                            allowTransparent = true
                        }
                    )
                    if (trafficSniffing || useFakeDns) {
                        sniffing = InboundObject.SniffingObject().apply {
                            enabled = true
                            destOverride = when {
                                useFakeDns && !trafficSniffing -> listOf("fakedns")
                                useFakeDns -> listOf("fakedns", "http", "tls")
                                else -> listOf("http", "tls")
                            }
                            metadataOnly = useFakeDns && !trafficSniffing
                            routeOnly = true
                        }
                    }
                }
            )
        }

        if (requireTransproxy) {
            inbounds.add(
                InboundObject().apply {
                    tag =  TAG_TRANS
                    listen = bind
                    port = DataStore.transproxyPort
                    protocol = "dokodemo-door"
                    settings = LazyInboundConfigurationObject(
                        this,
                        DokodemoDoorInboundConfigurationObject().apply {
                            network = "tcp,udp"
                            followRedirect = true
                        }
                    )
                    if (trafficSniffing || useFakeDns) {
                        sniffing = InboundObject.SniffingObject().apply {
                            enabled = true
                            destOverride = when {
                                useFakeDns && !trafficSniffing -> listOf("fakedns")
                                useFakeDns -> listOf("fakedns", "http", "tls", "quic")
                                else -> listOf("http", "tls", "quic")
                            }
                            metadataOnly = useFakeDns && !trafficSniffing
                            routeOnly = true
                        }
                    }
                    when (DataStore.transproxyMode) {
                        1 -> streamSettings = StreamSettingsObject().apply {
                            sockopt = StreamSettingsObject.SockoptObject().apply {
                                tproxy = "tproxy"
                            }
                        }
                    }
                }
            )
        }

        outbounds = mutableListOf()

        // init routing object
        // set rules for wsUseBrowserForwarder and bypass LAN
        routing = RoutingObject().apply {
            domainStrategy = DataStore.domainStrategy

            rules = mutableListOf()

            val wsRules = HashMap<String, RoutingObject.RuleObject>()

            for (proxyEntity in proxies) {
                val bean = proxyEntity.requireBean()

                if (bean is io.nekohasekai.sagernet.ftm.v2ray.StandardV2RayBean && bean.type == "ws" && bean.wsUseBrowserForwarder == true) {
                    val route = RoutingObject.RuleObject().apply {
                        type = "field"
                        outboundTag =  TAG_DIRECT
                        when {
                            bean.host.isIpAddress() -> {
                                ip = listOf(bean.host)
                            }

                            bean.host.isNotBlank() -> {
                                domain = listOf(bean.host)
                            }

                            bean.serverAddress.isIpAddress() -> {
                                ip = listOf(bean.serverAddress)
                            }

                            else -> domain = listOf(bean.serverAddress)
                        }
                    }
                    wsRules[bean.host.takeIf { !it.isNullOrBlank() } ?: bean.serverAddress] = route
                }
            }

            rules.addAll(wsRules.values)

            if (!forTest && (requireHttp || DataStore.bypassLanInCore)) {
                rules.add(
                    RoutingObject.RuleObject().apply {
                        type = "field"
                        outboundTag =  TAG_BYPASS
                        ip = listOf("geoip:private")
                    }
                )
            }
        }

        // returns outbound tag
        fun buildChain(
            chainId: Long,
            profileList: List<ProxyEntity>,
        ): String {
            lateinit var currentOutbound: OutboundObject
            lateinit var pastOutbound: OutboundObject
            lateinit var pastInboundTag: String
            var pastEntity: ProxyEntity? = null
            val chainMap = LinkedHashMap<Int, ProxyEntity>()
            indexMap.add(
                 V2rayBuildResult.IndexEntity(
                    chainMap
                )
            )
            val chainOutbounds = ArrayList<OutboundObject>()

            // chainTagOut: v2ray outbound tag for this chain
            var chainTagOut = ""
            val chainTag = "c-$chainId"
            var muxApplied = false

            // v2sekai's outbound domainStrategy
            fun genDomainStrategy(noAsIs: Boolean): String {
                return when {
                    !resolveDestination && !noAsIs -> "AsIs"
                    ipv6Mode == IPv6Mode.DISABLE -> "UseIPv4"
                    ipv6Mode == IPv6Mode.PREFER -> "PreferIPv6"
                    ipv6Mode == IPv6Mode.ONLY -> "UseIPv6"
                    else -> "PreferIPv4"
                }
            }

            var currentDomainStrategy = genDomainStrategy(false)

            profileList.forEachIndexed { index, proxyEntity ->
                val bean = proxyEntity.requireBean()
                currentOutbound = OutboundObject()

                // tagOut: v2ray outbound tag for a profile
                // profile2 (in) (global)   tag g-(id)
                // profile1                 tag (chainTag)-(id)
                // profile0 (out)           tag (chainTag)-(id) / single: "proxy"
                var tagOut = "$chainTag-${proxyEntity.id}"

                // needGlobal: can only contain one?
                var needGlobal = false

                // first profile set as global
                if (index == profileList.lastIndex) {
                    needGlobal = true
                    tagOut = "g-" + proxyEntity.id
                    bypassDNSBeans += proxyEntity.requireBean()
                }

                // last profile set as "proxy"
                if (chainId == 0L && index == 0) {
                    tagOut = "proxy"
                }

                // chain rules
                if (index > 0) {
                    // chain route/proxy rules
                    if (!pastEntity!!.needExternal()) {
                        pastOutbound.proxySettings = OutboundObject.ProxySettingsObject().apply {
                            tag = tagOut
                            transportLayer = true
                        }
                    } else {
                        routing.rules.add(
                            RoutingObject.RuleObject().apply {
                                type = "field"
                                inboundTag = listOf(pastInboundTag)
                                outboundTag = tagOut
                            }
                        )
                    }
                } else {
                    // index == 0 means last profile in chain / not chain
                    chainTagOut = tagOut
                    outboundTags.add(tagOut)
                    if (chainId == 0L) outboundTagsCurrent.add(tagOut)
                }

                if (needGlobal) {
                    if (globalOutbounds.contains(proxyEntity.id)) {
                        return@forEachIndexed
                    }
                    globalOutbounds.add(proxyEntity.id)
                }

                outboundTagsAll[tagOut] = proxyEntity

                // Chain outbound
                if (proxyEntity.needExternal()) {
                    val localPort = mkPort()
                    chainMap[localPort] = proxyEntity
                    currentOutbound.apply {
                        protocol = "socks"
                        settings = LazyOutboundConfigurationObject(
                            this,
                            SocksOutboundConfigurationObject().apply {
                                servers = listOf(
                                    SocksOutboundConfigurationObject.ServerObject()
                                        .apply {
                                            address =
                                                 LOCALHOST
                                            port = localPort
                                        }
                                )
                            }
                        )
                    }
                } else { // internal outbound
                    currentOutbound.apply {
                        val keepAliveInterval = DataStore.tcpKeepAliveInterval
                        val needKeepAliveInterval = keepAliveInterval !in intArrayOf(0, 15)

                        if (bean is io.nekohasekai.sagernet.ftm.v2ray.StandardV2RayBean) {
                            when (bean) {
                                is io.nekohasekai.sagernet.ftm.socks.SOCKSBean -> {
                                    protocol = "socks"
                                    settings = LazyOutboundConfigurationObject(
                                        this,
                                        SocksOutboundConfigurationObject().apply {
                                            servers =
                                                listOf(
                                                    SocksOutboundConfigurationObject.ServerObject()
                                                        .apply {
                                                            address = bean.serverAddress
                                                            port = bean.serverPort
                                                            if (!bean.username.isNullOrBlank()) {
                                                                users = listOf(
                                                                    SocksOutboundConfigurationObject.ServerObject.UserObject()
                                                                        .apply {
                                                                            user = bean.username
                                                                            pass = bean.password
                                                                        }
                                                                )
                                                            }
                                                        }
                                                )
                                            version = bean.protocolVersionName()
                                        }
                                    )
                                }

                                is io.nekohasekai.sagernet.ftm.http.HttpBean -> {
                                    protocol = "http"
                                    settings = LazyOutboundConfigurationObject(
                                        this,
                                        HTTPOutboundConfigurationObject().apply {
                                            servers =
                                                listOf(
                                                    HTTPOutboundConfigurationObject.ServerObject()
                                                        .apply {
                                                            address = bean.serverAddress
                                                            port = bean.serverPort
                                                            if (!bean.username.isNullOrBlank()) {
                                                                users = listOf(
                                                                    HTTPInboundConfigurationObject.AccountObject()
                                                                        .apply {
                                                                            user = bean.username
                                                                            pass = bean.password
                                                                        }
                                                                )
                                                            }
                                                        }
                                                )
                                        }
                                    )
                                }

                                is io.nekohasekai.sagernet.ftm.v2ray.VMessBean -> {
                                    protocol = "vmess"
                                    settings = LazyOutboundConfigurationObject(
                                        this,
                                        VMessOutboundConfigurationObject().apply {
                                            vnext =
                                                listOf(
                                                    VMessOutboundConfigurationObject.ServerObject()
                                                        .apply {
                                                            address = bean.serverAddress
                                                            port = bean.serverPort
                                                            users =
                                                                listOf(
                                                                    VMessOutboundConfigurationObject.ServerObject.UserObject()
                                                                        .apply {
                                                                            id = bean.uuid
                                                                            alterId = bean.alterId
                                                                            security =
                                                                                bean.encryption.takeIf { it.isNotBlank() }
                                                                                    ?: "auto"
                                                                            experimental = ""
                                                                            if (bean.experimentalAuthenticatedLength) {
                                                                                experimental += "AuthenticatedLength"
                                                                            }
                                                                            if (bean.experimentalNoTerminationSignal) {
                                                                                experimental += "NoTerminationSignal"
                                                                            }
                                                                            if (experimental.isBlank()) {
                                                                                experimental =
                                                                                    null
                                                                            }
                                                                        }
                                                                )
                                                            when (bean.packetEncoding) {
                                                                1 -> {
                                                                    packetEncoding = "packet"
                                                                    currentDomainStrategy =
                                                                        genDomainStrategy(
                                                                            true
                                                                        )
                                                                }

                                                                2 -> packetEncoding = "xudp"
                                                            }
                                                        }
                                                )
                                        }
                                    )
                                }

                                is io.nekohasekai.sagernet.ftm.trojan.TrojanBean -> {
                                    protocol = "trojan"
                                    settings = LazyOutboundConfigurationObject(
                                        this,
                                        TrojanOutboundConfigurationObject().apply {
                                            servers =
                                                listOf(
                                                    TrojanOutboundConfigurationObject.ServerObject()
                                                        .apply {
                                                            address = bean.serverAddress
                                                            port = bean.serverPort
                                                            password = bean.password
                                                        }
                                                )
                                        }
                                    )
                                }
                            }

                            streamSettings = StreamSettingsObject().apply {
                                network = bean.type

                                if (bean.security.isNotBlank()) {
                                    security = bean.security
                                }
                                val v5_utls = bean.utlsFingerprint.isNotBlank()
                                if (security == "tls") {
                                    tlsSettings = TLSObject().apply {
                                        if (bean.sni.isNotBlank()) {
                                            serverName = bean.sni
                                        }

                                        if (bean.alpn.isNotBlank()) {
                                            val ds = bean.alpn.split("\n")
                                                .filter { it.isNotBlank() }
                                            if (v5_utls) {
                                                nextProtocol = ds
                                            } else {
                                                alpn = ds
                                            }
                                        }

                                        if (bean.certificates.isNotBlank()) {
                                            disableSystemRoot = true
                                            certificates = listOf(
                                                TLSObject.CertificateObject()
                                                    .apply {
                                                        usage =
                                                            if (v5_utls) "ENCIPHERMENT" else "verify"
                                                        certificate = bean.certificates.split("\n")
                                                            .filter { it.isNotBlank() }
                                                    }
                                            )
                                        }

                                        if (bean.pinnedPeerCertificateChainSha256.isNotBlank()) {
                                            pinnedPeerCertificateChainSha256 =
                                                bean.pinnedPeerCertificateChainSha256.split("\n")
                                                    .filter { it.isNotBlank() }
                                        }

                                        if (bean.allowInsecure) {
                                            allowInsecure = true
                                        }
                                    }
                                    if (v5_utls) {
                                        utlsSettings = UTLSObject().apply {
                                            imitate = bean.utlsFingerprint
                                            tlsConfig = tlsSettings
                                        }
                                        tlsSettings = null
                                        security = "utls"
                                    }
                                }

                                when (network) {
                                    "tcp" -> {
                                        tcpSettings = TcpObject().apply {
                                            if (bean.headerType == "http") {
                                                header = TcpObject.HeaderObject().apply {
                                                    type = "http"
                                                    if (bean.host.isNotBlank() || bean.path.isNotBlank()) {
                                                        request =
                                                            TcpObject.HeaderObject.HTTPRequestObject()
                                                                .apply {
                                                                    headers = mutableMapOf()
                                                                    if (bean.host.isNotBlank()) {
                                                                        headers["Host"] =
                                                                            TcpObject.HeaderObject.StringOrListObject()
                                                                                .apply {
                                                                                    valueY =
                                                                                        bean.host.split(
                                                                                            ","
                                                                                        )
                                                                                            .map { it.trim() }
                                                                                }
                                                                    }
                                                                    if (bean.path.isNotBlank()) {
                                                                        path = bean.path.split(",")
                                                                    }
                                                                }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    "kcp" -> {
                                        kcpSettings = KcpObject().apply {
                                            mtu = 1350
                                            tti = 50
                                            uplinkCapacity = 12
                                            downlinkCapacity = 100
                                            congestion = false
                                            readBufferSize = 1
                                            writeBufferSize = 1
                                            header = KcpObject.HeaderObject().apply {
                                                type = bean.headerType
                                            }
                                            if (bean.mKcpSeed.isNotBlank()) {
                                                seed = bean.mKcpSeed
                                            }
                                        }
                                    }

                                    "ws" -> {
                                        wsSettings = WebSocketObject().apply {
                                            headers = mutableMapOf()

                                            if (bean.host.isNotBlank()) {
                                                headers["Host"] = bean.host
                                            }

                                            path = bean.path.takeIf { it.isNotBlank() } ?: "/"

                                            if (bean.wsMaxEarlyData > 0) {
                                                maxEarlyData = bean.wsMaxEarlyData
                                            }

                                            if (bean.earlyDataHeaderName.isNotBlank()) {
                                                earlyDataHeaderName = bean.earlyDataHeaderName
                                            }

                                            if (bean.wsUseBrowserForwarder) {
                                                useBrowserForwarding = true
                                                browserForwarder = BrowserForwarderObject().apply {
                                                    listenAddr =
                                                         LOCALHOST
                                                    listenPort = mkPort()
                                                }
                                            }
                                        }
                                    }

                                    "http" -> {
                                        network = "http"

                                        httpSettings = HttpObject().apply {
                                            if (bean.host.isNotBlank()) {
                                                host = bean.host.split(",")
                                            }

                                            path = bean.path.takeIf { it.isNotBlank() } ?: "/"
                                        }
                                    }

                                    "quic" -> {
                                        quicSettings = QuicObject().apply {
                                            security = bean.quicSecurity.takeIf { it.isNotBlank() }
                                                ?: "none"
                                            key = bean.quicKey
                                            header = QuicObject.HeaderObject().apply {
                                                type = bean.headerType.takeIf { it.isNotBlank() }
                                                    ?: "none"
                                            }
                                        }
                                    }

                                    "grpc" -> {
                                        grpcSettings = GrpcObject().apply {
                                            serviceName = bean.grpcServiceName
                                        }
                                    }
                                }

                                if (needKeepAliveInterval) {
                                    sockopt = StreamSettingsObject.SockoptObject().apply {
                                        tcpKeepAliveInterval = keepAliveInterval
                                    }
                                }
                            }
                        } else if (bean is io.nekohasekai.sagernet.ftm.shadowsocks.ShadowsocksBean || bean is io.nekohasekai.sagernet.ftm.shadowsocksr.ShadowsocksRBean) {
                            protocol = "shadowsocks"
                            settings = LazyOutboundConfigurationObject(
                                this,
                                ShadowsocksOutboundConfigurationObject().apply {
                                    servers =
                                        listOf(
                                            ShadowsocksOutboundConfigurationObject.ServerObject()
                                                .apply {
                                                    address = bean.serverAddress
                                                    port = bean.serverPort
                                                    when (bean) {
                                                        is io.nekohasekai.sagernet.ftm.shadowsocks.ShadowsocksBean -> {
                                                            method = bean.method
                                                            password = bean.password
                                                            experimentReducedIvHeadEntropy =
                                                                bean.experimentReducedIvHeadEntropy
                                                        }

                                                        is io.nekohasekai.sagernet.ftm.shadowsocksr.ShadowsocksRBean -> {
                                                            method = bean.method
                                                            password = bean.password
                                                        }
                                                    }
                                                }
                                        )
                                    if (needKeepAliveInterval) {
                                        streamSettings = StreamSettingsObject().apply {
                                            sockopt = StreamSettingsObject.SockoptObject().apply {
                                                tcpKeepAliveInterval = keepAliveInterval
                                            }
                                        }
                                    }
                                    if (bean is io.nekohasekai.sagernet.ftm.shadowsocksr.ShadowsocksRBean) {
                                        plugin = "shadowsocksr"
                                        pluginArgs = listOf(
                                            "--obfs=${bean.obfs}",
                                            "--obfs-param=${bean.obfsParam}",
                                            "--protocol=${bean.protocol}",
                                            "--protocol-param=${bean.protocolParam}"
                                        )
                                    } else if (bean is io.nekohasekai.sagernet.ftm.shadowsocks.ShadowsocksBean && bean.plugin.isNotBlank()) {
                                        val pluginConfiguration = PluginConfiguration(bean.plugin)
                                        try {
                                            PluginManager.init(pluginConfiguration)
                                                ?.let { (path, opts, _) ->
                                                    plugin = path
                                                    pluginOpts = opts.toString()
                                                    // Shadowsocks 传统艺能
                                                    if (DataStore.serviceMode == Key.MODE_VPN) {
                                                        pluginArgs = listOf("-V")
                                                    }
                                                }
                                        } catch (e: PluginManager.PluginNotFoundException) {
                                            if (e.plugin in arrayOf("v2ray-plugin", "obfs-local")) {
                                                plugin = e.plugin
                                                pluginOpts = pluginConfiguration.getOptions()
                                                    .toString()
                                            } else {
                                                throw e
                                            }
                                        }
                                    }
                                }
                            )
                        } else if (bean is io.nekohasekai.sagernet.ftm.ssh.SSHBean) {
                            protocol = "ssh"
                            settings = LazyOutboundConfigurationObject(
                                this,
                                SSHOutbountConfigurationObject().apply {
                                    address = bean.finalAddress
                                    port = bean.finalPort
                                    user = bean.username
                                    when (bean.authType) {
                                         SSHBean.AUTH_TYPE_PRIVATE_KEY -> {
                                            privateKey = bean.privateKey
                                            password = bean.privateKeyPassphrase
                                        }

                                        else -> {
                                            password = bean.password
                                        }
                                    }
                                    publicKey = bean.publicKey
                                }
                            )
                            streamSettings = StreamSettingsObject().apply {
                                if (needKeepAliveInterval) {
                                    sockopt = StreamSettingsObject.SockoptObject().apply {
                                        tcpKeepAliveInterval = keepAliveInterval
                                    }
                                }
                            }
                        }

                        if (!muxApplied && proxyEntity.needCoreMux()) {
                            muxApplied = true
                            mux = OutboundObject.MuxObject().apply {
                                enabled = true
                                concurrency = DataStore.muxConcurrency
                                if (bean is io.nekohasekai.sagernet.ftm.v2ray.StandardV2RayBean) {
                                    when (bean.packetEncoding) {
                                        1 -> {
                                            packetEncoding = "packet"
                                        }

                                        2 -> {
                                            packetEncoding = "xudp"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                pastEntity?.requireBean()?.apply {
                    // don't loopback
                    if (currentDomainStrategy != "AsIs" && !serverAddress.isIpAddress()) {
                        domainListDNSDirect.add("full:$serverAddress")
                    }
                }
                if (forTest) {
                    currentDomainStrategy = "AsIs"
                }

                currentOutbound.tag = tagOut
                currentOutbound.domainStrategy = currentDomainStrategy

                // External proxy need a dokodemo-door inbound to forward the traffic
                // For external proxy software, their traffic must goes to v2ray-core to use protected fd.
                if (bean.canMapping() && proxyEntity.needExternal()) {
                    // With ss protect, don't use mapping
                    var needExternal = true
                    if (index == profileList.lastIndex) {
                        val pluginId = when (bean) {
                            is io.nekohasekai.sagernet.ftm.hysteria.HysteriaBean -> "hysteria-plugin"
                            is io.nekohasekai.sagernet.ftm.wireguard.WireGuardBean -> "wireguard-plugin"
                            is io.nekohasekai.sagernet.ftm.tuic.TuicBean -> "tuic-plugin"
                            else -> ""
                        }
                        if (Plugins.isUsingMatsuriExe(pluginId)) {
                            needExternal = false
                        }
                    }
                    if (needExternal) {
                        val mappingPort = mkPort()
                        bean.finalAddress =  LOCALHOST
                        bean.finalPort = mappingPort

                        inbounds.add(
                            InboundObject().apply {
                                listen =  LOCALHOST
                                port = mappingPort
                                tag = "$chainTag-mapping-${proxyEntity.id}"
                                protocol = "dokodemo-door"
                                settings = LazyInboundConfigurationObject(
                                    this,
                                    DokodemoDoorInboundConfigurationObject().apply {
                                        address = bean.serverAddress
                                        network = bean.network()
                                        port = bean.serverPort
                                    }
                                )

                                pastInboundTag = tag

                                // no chain rule and not outbound, so need to set to direct
                                if (index == profileList.lastIndex) {
                                    routing.rules.add(
                                        RoutingObject.RuleObject().apply {
                                            type = "field"
                                            inboundTag = listOf(tag)
                                            outboundTag =
                                                 TAG_DIRECT
                                        }
                                    )
                                }
                            }
                        )
                    }
                }

                outbounds.add(currentOutbound)
                chainOutbounds.add(currentOutbound)
                pastOutbound = currentOutbound
                pastEntity = proxyEntity
            }

            return chainTagOut
        }

        val tagProxy = buildChain(0, proxies)
        val tagMap = mutableMapOf<Long, String>()
        extraProxies.forEach { (key, entities) ->
            tagMap[key] = buildChain(key, entities)
        }

        val notVpn = DataStore.serviceMode != Key.MODE_VPN

        // apply user rules
        for (rule in extraRules) {
            val _uidList = rule.packages.map {
                PackageCache[it]?.takeIf { uid -> uid >= 1000 }
            }.toHashSet().filterNotNull()

            if (rule.packages.isNotEmpty()) {
                dumpUid = true
                if (notVpn) {
                    Toast.makeText(
                        SagerNet.application,
                        SagerNet.application.getString(R.string.route_need_vpn, rule.displayName()),
                        Toast.LENGTH_SHORT
                    ).show()
                    continue
                }
            }
            val ruleObj = RoutingObject.RuleObject().apply {
                type = "field"
                if (rule.packages.isNotEmpty()) {
                    PackageCache.awaitLoadSync()
                    uidList = _uidList
                }

                var _domainList: List<String>? = null
                if (rule.domains.isNotBlank()) {
                    domain = rule.domains.split("\n")
                    _domainList = domain
                }
                if (rule.ip.isNotBlank()) {
                    ip = rule.ip.split("\n")
                }
                if (rule.port.isNotBlank()) {
                    port = rule.port
                }
                if (rule.sourcePort.isNotBlank()) {
                    sourcePort = rule.sourcePort
                }
                if (rule.network.isNotBlank()) {
                    network = rule.network
                }
                if (rule.source.isNotBlank()) {
                    source = rule.source.split("\n")
                }
                if (rule.protocol.isNotBlank()) {
                    protocol = rule.protocol.split("\n")
                }
                if (rule.attrs.isNotBlank()) {
                    attrs = rule.attrs
                }

                if (rule.reverse) inboundTag = listOf("reverse-${rule.id}")

                // also bypass lookup
                // cannot use other outbound profile to lookup...
                if (rule.outbound == -1L) {
                    uidListDNSDirect += _uidList
                    if (_domainList != null) domainListDNSDirect += _domainList
                } else if (rule.outbound == 0L) {
                    uidListDNSRemote += _uidList
                    if (_domainList != null) domainListDNSRemote += _domainList
                }

                outboundTag = when (val outId = rule.outbound) {
                    0L -> tagProxy
                    -1L ->  TAG_BYPASS
                    -2L ->  TAG_BLOCK
                    else -> if (outId == proxy.id) tagProxy else tagMap[outId] ?: ""
                }
            }

            if (ruleObj.outboundTag.isNullOrBlank()) {
                Toast.makeText(
                    SagerNet.application,
                    "Warning: " + rule.displayName() + ": A non-existent outbound was specified.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                routing.rules.add(ruleObj)
            }

            if (rule.reverse) {
                outbounds.add(
                    OutboundObject().apply {
                        tag = "reverse-out-${rule.id}"
                        protocol = "freedom"
                        settings = LazyOutboundConfigurationObject(
                            this,
                            FreedomOutboundConfigurationObject().apply {
                                redirect = rule.redirect
                            }
                        )
                    }
                )
                if (reverse == null) {
                    reverse = ReverseObject().apply {
                        bridges = ArrayList()
                    }
                }
                reverse.bridges.add(
                    ReverseObject.BridgeObject().apply {
                        tag = "reverse-${rule.id}"
                        domain = rule.domains.substringAfter("full:")
                    }
                )
                routing.rules.add(
                    RoutingObject.RuleObject().apply {
                        type = "field"
                        inboundTag = listOf("reverse-${rule.id}")
                        outboundTag = "reverse-out-${rule.id}"
                    }
                )
            }
        }

        for (freedom in arrayOf(
             TAG_DIRECT,
             TAG_BYPASS
        )) outbounds.add(
            OutboundObject().apply {
                tag = freedom
                protocol = "freedom"
            }
        )

        outbounds.add(
            OutboundObject().apply {
                tag =  TAG_BLOCK
                protocol = "blackhole"
            }
        )

        if (!forTest) {
            inbounds.add(
                InboundObject().apply {
                    tag =  TAG_DNS_IN
                    listen = bind
                    port = DataStore.localDNSPort
                    protocol = "dokodemo-door"
                    settings = LazyInboundConfigurationObject(
                        this,
                        DokodemoDoorInboundConfigurationObject().apply {
                            address = if (!remoteDns.first().isIpAddress()) {
                                "8.8.8.8"
                            } else {
                                remoteDns.first()
                            }
                            network = "tcp,udp"
                            port = 53
                        }
                    )
                }
            )

            outbounds.add(
                OutboundObject().apply {
                    protocol = "dns"
                    tag =  TAG_DNS_OUT
                    settings = LazyOutboundConfigurationObject(
                        this,
                        DNSOutboundConfigurationObject().apply {
                            userLevel = 1
                            var dns = remoteDns.first()
                            if (dns.contains(":")) {
                                val lPort = dns.substringAfterLast(":")
                                dns = dns.substringBeforeLast(":")
                                lPort.toIntOrNull()?.apply { port = this }
                            }
                            if (dns.isIpAddress()) {
                                address = dns
                            } else if (dns.contains("://")) {
                                network = "tcp"
                                address = dns.substringAfter("://")
                            }
                        }
                    )
                }
            )
        }

        if (DataStore.directDnsUseSystem) {
            // finally able to use "localDns" now...
            directDNS = listOf("localhost")
        }

        // routing for DNS server
        for (dns in remoteDns) {
            if (!dns.isIpAddress()) continue
            routing.rules.add(
                0,
                RoutingObject.RuleObject().apply {
                    type = "field"
                    outboundTag = tagProxy
                    ip = listOf(dns)
                }
            )
        }

        for (dns in directDNS) {
            if (!dns.isIpAddress()) continue
            routing.rules.add(
                0,
                RoutingObject.RuleObject().apply {
                    type = "field"
                    outboundTag =  TAG_DIRECT
                    ip = listOf(dns)
                }
            )
        }

        // No need to "bypass IP"
        // see buildChain()
        val directLookupDomain = HashSet<String>()

        // Bypass Lookup for the first profile
        bypassDNSBeans.forEach {
            var serverAddr = it.serverAddress
            if (it is io.nekohasekai.sagernet.ftm.hysteria.HysteriaBean && it.isMultiPort()) {
                serverAddr = it.serverAddress.substringBeforeLast(":")
            }

            if (!serverAddr.isIpAddress()) {
                directLookupDomain.add("full:$serverAddr")
                if (DataStore.enhanceDomain) tryDomains.add(serverAddr)
            }
        }

        remoteDns.forEach {
            var address = it
            if (address.contains("://")) {
                address = address.substringAfter("://")
            }
            "https://$address".toHttpUrlOrNull()?.apply {
                if (!host.isIpAddress()) {
                    directLookupDomain.add("full:$host")
                }
            }
        }

        // dns object user rules
        if (enableDnsRouting) {
            dns.servers[0].valueY?.uidList = uidListDNSRemote.toHashSet().toList()
            dns.servers[0].valueY?.domains = domainListDNSRemote.toHashSet().toList()
            directLookupDomain += domainListDNSDirect
        }

        // add directDNS objects here
        if (directLookupDomain.isNotEmpty() || uidListDNSDirect.isNotEmpty()) {
            dns.servers.addAll(
                directDNS.map {
                    DnsObject.StringOrServerObject().apply {
                        valueY = DnsObject.ServerObject().apply {
                            address = it.replace("https://", "https+local://")
                            domains = directLookupDomain.toList()
                            uidList = uidListDNSDirect.toHashSet().toList()
                            fallbackStrategy = "disabled"
                            applyDNSNetworkSettings(true)
                        }
                    }
                }
            )
        }

        // Disable v2ray DNS for test (already remove in Go)
        if (forTest) dns.servers.clear()

        if (!forTest) {
            routing.rules.add(
                0,
                RoutingObject.RuleObject().apply {
                    type = "field"
                    inboundTag = listOf( TAG_DNS_IN)
                    outboundTag =  TAG_DNS_OUT
                }
            )
        }

        if (allowAccess) {
            // temp: fix crash
            routing.rules.add(
                RoutingObject.RuleObject().apply {
                    type = "field"
                    ip = listOf("255.255.255.255")
                    outboundTag =  TAG_BLOCK
                }
            )
        }

        if (trafficStatistics) stats = emptyMap()
    }.let {
         V2rayBuildResult(
            gson.toJson(it),
            indexMap,
            it.browserForwarder?.listenPort ?: 0,
            outboundTags,
            outboundTagsCurrent,
            outboundTagsAll,
             TAG_BYPASS,
            dumpUid,
            tryDomains
        )
    }
}
