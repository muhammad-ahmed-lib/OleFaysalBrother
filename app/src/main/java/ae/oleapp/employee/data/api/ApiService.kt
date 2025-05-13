package ae.oleapp.employee.data.api

import ae.oleapp.employee.data.model.BaseResponse
import ae.oleapp.employee.data.model.response.AssignedClub
import ae.oleapp.employee.data.model.response.DocumentData
import ae.oleapp.employee.data.model.response.EmployeeResponse
import ae.oleapp.employee.data.model.response.GetAllEmployeeResponse
import ae.oleapp.employee.data.model.response.GetClub
import ae.oleapp.employee.data.model.response.GetDesignationDataResponse
import ae.oleapp.employee.data.model.response.GetEmployeeDetails
import ae.oleapp.employee.data.model.response.GetEmployeeDetails.EmployeeNotes
import ae.oleapp.employee.data.model.response.Permission
import ae.oleapp.employee.data.model.response.ReviewData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService{

    @GET("owner/employees")
    suspend fun getAllEmployees(@Query("club_id") id: Int): BaseResponse<List<GetAllEmployeeResponse>>

    @GET("owner/employees/designation/{clubId}")
    suspend fun getEmployeeDesignations(@Path ("clubId") id: Int): BaseResponse<List<GetDesignationDataResponse>>

    @FormUrlEncoded
    @POST("owner/employees/designation/add")
    suspend fun addEmployeeDesignation(@Field("designation_name") designationName: String,
                                       @Field("permissions") permissions: String,
                                       @Field("club_id") clubId: String): BaseResponse<GetDesignationDataResponse>

    @FormUrlEncoded
    @POST("owner/employees/designation/add")
    suspend fun updateEmployeeDesignation(@Field("designation_id") designationId: Int?,
                                       @Field("designation_name") designationName: String,
                                       @Field("permissions") permissions: String,
                                       @Field("club_id") clubId: String): BaseResponse<GetDesignationDataResponse>

    @GET("owner/club")
    suspend fun getOwnerClubList(@Query("latitude") latitude : Double,
                                 @Query("longitude")  longitude : Double,
                                 @Query("date")  date : String) : BaseResponse<List<GetClub>>

    @GET("general/utils/permissions")
    suspend fun getPermissions() : BaseResponse<List<Permission>>

    @Multipart
    @POST("owner/employees")
    suspend fun addEmployee(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("phone_country_id") phoneCountryId: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("designation_id") designationId: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("clubs") clubs: RequestBody,
        @Part("salary") salary: RequestBody,
        @Part("login_id") loginId: RequestBody,
        @Part("password") password: RequestBody
    ) : BaseResponse<EmployeeResponse>

    @Multipart
    @PUT("owner/employees")
    suspend fun updateEmployee(
        @Part("employee_id") employeeId: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("phone_country_id") phoneCountryId: RequestBody?,
        @Part("phone_number") phoneNumber: RequestBody?,
        @Part("designation_id") designationId: RequestBody?,
        @Part photo: MultipartBody.Part?,
        @Part("clubs") clubs: RequestBody?,
        @Part("salary") salary: RequestBody?,
        @Part("login_id") loginId: RequestBody?,
        @Part("password") password: RequestBody?
    ) : BaseResponse<EmployeeResponse>

    @GET("owner/employees/single")
    suspend fun getEmployeeDetails(@Query("employee_id") employeeId: Int,
                                   @Query("club_id") clubId: Int) : BaseResponse<GetEmployeeDetails>

    @FormUrlEncoded
    @POST("owner/employees/task/add")
    suspend fun addEmployeeNote(@Field("employee_id") employeeId: String,
                                @Field("club_id") clubId: String,
                                @Field("description") description: String,
                                @Field("due_date") dueDate: String): BaseResponse<EmployeeNotes>

    @FormUrlEncoded
    @PUT("owner/employees/{id}/tasks")
    suspend fun updateEmployeeNote(
                                @Path("id") taskId: String,
                                @Field("description") description: String, ): BaseResponse<EmployeeNotes>


    @DELETE("owner/employees/{id}/tasks")
    suspend fun deleteEmployeeNote(@Path("id") taskId: String): BaseResponse<EmployeeNotes>


    @FormUrlEncoded
    @POST("owner/employees/assign-club")
    suspend fun assignClub(@Field("employee_id") employeeId: String,
                                @Field("club_id") clubId: String): BaseResponse<AssignedClub>

    @GET("owner/employees/{employee_id}/reviews")
    suspend fun getEmployeeRatings(@Path("employee_id") employeeId: Int): BaseResponse<List<ReviewData>>

    @Multipart
    @POST("owner/employees/upload-document")
    suspend fun uploadEmployeeDocument(
        @Part("employee_id") employeeId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("type") type: RequestBody,
        @Part front: MultipartBody.Part?,
        @Part back: MultipartBody.Part?,
        @Part document: MultipartBody.Part?,
        @Part("expiry_date") expiryDate: RequestBody
    ): BaseResponse<DocumentData>

    @DELETE("owner/employees/delete-document")
    suspend fun deleteEmployeeDocument(
        @Query("document_id") documentId: Int,
        @Query("employee_id") employeeId: Int
    ): BaseResponse<Any>


}