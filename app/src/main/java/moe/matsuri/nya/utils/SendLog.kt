package moe.matsuri.nya.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.FileProvider
import io.nekohasekai.sagernet.BuildConfig
import io.nekohasekai.sagernet.R
import com.narcis.application.presentation.connection.Logs
import com.narcis.application.presentation.connection.use
import io.nekohasekai.sagernet.ktx.app
import io.nekohasekai.sagernet.utils.CrashHandler
import libcore.Libcore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object SendLog {
    // Create full log and send
    fun sendLog(context: Context, title: String) {
        val logFile = File.createTempFile(
            "$title ",
            ".log",
            File(app.cacheDir, "log").also { it.mkdirs() })

        var report = CrashHandler.buildReportHeader()

        report += "Logcat: \n\n"

        logFile.writeText(report)

        try {
            Runtime.getRuntime().exec(arrayOf("logcat", "-d")).inputStream.use(
                FileOutputStream(
                    logFile, true
                )
            )
            logFile.appendText("\n")
            logFile.appendBytes(Libcore.nekoLogGet())
        } catch (e: IOException) {
            Logs.w(e)
            logFile.appendText("Export logcat error: " + CrashHandler.formatThrowable(e))
        }
        if ( Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP ) {
        context.startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).setType("text/x-log")
                    .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .putExtra(
                        Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                            context, BuildConfig.APPLICATION_ID + ".cache", logFile
                        )
                    ), context.getString(R.string.abc_shareactionprovider_share_with)
            )
        )
    }
    }
}