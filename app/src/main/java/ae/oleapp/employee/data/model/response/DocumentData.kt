package ae.oleapp.employee.data.model.response

import com.google.gson.annotations.SerializedName

data class DocumentData(

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("front")
    val front: String? = null,

    @SerializedName("back")
    val back: String? = null,

    @SerializedName("document")
    val document: String? = null,

    @SerializedName("type")
    val type: String? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("expires_at")
    val expiresAt: String? = null,

    @SerializedName("employee_profile_id")
    val employeeProfileId: Int? = null
)