package com.abrnoc.application.presentation.utiles.network

import android.util.Base64
import io.nekohasekai.sagernet.bg.VpnService
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.apache.commons.codec.digest.HmacAlgorithms
import org.apache.commons.codec.digest.HmacUtils
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.net.UnknownHostException
import kotlin.coroutines.resume

private const val PING_KEY = "lci6UYRryo5rcQVpxfJ0fCs6UBY5eGyV"

class PingUtil {
    val wallClock = System::currentTimeMillis
    fun buildUdpPingData(serverKeyBase64: String?): ByteArray {
        val timestampSeconds = (System::currentTimeMillis.toString().toLong()) / 1000
        val timestampBytes = NetUtils.byteArrayBuilder { writeInt(timestampSeconds.toInt()) }
            .reversedArray()

        val hmacBytes = HmacUtils.getInitializedMac(
            HmacAlgorithms.HMAC_SHA_256,
            PING_KEY.toByteArray(Charsets.US_ASCII),
        ).apply {
            if (serverKeyBase64 != null) update(Base64.decode(serverKeyBase64, 0))
            update(timestampBytes)
        }.doFinal()

        return NetUtils.byteArrayBuilder {
            write(byteArrayOf(0xfe.toByte(), 0x01))
            write(timestampBytes)
            write(hmacBytes)
        }
    }

    suspend fun ping(
        ip: String,
        port: Int,
        pingData: ByteArray,
        tcp: Boolean,
        timeout: Int = 5000,
    ): Boolean = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            val result = try {
                val address = InetSocketAddress(InetAddress.getByName(ip), port)
                val result = if (tcp) {
                    pingTcp(pingData, address, continuation, timeout)
                } else {
                    pingUdp(pingData, address, continuation, timeout)
                }
                result
            } catch (e: IOException) {
                if (continuation.isActive) {
                    val protocol = if (tcp) "TCP" else "UDP"
//                    ProtonLogger.log(
//                        ConnConnectScanFailed,
//                        "destination: $ip:$port ($protocol); error: $e",
//                    )
                }
                false
            }
            continuation.resume(result)
        }
    }

    private fun pingTcp(
        pingData: ByteArray,
        socketAddress: SocketAddress,
        continuation: CancellableContinuation<*>,
        timeout: Int,
    ): Boolean {
        Socket().use {socket ->
            continuation.invokeOnCancellation {
                socket.close()
            }
            socket.soTimeout = timeout
            protectSocket(socket)
            socket.connect(socketAddress, timeout)
            return if (pingData.isEmpty()) {
                socket.isConnected
            } else {
                socket.getOutputStream().apply {
                    write(pingData)
                    flush()
                }
                socket.getInputStream().read() != -1
            }

        }
    }

    private fun pingUdp(
        pingData: ByteArray,
        socketAddress: SocketAddress,
        continuation: CancellableContinuation<*>,
        timeout: Int,
    ): Boolean {
        DatagramSocket().use { socket ->
            continuation.invokeOnCancellation {
                socket.close()
            }
            val packet = DatagramPacket(pingData, pingData.size, socketAddress)
            socket.soTimeout = timeout
            protectSocket(socket)
            socket.send(packet)
            val responsePacket = DatagramPacket(ByteArray(3), 3)
            socket.receive(responsePacket)
            val expectedResponse = byteArrayOf(0xfe.toByte(), 0x01, 0x01)
            return responsePacket.data.contentEquals(expectedResponse)
        }
    }
    private fun protectSocket(socket: Socket) {
        if (!socket.isBound) {
            socket.bind(InetSocketAddress(0))
        }
        val success = try {
            (VpnService.instance)?.protect(socket) ?: false
        } catch (e: NullPointerException) {
            // NPE is thrown if the socket is closed. A socket can be closed e.g. by invokeOnCancellation in pingTcp.
            false
        }
        if (!success) logFailedProtect()
    }

    private fun protectSocket(socket: DatagramSocket) {
        if (!socket.isBound) {
            socket.bind(InetSocketAddress(0))
        }
        val success = try {
            (VpnService.instance)?.protect(socket) ?: false
        } catch (e: NullPointerException) {
            // NPE is thrown if the socket is closed. A socket can be closed e.g. by invokeOnCancellation in pingUdp.
            false
        }
        if (!success) logFailedProtect()
    }

    private fun logFailedProtect() {
//        ProtonLogger.logCustom(LogLevel.WARN, LogCategory.CONN_SERVER_SWITCH, "ping socket not protected")
    }

    suspend fun pingTcpByHostname(hostname: String, port: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val hostAddress = InetAddress.getByName(hostname).hostAddress
            if (hostAddress == null) {
                false
            } else {
                ping(hostAddress, port, ByteArray(0), true)
            }
        } catch (e: UnknownHostException) {
            false
        }
    }
}