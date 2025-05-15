package ae.oleapp.employee.ui.addemployee

import ae.oleapp.employee.data.model.response.EmployeeResponse
import ae.oleapp.employee.data.model.response.GetDesignationDataResponse
import ae.oleapp.employee.data.model.response.Permission
import ae.oleapp.employee.data.repo.EmployeeRepo
import ae.oleapp.employee.utils.CustomToast
import ae.oleapp.employee.utils.DataState
import ae.oleapp.employee.utils.getBitmapAndFileFromUri
import ae.oleapp.employee.utils.toRequestBodyOrNull
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class AddEmployeeVM(
    private val employeeRepo: EmployeeRepo,
    private val context: Context
) : ViewModel() {


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    var desingationId: Int? = null

    private val _getEmployeeDesignationState =
        MutableStateFlow<List<GetDesignationDataResponse>>(emptyList())
    val getEmployeeDesignationState: StateFlow<List<GetDesignationDataResponse>> =
        _getEmployeeDesignationState.asStateFlow()

    private val _addEmployeeDesignationState = MutableStateFlow<GetDesignationDataResponse?>(null)
    val addEmployeeDesignationState: StateFlow<GetDesignationDataResponse?> =
        _addEmployeeDesignationState.asStateFlow()

    private val _getPermissionState = MutableStateFlow<List<Permission>>(emptyList())
    val getPermissionState: StateFlow<List<Permission>> = _getPermissionState.asStateFlow()

    private val _addEmployeeState = MutableStateFlow<EmployeeResponse?>(null)
    val addEmployeeState: StateFlow<EmployeeResponse?> = _addEmployeeState.asStateFlow()

    /**------------------------------Add Employee Form Data-------------------------------------------------*/

    private val _employeeName = MutableStateFlow("")
    val employeeName = _employeeName.asStateFlow()

    private val _employeeEmail = MutableStateFlow("")
    val employeeEmail = _employeeEmail.asStateFlow()

    private val _employeeSalary = MutableStateFlow("")
    val employeeSalary = _employeeSalary.asStateFlow()

    private val _getClickedDesignation = MutableStateFlow<GetDesignationDataResponse?>(null)
    val getClickedDesignationState: StateFlow<GetDesignationDataResponse?> =
        _getClickedDesignation.asStateFlow()

    private val _employeeCountryId = MutableStateFlow("")
    val employeeCountryId = _employeeCountryId.asStateFlow()

    private val _employeePhone = MutableStateFlow("")
    val employeePhone = _employeePhone.asStateFlow()

    private val _employeeId = MutableStateFlow("")
    val employeeId = _employeeId.asStateFlow()

    private val _employeeLoginId = MutableStateFlow("")
    val employeeLoginId = _employeeId.asStateFlow()

    private val _employeePassword = MutableStateFlow("")
    val employeePassword = _employeePassword.asStateFlow()

    private val _employeeImageUri = MutableStateFlow<Uri?>(null)
    val employeeImageUri = _employeeImageUri.asStateFlow()

    private val _isEmployeeAdded = MutableStateFlow(false)
    val isEmployeeAdded: StateFlow<Boolean> = _isEmployeeAdded.asStateFlow()

    private val _clubId = MutableStateFlow<Int?>(null)
    val clubId: StateFlow<Int?> = _clubId.asStateFlow()

    private val _isEmployeeCredentialUpdate = MutableStateFlow(false)
    val isEmployeeCredentialUpdate: StateFlow<Boolean> = _isEmployeeCredentialUpdate.asStateFlow()

    private val _isEmployeeDetailUpdate = MutableStateFlow(false)
    val isEmployeeDetailUpdate: StateFlow<Boolean> = _isEmployeeDetailUpdate.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()


    fun setIsEditMode(value: Boolean) {
        _isEditMode.value = value
    }

    fun setIsEmployeeCredentialUpdate(value: Boolean) {
        _isEmployeeCredentialUpdate.value = value
    }

    fun setIsEmployeeDetailUpdate(value: Boolean) {
        _isEmployeeDetailUpdate.value = value
    }


    fun setClubId(value: Int) {
        _clubId.value = value
    }

    fun setEmail(value: String) {
        _employeeEmail.value = value
    }

    fun setName(value: String) {
        _employeeName.value = value
    }

    fun setSalary(value: String) {
        _employeeSalary.value = value
    }

    fun setCountryId(value: String) {
        _employeeCountryId.value = value
    }

    fun setPhone(value: String) {
        _employeePhone.value = value
    }

    fun setEmployeeId(value: String) {
        _employeeId.value = value
    }

    fun setEmployeeLoginId(value: String) {
        _employeeLoginId.value = value
    }

    fun setPassword(value: String) {
        _employeePassword.value = value
    }

    fun setImageUri(value: Uri) {
        _employeeImageUri.value = value
    }


    fun validateEmployee() {
// Edit mode: skip validation and directly call API
        if (_isEditMode.value) {
            updateEmployeeApi()
            return
        }

        when {
            _employeeName.value.isEmpty() -> {
                CustomToast.makeFancyToast("Name is required", CustomToast.ERROR)
            }

            _employeeEmail.value.isEmpty() -> {
                CustomToast.makeToast("Email is required")
            }

            !_employeeEmail.value.contains("@") -> {
                CustomToast.makeToast("Enter a valid email")
            }

            _getClickedDesignation.value?.name.isNullOrEmpty() -> {
                CustomToast.makeToast("Designation is required")
            }

            _employeeCountryId.value.isEmpty() -> {
                CustomToast.makeToast("Country is required")
            }

            _employeePhone.value.isEmpty() -> {
                CustomToast.makeToast("Phone number is required")
            }

            _employeePhone.value.length != 5 -> {
                CustomToast.makeToast("Enter a valid 5-digit phone number")
            }

            _employeeSalary.value.isEmpty() -> {
                CustomToast.makeToast("Salary is required")
            }

            _employeeLoginId.value.isEmpty() -> {
                CustomToast.makeToast("Employee Login ID is required")
            }

            clubId.value == null -> {
                CustomToast.makeToast("Club ID is required")
            }

            _employeePassword.value.isEmpty() -> {
                CustomToast.makeToast("Password is required")
            }

            _employeePassword.value.length < 6 -> {
                CustomToast.makeToast("Password must be at least 6 characters")
            }

            _employeeImageUri.value.toString().isEmpty() -> {
                CustomToast.makeFancyToast("Select Profile image", CustomToast.ERROR)
            }

            else -> {

                addEmployeeApi()
                CustomToast.makeToast("All fields are valid, submitting...")
            }
        }
    }

    fun addEmployeeApi() {
        viewModelScope.launch {
            val result = employeeImageUri.value?.let {
                val result = getBitmapAndFileFromUri(context, it)
                val mimeType = when (result.second.extension.lowercase()) {
                    "jpg", "jpeg" -> "image/jpeg"
                    "png" -> "image/png"
                    else -> "image/jpeg"
                }
                val requestFile = result.second.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photo", result.second.name, requestFile)
            }

            employeeRepo.addEmployee(

                name = _employeeName.value.toRequestBody("text/plain".toMediaTypeOrNull()),
                email = _employeeEmail.value.toRequestBody("text/plain".toMediaTypeOrNull()),
                phoneCountryId = _employeeCountryId.value.toRequestBody("text/plain".toMediaTypeOrNull()),
                phoneNumber = _employeePhone.value.toRequestBody("text/plain".toMediaTypeOrNull()),
                designationId = _getClickedDesignation.value?.id.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull()),
                photo = result ?: MultipartBody.Part.createFormData("photo", ""),
                clubs = clubId.value.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                salary = _employeeSalary.value.toRequestBody("text/plain".toMediaTypeOrNull()),
                loginId = _employeeLoginId.value.toRequestBody("text/plain".toMediaTypeOrNull()),
                password = _employeePassword.value.toRequestBody("text/plain".toMediaTypeOrNull())

            ).collect { response ->

                when (response) {
                    is DataState.Error -> {
                        _isLoading.value = false
                        CustomToast.makeFancyToast(response.errorMessage, CustomToast.ERROR)
                    }

                    DataState.Loading -> {
                        _isLoading.value = true
                        Log.d("sjdkhafj", "addEmployeeApi: Loading}")
                    }

                    is DataState.Success -> {
                        _isLoading.value = false
                        _isEmployeeAdded.tryEmit(true)
                        _addEmployeeState.tryEmit(response.data.data)
                    }
                }
            }
        }
    }

    fun updateEmployeeApi() {
        viewModelScope.launch {
//            val result = employeeImageUri.value?.let {
//                val result = getBitmapAndFileFromUri(context, it)
//                val mimeType = when (result.second.extension.lowercase()) {
//                    "jpg", "jpeg" -> "image/jpeg"
//                    "png" -> "image/png"
//                    else -> "image/jpeg"
//                }
//                val requestFile = result.second.asRequestBody(mimeType.toMediaTypeOrNull())
//                MultipartBody.Part.createFormData("photo", result.second.name, requestFile)
//            }

            employeeRepo.updateEmployee(
                employeeId = _employeeId.value.toRequestBodyOrNull(),
                name = _employeeName.value.toRequestBodyOrNull(),
                email = _employeeEmail.value.toRequestBodyOrNull(),
                phoneCountryId = _employeeCountryId.value.toRequestBodyOrNull(),
                phoneNumber = _employeePhone.value.toRequestBodyOrNull(),
                designationId = _getClickedDesignation.value?.id.toString().toRequestBodyOrNull(),
//                photo = result ?: MultipartBody.Part.createFormData("photo", ""),
                clubs = clubId.value.toString().toRequestBodyOrNull(),
                salary = _employeeSalary.value.toRequestBodyOrNull(),
                loginId = _employeeLoginId.value.toRequestBodyOrNull(),
                password = _employeePassword.value.toRequestBodyOrNull()

            ).collect { response ->

                when (response) {
                    is DataState.Error -> {
                        _isLoading.value = false
                        CustomToast.makeFancyToast(response.errorMessage, CustomToast.ERROR)
                    }

                    DataState.Loading -> {
                        _isLoading.value = true
                        Log.d("sjdkhafj", "addEmployeeApi: Loading}")
                    }

                    is DataState.Success -> {
                        _isLoading.value = false
                        _isEmployeeAdded.tryEmit(true)
                        _addEmployeeState.tryEmit(response.data.data)
                    }
                }
            }
        }
    }


    fun getDesignations(clubId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            employeeRepo.getEmployeeDesignations(clubId).collect { response ->
                when (response) {
                    is DataState.Error -> {
                        _isLoading.value = false
                        Log.d("asjfhajk", "fetchEmployees: ${response.errorMessage}")
                    }

                    DataState.Loading -> {
                        _isLoading.value = true
                        Log.d("asjfhajk", "fetchEmployees: Loading")
                    }

                    is DataState.Success -> {
                        _isLoading.value = false
                        _getEmployeeDesignationState.tryEmit(response.data.data)
                    }
                }
            }
        }
    }

    fun addDesignation(designationName: String, permissions: String, clubId: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            desingationId?.let {
                employeeRepo.updateEmployeeDesignation(
                    it,
                    designationName,
                    permissions,
                    clubId.toString()
                )
                    .collect { response ->
                        when (response) {
                            is DataState.Error -> {
                                _isLoading.value = false
                                CustomToast.makeToast(response.errorMessage)
                            }

                            DataState.Loading -> {
                                _isLoading.value = true
                            }

                            is DataState.Success -> {
                                _isLoading.value = false
                                _addEmployeeDesignationState.tryEmit(response.data.data)
                            }
                        }
                    }
            } ?: run {
                employeeRepo.addEmployeeDesignation(designationName, permissions, clubId.toString())
                    .collect { response ->
                        when (response) {
                            is DataState.Error -> {
                                _isLoading.value = false
                                CustomToast.makeToast(response.errorMessage)
                            }

                            DataState.Loading -> {
                                _isLoading.value = true
                            }

                            is DataState.Success -> {
                                _isLoading.value = false
                                _addEmployeeDesignationState.tryEmit(response.data.data)
                            }
                        }
                    }
            }
        }
    }

    fun setClickedDesignation(designation: GetDesignationDataResponse) {
        _getClickedDesignation.value = designation
    }

    fun getPermissions() {
        viewModelScope.launch(Dispatchers.IO) {
            employeeRepo.getPermissions().collect { response ->
                when (response) {
                    is DataState.Error -> {
                        _isLoading.value = false
                        CustomToast.makeToast(response.errorMessage)
                    }

                    DataState.Loading -> {
                        _isLoading.value = true
                    }

                    is DataState.Success -> {
                        _isLoading.value = false
                        _getPermissionState.tryEmit(response.data.data)
                    }
                }
            }
        }
    }


    fun resetData() {
        _employeeName.value = ""
        _employeeEmail.value = ""
        _employeeSalary.value = ""
        _getClickedDesignation.value = null
        _employeeCountryId.value = ""
        _employeePhone.value = ""
        _employeeId.value = ""
        _employeePassword.value = ""
        _employeeImageUri.value = Uri.EMPTY
        _isEmployeeCredentialUpdate.value = false
        _isEmployeeDetailUpdate.value = false
        _isEditMode.value = false

    }


}