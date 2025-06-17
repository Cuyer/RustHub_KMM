package pl.cuyer.rusthub.data.network.util

import io.ktor.http.URLBuilder

fun <T> URLBuilder.appendNonNull(parameter: Pair<String, T?>) =
    parameter.second?.let { parameters.append(parameter.first, it.toString()) }