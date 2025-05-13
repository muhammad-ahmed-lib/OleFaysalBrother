package ae.oleapp.employee.data.repo


import ae.oleapp.employee.data.api.ApiService
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
import ae.oleapp.employee.utils.safeApiCall
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class EmployeeRepoImp (private val partnerApiService: ApiService , private val ownerApiClient : ApiService) : EmployeeRepo {

    override suspend fun getAllEmployees(clubId: Int): Flow<DataState<BaseResponse<List<GetAllEmployeeResponse>>>> = safeApiCall {
        partnerApiService.getAllEmployees(clubId)
    }

    override suspend fun getEmployeeDesignations(clubId: Int): Flow<DataState<BaseResponse<List<GetDesignationDataResponse>>>>  = safeApiCall {
        partnerApiService.getEmployeeDesignations(clubId)
    }

    override suspend fun addEmployeeDesignation(
        designationName: String,
        permissions: String,
        clubId: String
    ): Flow<DataState<BaseResponse<GetDesignationDataResponse>>> = safeApiCall {
        partnerApiService.addEmployeeDesignation(designationName,permissions,clubId)
    }

    override suspend fun updateEmployeeDesignation(
        designationId: Int?,
        designationName: String,
        permissions: String,
        clubId: String
    ): Flow<DataState<BaseResponse<GetDesignationDataResponse>>>  = safeApiCall{
        partnerApiService.updateEmployeeDesignation(designationId,designationName,permissions,clubId)
    }

    override suspend fun getOwnerClubList(
        latitude: Double,
        longitude: Double,
        date: String
    ): Flow<DataState<BaseResponse<List<GetClub>>>> = safeApiCall {
        ownerApiClient.getOwnerClubList(latitude,longitude,date)

    }

    override suspend fun getPermissions(): Flow<DataState<BaseResponse<List<Permission>>>> = safeApiCall {
        ownerApiClient.getPermissions()

    }

    override suspend fun addEmployee(
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
    ): Flow<DataState<BaseResponse<EmployeeResponse>>> = safeApiCall {
        partnerApiService.addEmployee(name,email,phoneCountryId,phoneNumber,designationId,photo,clubs,salary,loginId,password)

    }

    override suspend fun updateEmployeeNote(
        taskId: String,
        description: String
    ): Flow<DataState<BaseResponse<EmployeeNotes>>> = safeApiCall {
        partnerApiService.updateEmployeeNote(taskId , description)
    }

    override suspend fun deleteEmployeeNote(taskId: String): Flow<DataState<BaseResponse<EmployeeNotes>>> = safeApiCall {
        partnerApiService.deleteEmployeeNote(taskId)
    }

    override suspend fun updateEmployee(
        employeeId: RequestBody?,
        name: RequestBody?,
        email: RequestBody?,
        phoneCountryId: RequestBody?,
        phoneNumber: RequestBody?,
        designationId: RequestBody?,
        photo: MultipartBody.Part?,
        clubs: RequestBody?,
        salary: RequestBody?,
        loginId: RequestBody?,
        password: RequestBody?
    ): Flow<DataState<BaseResponse<EmployeeResponse>>> = safeApiCall {
        partnerApiService.updateEmployee(employeeId,name,email,phoneCountryId,phoneNumber,designationId,photo,clubs,salary,loginId,password)
    }


    override suspend fun getEmployeeDetails(
        employeeId: Int,
        clubId: Int
    ): Flow<DataState<BaseResponse<GetEmployeeDetails>>> = safeApiCall {
        partnerApiService.getEmployeeDetails(employeeId,clubId)
    }

    override suspend fun addEmployeeNote(
        employeeId: String,
        clubId: String,
        description: String,
        dueDate: String
    ): Flow<DataState<BaseResponse<EmployeeNotes>>> = safeApiCall {
        partnerApiService.addEmployeeNote(employeeId,clubId,description,dueDate)
    }

    override suspend fun assignClub(
        employeeId: String,
        clubId: String
    ): Flow<DataState<BaseResponse<AssignedClub>>> = safeApiCall {
        partnerApiService.assignClub(employeeId,clubId)
    }

    override suspend fun getEmployeeRatings(employeeId: Int): Flow<DataState<BaseResponse<List<ReviewData>>>> = safeApiCall {
        partnerApiService.getEmployeeRatings(employeeId)
    }

    override suspend fun uploadEmployeeDocument(
        employeeId: RequestBody,
        name: RequestBody,
        type: RequestBody,
        front: MultipartBody.Part?,
        back: MultipartBody.Part?,
        document: MultipartBody.Part?,
        expiryDate: RequestBody
    ): Flow<DataState<BaseResponse<DocumentData>>> = safeApiCall {
        partnerApiService.uploadEmployeeDocument(employeeId,name,type,front,back,document,expiryDate)
    }

    override suspend fun deleteEmployeeDocument(
        documentId: Int,
        employeeId: Int
    ): Flow<DataState<BaseResponse<Any>>> = safeApiCall {
        partnerApiService.deleteEmployeeDocument(documentId,employeeId)
    }

}