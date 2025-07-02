package pl.cuyer.rusthub.util

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

private val json = Json { ignoreUnknownKeys = true }

@OptIn(ExperimentalEncodingApi::class)
fun anonymousAccountExpiresInDays(token: String): Int? {
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
        val days = (expiration - now).inWholeDays
        if (days >= 0) days.toInt() else null
    } catch (_: Exception) {
        null
    }
}
