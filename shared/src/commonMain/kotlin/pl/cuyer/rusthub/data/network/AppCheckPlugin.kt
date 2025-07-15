package pl.cuyer.rusthub.data.network

import io.ktor.client.plugins.api.createClientPlugin
import pl.cuyer.rusthub.util.AppCheckTokenProvider

const val APP_CHECK_HEADER = "X-Firebase-AppCheck"

class AppCheckPluginConfig {
    lateinit var provider: AppCheckTokenProvider
}

val AppCheckPlugin = createClientPlugin("AppCheckPlugin", ::AppCheckPluginConfig) {
    val provider = pluginConfig.provider
    onRequest { request, _ ->
        val token = provider.currentToken()
        if (token != null) {
            request.headers.append(APP_CHECK_HEADER, token)
        }
    }
}
