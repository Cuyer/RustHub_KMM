package pl.cuyer.rusthub.data.network

import io.ktor.client.plugins.api.createClientPlugin
import pl.cuyer.rusthub.util.CrashReporter
import pl.cuyer.rusthub.data.network.APP_CHECK_HEADER

val CrashReportingPlugin = createClientPlugin("CrashReportingPlugin") {
    onRequest { request, _ ->
        val message = buildString {
            appendLine("Request ${request.method.value} ${request.url}")
            appendLine("Headers:")
            request.headers.entries().forEach { (key, values) ->
                values.forEach { value ->
                    appendLine("$key: $value")
                }
            }
            val bodyText = request.body.toString()
            if (bodyText.isNotBlank()) {
                appendLine("Body: $bodyText")
            }
        }
        CrashReporter.log(message)
    }
}