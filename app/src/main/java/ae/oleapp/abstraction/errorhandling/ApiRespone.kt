package ae.oleapp.abstraction.errorhandling

// ApiResponse.kt
sealed class ApiResponse<T>(val data: T? = null, val error: String? = null) {
    class Success<T>(data: T) : ApiResponse<T>(data)
    class Error<T>(error: String) : ApiResponse<T>(error = error)
    class Loading<T> : ApiResponse<T>()
}

