package ae.oleapp.abstraction.repository

import ae.oleapp.abstraction.api_client.ApiClient
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.PlayersCountResponse
import ae.oleapp.abstraction.models.Sale
import ae.oleapp.abstraction.models.SaleResponse
import ae.oleapp.abstraction.models.SmsData
import ae.oleapp.abstraction.models.SmsHomeResponse
import ae.oleapp.abstraction.models.SmsPurchaseRequest
import ae.oleapp.abstraction.models.SmsPurchaseResponse
import ae.oleapp.abstraction.models.SmsRequest
import ae.oleapp.abstraction.models.SmsResponse
import ae.oleapp.abstraction.models.SmsUsageResponse
import ae.oleapp.abstraction.models.UpdateSmsRequest
import ae.oleapp.utils.TinyDB
import android.content.Context
import android.util.Log
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmsRepository(private val context: Context) {

    private val TAG="SmsRepositoryInfo"
    private val accessToken = TinyDB(context).getToken().also {
        Log.d("InventoryRepository", "Access Token: $it")
    }
    suspend fun sendSmsRequest(request: SmsRequest): ApiResponse<SmsResponse> {
        return try {
            val token = TinyDB(context).getToken() ?: run {
                Log.e(TAG, "No token available")
                return ApiResponse.Error("Authentication required")
            }

            Log.d(TAG, "Formatted request: ${request}")

            val response = ApiClient.ownerApiService.sendSmsRequest(
                "Bearer $token",
                request
            )

            when {
                response.isSuccessful -> {
                    response.body()?.let {
                        when (it.status) {
                            200 -> ApiResponse.Success(it)
                            400 -> ApiResponse.Error(it.message ?: "Duplicate request")
                            else -> ApiResponse.Error("Unexpected status: ${it.status}")
                        }
                    } ?: ApiResponse.Error("Empty response body")
                }
                else -> {
                    try {
                        // Try to parse error response body
                        val errorBody = response.errorBody()?.string()
                        Log.e(TAG, "API Error ${response.code()}: $errorBody")
                        ApiResponse.Error("Server error: ${response.code()} - $errorBody")
                    } catch (e: Exception) {
                        ApiResponse.Error("Failed to parse error: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network error", e)
            ApiResponse.Error("Network error: ${e.message}")
        }
    }

    private fun convertTo24Hour(time12h: String): String {
        return try {
            val inFormat = SimpleDateFormat("hh:mma", Locale.US)
            val outFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
            outFormat.format(inFormat.parse(time12h) ?: Date())
        } catch (e: Exception) {
            "00:00:00" // Fallback
        }
    }

    suspend fun updateSmsRequest(accessToken: String, request: UpdateSmsRequest): ApiResponse<SmsResponse> {
        return try {
            val response = ApiClient.ownerApiService.updateSmsRequest("Bearer $accessToken", request)
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResponse.Success(it)
                } ?: ApiResponse.Error("Empty response body")
            } else {
                ApiResponse.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Network error")
        }
    }

    suspend fun purchaseSms(request: SmsPurchaseRequest): ApiResponse<SmsPurchaseResponse> {
        return try {
            val response = ApiClient.ownerApiService.purchaseSms("Bearer $accessToken", request)
            if (response.isSuccessful) {
                response.body()?.let {
                    ApiResponse.Success(it)
                } ?: ApiResponse.Error("Empty response body")
            } else {
                ApiResponse.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Network error")
        }
    }

    fun fetchHomeSms(clubId: Int = 1, callback: (Result<SmsHomeResponse>) -> Unit) {
        val call = ApiClient.ownerApiService.getSmsHome("Bearer $accessToken", clubId)

        call.enqueue(object : retrofit2.Callback<SmsHomeResponse> {
            override fun onResponse(
                call: Call<SmsHomeResponse>,
                response: retrofit2.Response<SmsHomeResponse>
            ) {
                if (response.isSuccessful) {
                    val sale = response.body()
                    if (sale != null) {
                        callback(Result.success(sale))
                    } else {
                        callback(Result.failure(Throwable("Sale data is null")))
                    }
                } else {
                    callback(Result.failure(Throwable("Error: ${response.code()} ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<SmsHomeResponse>, t: Throwable) {
                Log.e("InventoryRepository", "Network request failed: ${t.message}")
                callback(Result.failure(t))
            }
        })
    }

    fun fetchPlayersCount(clubId: Int = 1, callback: (Result<PlayersCountResponse>) -> Unit) {
        val call = ApiClient.ownerApiService.getPlayersCount("Bearer $accessToken", clubId)

        call.enqueue(object : retrofit2.Callback<PlayersCountResponse> {
            override fun onResponse(
                call: Call<PlayersCountResponse>,
                response: retrofit2.Response<PlayersCountResponse>
            ) {
                if (response.isSuccessful) {
                    val sale = response.body()
                    if (sale != null) {
                        callback(Result.success(sale))
                    } else {
                        callback(Result.failure(Throwable("Sale data is null")))
                    }
                } else {
                    callback(Result.failure(Throwable("Error: ${response.code()} ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<PlayersCountResponse>, t: Throwable) {
                Log.e("InventoryRepository", "Network request failed: ${t.message}")
                callback(Result.failure(t))
            }
        })
    }



    fun fetchSmsHistory(clubId: Int = 1, callback: (Result<SmsUsageResponse>) -> Unit) {
        val call = ApiClient.ownerApiService.getSmsDeliveryHistory("Bearer $accessToken", clubId)

        call.enqueue(object : retrofit2.Callback<SmsUsageResponse> {
            override fun onResponse(
                call: Call<SmsUsageResponse>,
                response: retrofit2.Response<SmsUsageResponse>
            ) {
                if (response.isSuccessful) {
                    val sale = response.body()
                    if (sale != null) {
                        callback(Result.success(sale))
                    } else {
                        callback(Result.failure(Throwable("Sale data is null")))
                    }
                } else {
                    callback(Result.failure(Throwable("Error: ${response.code()} ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<SmsUsageResponse>, t: Throwable) {
                Log.e("InventoryRepository", "Network request failed: ${t.message}")
                callback(Result.failure(t))
            }
        })
    }

}