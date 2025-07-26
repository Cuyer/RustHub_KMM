package pl.cuyer.rusthub.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import pl.cuyer.rusthub.SharedRes
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

private val json = Json { ignoreUnknownKeys = true }

@OptIn(ExperimentalEncodingApi::class)
fun anonymousAccountExpiresIn(token: String): Duration? {
    val parts = token.split(".")
    if (parts.size < 2) return null
    val payload = parts[1]
        .replace('-', '+')
        .replace('_', '/')
        .let { if (it.length % 4 != 0) it.padEnd((it.length + 3) / 4 * 4, '=') else it }
    return try {
        val decoded = Base64.decode(payload).decodeToString()
        val exp = json.parseToJsonElement(decoded).jsonObject["exp"]?.jsonPrimitive?.content?.toLong()
            ?: return null
        val expiration = Instant.fromEpochSeconds(exp)
        val now = Clock.System.now()
        val duration = expiration - now
        if (duration.inWholeSeconds > 0) duration else null
    } catch (_: Exception) {
        null
    }
}

fun formatExpiration(duration: Duration, stringProvider: StringProvider): String {
    val days = duration.inWholeDays
    val hours = duration.inWholeHours % 24
    val minutes = duration.inWholeMinutes % 60
    return if (days > 0) {
        "$days${stringProvider.get(SharedRes.strings.days)}"
    } else {
        buildString {
            if (hours > 0) append("${hours}h ")
            append("${minutes}m")
        }
    }
}
