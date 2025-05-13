package ae.oleapp.employee.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetDesignationDataResponse(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("club_id")
    val clubId: Int? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,


    @SerializedName("permission")
    val permission: List<Permission>? = null,

    @SerializedName("designation_permissions")
    val designationPermissions: List<DesignationPermissions>? = null
)
@Parcelize
data class Permission(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("name")
    val name: String?
): Parcelable

data class DesignationPermissions(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("designation_id")
    val designationId: Int?,

    @SerializedName("permission_id")
    val permissionId: Int?,
)