package pl.cuyer.rusthub.data.network

import io.ktor.client.plugins.api.createClientPlugin
import pl.cuyer.rusthub.util.CrashReporter

val CrashReportingPlugin = createClientPlugin("CrashReportingPlugin") {
    onRequest { request, _ ->
        val body = request.body.toString()
        val message = buildString {
            append("Request ")
            append(request.method.value)
            append(' ')
            append(request.url)
            append("\nHeaders: ")
            append(request.headers)
        }
        CrashReporter.log(message)
    }
}
