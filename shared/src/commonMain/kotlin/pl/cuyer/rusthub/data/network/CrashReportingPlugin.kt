package pl.cuyer.rusthub.data.network

import io.ktor.client.plugins.api.createClientPlugin
import pl.cuyer.rusthub.util.CrashReporter
import pl.cuyer.rusthub.data.network.APP_CHECK_HEADER

val CrashReportingPlugin = createClientPlugin("CrashReportingPlugin") {
    onRequest { request, _ ->
        val body = request.body.toString()
        val authHeader = request.headers["Authorization"] ?: ""
        val appCheck = request.headers[APP_CHECK_HEADER] ?: ""
        val message = buildString {
            append("Request ")
            append(request.method.value)
            append(' ')
            append(request.url)
            append("\nAuth: ")
            append(authHeader)
            append("\n$APP_CHECK_HEADER: ")
            append(appCheck)
            append("\nBody: ")
            append(body)
        }
        CrashReporter.log(message)
    }
}
