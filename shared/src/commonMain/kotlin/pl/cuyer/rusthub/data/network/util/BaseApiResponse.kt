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
import pl.cuyer.rusthub.common.Result
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext

abstract class BaseApiResponse {

    inline fun <reified T> safeApiCall(crossinline apiCall: suspend () -> HttpResponse): Flow<Result<T>> =
        flow {
            coroutineContext.ensureActive()

            val response = apiCall()
            if (response.status.isSuccess()) {
                val data: T = response.body()
                emit(Result.Success(data))
            } else {
                val errorResponse = response.bodyAsText()
                emit(Result.Error(Exception(errorResponse)))
            }
        }.onStart {
            emit(Result.Loading)
        }.catch { e ->
            emit(Result.Error(e))
        }

    fun loading(): Result.Loading = Result.Loading
    fun <T> success(success: T): Result.Success<T> = Result.Success(success)
    fun <T> error(exception: Throwable): Result<T> =
        Result.Error(exception = exception)
}