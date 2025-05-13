package ae.oleapp.employee.data.repo

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
import ae.oleapp.employee.utils.DataState
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface  EmployeeRepo  {

    suspend fun getAllEmployees(
        clubId: Int,
    ): Flow<DataState<BaseResponse<List<GetAllEmployeeResponse>>>>

    suspend fun getEmployeeDesignations(
        clubId: Int,
    ): Flow<DataState<BaseResponse<List<GetDesignationDataResponse>>>>

    suspend fun addEmployeeDesignation(designationName: String,
                                       permissions: String,
                                       clubId: String): Flow<DataState<BaseResponse<GetDesignationDataResponse>>>

    suspend fun updateEmployeeDesignation(designationId: Int?,
                                       designationName: String,
                                       permissions: String,
                                       clubId: String): Flow<DataState<BaseResponse<GetDesignationDataResponse>>>

    suspend fun getOwnerClubList(latitude : Double,
                                 longitude : Double,
                                 date : String): Flow<DataState<BaseResponse<List<GetClub>>>>

    suspend fun getPermissions() : Flow<DataState<BaseResponse<List<Permission>>>>

    suspend fun addEmployee(
        name: RequestBody,
        email: RequestBody,
        phoneCountryId: RequestBody,
         phoneNumber: RequestBody,
        designationId: RequestBody,
        photo: MultipartBody.Part,
        clubs: RequestBody,
        salary: RequestBody,
        loginId: RequestBody,
        password: RequestBody
    ) : Flow<DataState<BaseResponse<EmployeeResponse>>>

    suspend fun updateEmployeeNote( taskId: String, description: String, ): Flow<DataState<BaseResponse<EmployeeNotes>>>

    suspend fun deleteEmployeeNote( taskId: String): Flow<DataState<BaseResponse<EmployeeNotes>>>

    suspend fun updateEmployee(
        employeeId: RequestBody?=null,
        name: RequestBody?=null,
        email: RequestBody?=null,
        phoneCountryId: RequestBody?=null,
        phoneNumber: RequestBody?=null,
        designationId: RequestBody?=null,
        photo: MultipartBody.Part?=null,
        clubs: RequestBody?=null,
        salary: RequestBody?=null,
        loginId: RequestBody?=null,
        password: RequestBody?=null
    ) : Flow<DataState<BaseResponse<EmployeeResponse>>>

    suspend fun getEmployeeDetails(employeeId: Int,
                                  clubId: Int) : Flow<DataState<BaseResponse<GetEmployeeDetails>>>

    suspend fun addEmployeeNote(employeeId: String,
                                clubId: String,
                                description: String,
                                dueDate: String): Flow<DataState<BaseResponse<EmployeeNotes>>>

    suspend fun assignClub(employeeId: String,
                           clubId: String): Flow<DataState<BaseResponse<AssignedClub>>>

    suspend fun getEmployeeRatings(employeeId: Int): Flow<DataState<BaseResponse<List<ReviewData>>>>

    suspend fun uploadEmployeeDocument(
        employeeId: RequestBody,
        name: RequestBody,
        type: RequestBody,
        front: MultipartBody.Part?,
        back: MultipartBody.Part?,
        document: MultipartBody.Part?,
        expiryDate: RequestBody
    ): Flow<DataState<BaseResponse<DocumentData>>>

    suspend fun deleteEmployeeDocument(documentId: Int, employeeId: Int): Flow<DataState<BaseResponse<Any>>>

}