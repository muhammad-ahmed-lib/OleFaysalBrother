package ae.oleapp.employee.data.model.common

import ae.oleapp.R
import ae.oleapp.employee.data.model.response.Permission
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateEmployeeData(
    val employeeName : String? = null,
    val employeeEmail : String? = null,
    val employeeDesignation : String? = null,
    val employeeDesignationId : Int? = null,
    val employeeSalary : String? = null,
    val employeeCountryId : Int? = null,
    val employeePhone : String? = null,
    val employeeId : String? = null,
    val employeeLoginId : String? = null,
    val employeeClubId : Int? = null,
    val employeePassword : String? = null,
    val employeeRole : String? = null,
    val employeePermission : String? = null,
    val isEmployeeCredentials : Boolean = false,
    val isEmployeeDetail : Boolean = false,
    val isEmployeePermission : Boolean = false,
    val isEditMode : Boolean = false
): Parcelable

@Parcelize
data class UpdateEmployeeRoleWithPermission(
    val designationId : Int? = null,
    val employeeDesignation : String? = null,
    val employeePermission : List<Permission>? = null,
    val employeeClubId : Int? = null,
    val isEditMode : Boolean = false
): Parcelable

@Parcelize
data class UpdateEmployeeDocument(
    val employeeId : Int? = null,
    val documenId : Int? = null,
    val DocumentType : DocumentType? = null,
    val isEditMode : Boolean = false,
    val isPdfCLick : Boolean = false,
    val front : String? = null,
    val back : String? = null,
    val expiry : String? = null,
    val document : String? = null
): Parcelable

enum class DocumentType(val typeName: String , val imageResId: Int) {
    ID_CARD("ID" , R.drawable.id_ic),
    PASSPORT("Passport" , R.drawable.passport_ic),
    VISA("Visa", R.drawable.visa_ic),
    CONTRACT("Contract", R.drawable.ic_contract),
    DRIVING_LICENSE("Driving" , R.drawable.ic_driving),
    VEHICLE_REGISTRATION("Vehicle" , R.drawable.ic_contract),
    PICTURE("Picture" , R.drawable.picture_ic)

}