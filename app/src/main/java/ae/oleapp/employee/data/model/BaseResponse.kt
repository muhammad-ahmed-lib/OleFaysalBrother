package ae.oleapp.employee.data.model

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("status")
    val status: Int?,

    @SerializedName("message")
    val message: String?,

    @SerializedName("error")
    val error: String?,

    @SerializedName("data")
    val data: T
)