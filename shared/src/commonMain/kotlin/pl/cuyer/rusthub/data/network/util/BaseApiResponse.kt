package pl.cuyer.rusthub.data.network.util

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.model.ErrorResponse
import kotlin.coroutines.coroutineContext

abstract class BaseApiResponse(
    val json: Json
) {

    inline fun <reified T> safeApiCall(crossinline apiCall: suspend () -> HttpResponse): Flow<Result<T>> =
        flow {
            coroutineContext.ensureActive()

            val response = apiCall()
            if (response.status.isSuccess()) {
                val data: T = response.body()
                emit(success(data))
            } else {
                val errorResponse = json.decodeFromString<ErrorResponse>(response.bodyAsText())
                emit(Result.Error(Exception(errorResponse.message)))
            }
        }.onStart {
            emit(loading())
        }.catch { e ->
            emit(error(e))
        }

    fun loading(): Result.Loading = Result.Loading
    fun <T> success(success: T): Result.Success<T> = Result.Success(success)
    fun <T> error(exception: Throwable): Result<T> =
        Result.Error(exception = exception)
}