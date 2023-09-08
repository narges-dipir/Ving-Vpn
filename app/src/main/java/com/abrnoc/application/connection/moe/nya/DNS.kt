package com.abrnoc.application.connection.moe.nya

import com.abrnoc.application.ftm.v2ray.V2RayConfig.DnsObject.ServerObject
import com.abrnoc.application.presentation.connection.DataStore


object DNS {
    fun ServerObject.applyDNSNetworkSettings(isDirect: Boolean) {
        if (isDirect) {
            if (DataStore.dnsNetwork.contains("NoDirectIPv4")) this.queryStrategy = "UseIPv6"
            if (DataStore.dnsNetwork.contains("NoDirectIPv6")) this.queryStrategy = "UseIPv4"
        } else {
            if (DataStore.dnsNetwork.contains("NoRemoteIPv4")) this.queryStrategy = "UseIPv6"
            if (DataStore.dnsNetwork.contains("NoRemoteIPv6")) this.queryStrategy = "UseIPv4"
        }
    }
}
