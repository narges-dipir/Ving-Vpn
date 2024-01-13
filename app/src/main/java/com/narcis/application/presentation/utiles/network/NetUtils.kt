package com.narcis.application.presentation.utiles.network

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream


object NetUtils {
    fun byteArrayBuilder(block: DataOutputStream.() -> Unit): ByteArray {
        val byteStream = ByteArrayOutputStream()
        DataOutputStream(byteStream).use(block)
        return byteStream.toByteArray()
    }
}
