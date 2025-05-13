package ae.oleapp.employee.data.model.response

import com.google.gson.annotations.SerializedName

data class GetAllEmployeeResponse(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("is_active")
    val isActive: Boolean?,

    @SerializedName("country_id")
    val countryId: Int?,

    @SerializedName("country_code")
    val countryCode: String?,

    @SerializedName("number")
    val number: String?,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("picture")
    val picture: String?,

    @SerializedName("currency")
    val currency: String?,

    @SerializedName("assigned_clubs")
    val assignedClubs: AssignedClub?,

    @SerializedName("salary")
    val salary: Int?,

    @SerializedName("login_id")
    val employeeLoginId: String?,

    @SerializedName("designation")
    val designation: GetDesignationDataResponse?,

    @SerializedName("total_ratings")
    val totalRatings: Int?,

    @SerializedName("average_ratings")
    val averageRatings: String?,

    @SerializedName("tip_amount")
    val tipAmount: Int?,

    @SerializedName("added_date")
    val addedDate: String?,

    @SerializedName("employee_of_the_month")
    val employeeOfTheMonth: Boolean?,

    @SerializedName("month")
    val month: String?
)

data class AssignedClub(
    @SerializedName("id") val id: Int?,
    @SerializedName("employee_id") val employeeId: Int?,
    @SerializedName("club_id") val clubId: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("currency") val currency: String?
)
