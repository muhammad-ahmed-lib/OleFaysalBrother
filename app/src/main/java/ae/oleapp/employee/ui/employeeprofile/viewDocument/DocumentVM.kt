package ae.oleapp.employee.ui.employeeprofile.viewDocument

import ae.oleapp.employee.data.repo.EmployeeRepo
import ae.oleapp.employee.utils.CustomToast
import ae.oleapp.employee.utils.DataState
import ae.oleapp.employee.utils.getBitmapAndFileFromUri
import ae.oleapp.employee.utils.getFileFromUri
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

class DocumentVM (private val employeeRepo: EmployeeRepo,
    private val context: Context): ViewModel() {

    var employeeId = ""
    var documentId = 0
    var documentName = ""
    var documentType = ""

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    val _isDocumentDelete = MutableStateFlow(false)


    private val _frontImageUrl = MutableStateFlow<String?>(null)
    val frontImageUrl = _frontImageUrl.asStateFlow()

    private val _backImageUrl = MutableStateFlow<String?>(null)
    val backImageUrl = _backImageUrl.asStateFlow()

    private val _employeeFrontImageUri = MutableStateFlow<Uri?>(null)
    val employeeFrontImageUri = _employeeFrontImageUri.asStateFlow()

    private val _employeeBackImageUri = MutableStateFlow<Uri?>(null)
    val employeeBackImageUri = _employeeBackImageUri.asStateFlow()

    private val _employeePdfImageUri = MutableStateFlow<Uri?>(null)
    val employeePdfImageUri = _employeePdfImageUri.asStateFlow()

    private val _employeePdfUrl = MutableStateFlow<String?>(null)
    val employeePdfImageUrl = _employeePdfUrl.asStateFlow()

    private val _expiryDate = MutableStateFlow<String?>(null)
    val expiryDate = _expiryDate.asStateFlow()

    private val _isPdfClick = MutableStateFlow(true)
    val isPdfClick : StateFlow<Boolean> = _isPdfClick.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode : StateFlow<Boolean> = _isEditMode.asStateFlow()

    fun setIsPdf(value: Boolean) {
        _isPdfClick.value = value
    }


    fun setIsEditMode(value: Boolean) {
        _isEditMode.value = value
    }

    fun setPdfImageUri(value: Uri) {
        _employeePdfImageUri.value = value
    }

    fun setPdfUrl(value: String) {
        _employeePdfUrl.value = value
    }

    fun setFrontImageUri(value: Uri) {
        _employeeFrontImageUri.value = value
    }

    fun setFrontImageUrl(value: String) {
        _frontImageUrl.value = value
    }

    fun setBackImageUrl(value: String) {
        _backImageUrl.value = value
    }

    fun setBackImageUri(value: Uri) {
        _employeeBackImageUri.value = value
    }

    fun setExpiryDate(value: String) {
        _expiryDate.value = value
    }


    fun validateDocuments(){
        when {
            employeeId.isEmpty() -> {
                CustomToast.makeFancyToast("Please select employee" , CustomToast.INFO)
            }
            documentName.isEmpty() -> {
                CustomToast.makeFancyToast("Please select document", CustomToast.INFO)
            }
            documentType.isEmpty() -> {
                CustomToast.makeFancyToast("Please select document type", CustomToast.INFO)
            }

            !isPdfClick.value && _employeeFrontImageUri.value == null -> {
                CustomToast.makeFancyToast("Please select front image" , CustomToast.INFO)
            }
            !isPdfClick.value && _employeeBackImageUri.value == null -> {
                CustomToast.makeFancyToast("Please select back image" , CustomToast.INFO)
            }

            isPdfClick.value && _employeePdfImageUri.value == null -> {
                CustomToast.makeFancyToast("Please select  Pdf" , CustomToast.INFO)
            }

            expiryDate.value == null -> {
                CustomToast.makeFancyToast("Please select expiry date" , CustomToast.INFO)
            }
            else -> {
                uploadDocuments()
            }
        }
    }

    private fun uploadDocuments(){
        viewModelScope.launch(Dispatchers.IO) {
            val front = employeeFrontImageUri.value?.let {
                val result = getBitmapAndFileFromUri(context, it)
                val mimeType = when (result.second.extension.lowercase()) {
                    "jpg", "jpeg" -> "image/jpeg"
                    "png" -> "image/png"
                    else -> "image/jpeg"
                }
                val requestFile = result.second.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("front", result.second.name, requestFile)
            }
//
            val back = employeeBackImageUri.value?.let {
                val result = getBitmapAndFileFromUri(context, it)
                val mimeType = when (result.second.extension.lowercase()) {
                    "jpg", "jpeg" -> "image/jpeg"
                    "png" -> "image/png"
                    else -> "image/jpeg"
                }
                val requestFile = result.second.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("back", result.second.name, requestFile)
            }
            val pdf = _employeePdfImageUri.value?.let {
                val result = getFileFromUri(context, it)
                val mimeType = "application/pdf"
                val requestFile = result.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("document", result.name, requestFile)
            }


            employeeRepo.uploadEmployeeDocument(
                employeeId = employeeId.toRequestBody("text/plain".toMediaTypeOrNull()),
                name = documentName.toRequestBody("text/plain".toMediaTypeOrNull()),
                type = documentType.toRequestBody("text/plain".toMediaTypeOrNull()),
                front = front ?: MultipartBody.Part.createFormData("front", ""),
                back = back ?: MultipartBody.Part.createFormData("back", ""),
                document = pdf ?: MultipartBody.Part.createFormData("document", ""),
                expiryDate = (expiryDate.value?:"").toRequestBody("text/plain".toMediaTypeOrNull()),
            ).collect{ response ->
                when(response){
                    is DataState.Error -> {
                        _isLoading.tryEmit(false)
                        CustomToast.makeFancyToast(response.errorMessage , CustomToast.ERROR)
                    }
                    DataState.Loading -> {
                        _isLoading.tryEmit(true)
                    }
                    is DataState.Success -> {
                        _isLoading.tryEmit(false)
                        resetData()
                        CustomToast.makeFancyToast("Success" , CustomToast.SUCCESS)
                    }
                }
            }
        }
    }

    fun deleteDocuments(){
        viewModelScope.launch(Dispatchers.IO) {
            employeeRepo.deleteEmployeeDocument(
                documentId = documentId,
                employeeId = employeeId.toInt()

            ).collect{ response ->
                when(response){
                    is DataState.Error -> {
                        _isLoading.tryEmit(false)
                        CustomToast.makeFancyToast(response.errorMessage , CustomToast.ERROR)
                    }
                    DataState.Loading -> {
                        _isLoading.tryEmit(true)
                    }
                    is DataState.Success -> {
                        _isLoading.tryEmit(false)
                        _isDocumentDelete.tryEmit(true)
                        CustomToast.makeFancyToast("Success" , CustomToast.SUCCESS)
                    }
                }
            }
        }
    }



    private fun resetData(){
        _employeeFrontImageUri.value = null
        _employeeBackImageUri.value = null
        _expiryDate.value = null
        _employeePdfImageUri.value = null
    }

}