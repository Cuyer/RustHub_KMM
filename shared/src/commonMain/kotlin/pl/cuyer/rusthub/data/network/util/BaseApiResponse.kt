package pl.cuyer.rusthub.data.network.util

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import pl.cuyer.rusthub.common.Result

abstract class BaseApiResponse {
    inline fun <reified T> safeApiCall(crossinline apiCall: suspend () -> HttpResponse): Flow<Result<T>> =
        flow {
            runCatching {
                val response = apiCall()
                if (response.status.isSuccess()) {
                    val data: T = response.body<T>()
                    emit(success(data))
                } else {
                    val errorResponse: String = response.bodyAsText()
                    emit(
                        error(
                            exception = Exception(errorResponse)
                        )
                    )
                }
            }.onFailure { throwable ->
                emit(error(throwable))
            }
        }.onStart {
            emit(loading())
        }

    fun loading(): Result.Loading = Result.Loading
    fun <T> success(success: T): Result.Success<T> = Result.Success(success)
    fun <T> error(exception: Throwable): Result<T> =
        Result.Error(exception = exception)
}