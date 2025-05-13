package ae.oleapp.employee.data.model.response

import com.google.gson.annotations.SerializedName

data class GetEmployeeDetails (
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("is_active") val isActive: Boolean? = null,
    @SerializedName("country_id") val countryId: Int? = null,
    @SerializedName("country_code") val countryCode: String? = null,
    @SerializedName("number") val number: String? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("picture") val picture: String? = null,
    @SerializedName("currency") val currency: String? = null,
    @SerializedName("assigned_clubs") val assignedClubs: AssignedClubs? = null,
    @SerializedName("salary") val salary: Int? = null,
    @SerializedName("login_id") val employeeLoginId: String? = null,
    @SerializedName("designation") val designation: GetDesignationDataResponse? = null,
//    @SerializedName("designation_id") val designationId: Int? = null,
    @SerializedName("total_ratings") val totalRatings: Int? = null,
    @SerializedName("average_ratings") val averageRatings: String? = null,
    @SerializedName("tip_amount") val tipAmount: Int? = null,
    @SerializedName("added_date") val addedDate: String? = null,
    @SerializedName("salary_month") val salaryMonth: String? = null,
    @SerializedName("salary_paid") val salaryPaid: Boolean? = null,
    @SerializedName("documents") val documents: List<DocumentData>? = null,
    @SerializedName("booking_data") val bookingData: List<BookingsData>? = null,
    @SerializedName("tips_data") val tipsData: TipsData? = null,
    @SerializedName("salary_data") val salaryData: List<SalaryData>? = null,
    @SerializedName("tasks") val tasks: List<EmployeeNotes>? = null
){

    fun hasTasks(): Boolean {
        return !tasks.isNullOrEmpty()
    }

    data class AssignedClubs(
        @SerializedName("id") val id: Int? = null,
        @SerializedName("name") val name: String? = null,
        @SerializedName("country") val country: String? = null,
        @SerializedName("currency") val currency: String? = null
    )

    data class TipsData(
        @SerializedName("payable_tips") val payableTips: Int? = null,
        @SerializedName("current_month_tips") val currentMonthTips: Int? = null,
        @SerializedName("last_week_tips") val lastWeekTips: Int? = null,
        @SerializedName("lifetime_tips") val lifetimeTips: Int? = null
    )

    data class BookingsData(
        @SerializedName("detail") val detail: String? = null,
        @SerializedName("count") val count: Int? = null,
        @SerializedName("status") val status: String? = null,
    )
    data class EmployeeNotes(

    @SerializedName("id") val id: Int? = null,
        @SerializedName("title") val title: String? = null,
        @SerializedName("description") val description: String? = null,
        @SerializedName("status") val status: String? = null,
        @SerializedName("due_date") val dueDate: String? = null,
        @SerializedName("created_at") val createdAt: String? = null,
        @SerializedName("employee_club_id") val employeeClubId: Int? = null,
        @SerializedName("employee_profile_id") val employeeProfileId: Int? = null,
    )
    data class SalaryData(
        @SerializedName("id") val id: Int? = null,
        @SerializedName("amount") val amount: Int? = null,
        @SerializedName("deduction") val deduction: Int? = null,
        @SerializedName("bonus") val bonus: Int? = null,
        @SerializedName("tip_amount") val tipAmount: Int? = null,
        @SerializedName("date") val date: String? = null,
        @SerializedName("salary_month") val salaryMonth: String? = null,
        @SerializedName("paid_at") val paidAt: String? = null,
    )
}