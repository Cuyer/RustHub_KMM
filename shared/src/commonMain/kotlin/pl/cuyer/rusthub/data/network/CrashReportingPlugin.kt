package pl.cuyer.rusthub.data.network

import io.ktor.client.plugins.api.createClientPlugin
import pl.cuyer.rusthub.util.CrashReporter
import pl.cuyer.rusthub.data.network.APP_CHECK_HEADER

private val sensitiveHeaders = setOf("Authorization", APP_CHECK_HEADER)

private fun isSensitive(header: String): Boolean {
    val key = header.lowercase()
    return sensitiveHeaders.any { it.lowercase() == key } ||
        key.contains("secret") ||
        key.contains("token")
}

private fun mask(value: String): String {
    return if (value.length <= 4) "****" else "*".repeat(value.length - 4) + value.takeLast(4)
}

val CrashReportingPlugin = createClientPlugin("CrashReportingPlugin") {
    onRequest { request, _ ->
        val message = buildString {
            appendLine("Request ${request.method.value} ${request.url}")
            appendLine("Headers:")
            request.headers.entries().forEach { (key, values) ->
                values.forEach { value ->
                    val loggedValue = if (isSensitive(key)) mask(value) else value
                    appendLine("$key: $loggedValue")
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
