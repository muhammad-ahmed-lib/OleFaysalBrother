package ae.oleapp.employee.utils

import ae.oleapp.employee.data.model.BaseResponse
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.net.SocketTimeoutException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> BaseResponse<T>,
): Flow<DataState<BaseResponse<T>>> = flow {
    emit(DataState.Loading)
    val apiResponse = apiCall.invoke()
    when(apiResponse.status){
        200 -> emit(DataState.Success(apiResponse))
        else -> emit(DataState.Error(apiResponse.error ?: apiResponse.message ?: "Something went wrong"))
    }
//    emit(DataState.Success())
}.catch { throwable ->
    val errorMessage = when (throwable) {
        is SocketTimeoutException -> "Request timed out. Please try again."
        is IOException -> "Network error. Please check your connection."
        is retrofit2.HttpException -> {
            val errorBody = throwable.response()?.errorBody()?.string()
            parseErrorBody(errorBody)
        }
        else -> "An unexpected error occurred"
    }
    emit(DataState.Error(errorMessage))
}.flowOn(Dispatchers.IO)

/**
 * Function to parse the backend error response and extract meaningful message.
 */

fun parseErrorBody(errorBody: String?): String {
    return try {
        val errorResponse = Gson().fromJson(errorBody, ApiErrorResponse::class.java)
        errorResponse.error ?: errorResponse.message ?: "Something went wrong"
    } catch (e: JsonSyntaxException) {
        "Error parsing server response"
    } catch (e: Exception) {
        "An unknown error occurred"
    }
}

/**
 * Data class to map API error responses.
 */
data class ApiErrorResponse(
    val statusCode: Int?,
    val message: String?,
    val error : String?
)