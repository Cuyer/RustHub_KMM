package pl.cuyer.rusthub.data.network.util

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.model.ErrorResponse
import pl.cuyer.rusthub.data.network.util.ApiExceptionMapper
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
                val body = response.bodyAsText()
                val exception = try {
                    if (body.isNotBlank()) {
                        val errorResponse = json.decodeFromString<ErrorResponse>(body)
                        ApiExceptionMapper.fromErrorResponse(errorResponse)
                    } else {
                        ApiExceptionMapper.fromStatusCode(response.status.value)
                    }
                } catch (e: SerializationException) {
                    ApiExceptionMapper.fromThrowable(e)
                }
                emit(Result.Error(exception))
            }
        }.catch { e ->
            if (e is CancellationException) {
                throw e
            } else {
                emit(error(ApiExceptionMapper.fromThrowable(e)))
            }
        }
    fun <T> success(success: T): Result.Success<T> = Result.Success(success)
    fun <T> error(exception: Throwable): Result<T> =
        Result.Error(exception = exception)

}
