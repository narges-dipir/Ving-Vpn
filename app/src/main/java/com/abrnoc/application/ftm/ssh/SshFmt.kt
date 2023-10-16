package com.abrnoc.application.ftm.ssh

fun parseShh(server: String): SSHBean {
//    val link = server.replace("ssh://", "https://").toHttpUrlOrNull()
//        ?: error("invalid trojan link $server")
    val parts = server.split("&")
    val configMap = mutableMapOf<String, String>()
    for (part in parts) {
        val configPart = part.split("=")
        if (configPart.size == 2) {
            val name = configPart[0]
            val value = configPart[1]
            configMap[name] = value
        }

    }

    return SSHBean().apply {
        serverAddress = configMap["address"]
        serverPort = configMap["port"]?.toInt() ?: 8080
        username = configMap["username"]
        authType = 1
        password = configMap["password"]
    }
}