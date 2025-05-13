package ae.oleapp.employee.ui.employeedetail

import ae.oleapp.employee.data.model.response.AssignedClub
import ae.oleapp.employee.data.model.response.GetEmployeeDetails
import ae.oleapp.employee.data.model.response.GetEmployeeDetails.EmployeeNotes
import ae.oleapp.employee.data.model.response.ReviewData
import ae.oleapp.employee.data.repo.EmployeeRepo
import ae.oleapp.employee.utils.CustomToast
import ae.oleapp.employee.utils.DataState
import ae.oleapp.employee.utils.DateTimeHelper.DATE_FORMAT_YYYY_MM_DD
import ae.oleapp.employee.utils.DateTimeHelper.getCurrentDate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmployeeDetailVM (private val employeeRepo: EmployeeRepo) :ViewModel() {

    var employeeId = -1
    var employeeClubId = -1
    var noteId = -1
    var isEmployeeNoteUpdate = false

    //--------------------------------Api Calls---------------------------------------
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _getEmployeeDetail = MutableStateFlow<GetEmployeeDetails?>(null)
    val getEmployeeDetail : StateFlow<GetEmployeeDetails?> = _getEmployeeDetail.asStateFlow()

    private val _employeeNote = MutableStateFlow<String?>(null)
    val employeeNote : StateFlow<String?> = _employeeNote.asStateFlow()

    private val _employeeNoteResponse = MutableStateFlow<EmployeeNotes?>(null)
    val employeeNoteResponse : StateFlow<EmployeeNotes?> = _employeeNoteResponse.asStateFlow()

    //---------------------------------EmployeeRating-----------------------------------------

    private val _employeeRatings = MutableStateFlow<List<ReviewData>>(emptyList())
    val employeeRatings: StateFlow<List<ReviewData>> = _employeeRatings.asStateFlow()

    //----------------------------------ChangeClub-----------------------------------------

    private val _employeeChangeClub = MutableStateFlow<AssignedClub?>(null)
    val employeeChangeClub: StateFlow<AssignedClub?> = _employeeChangeClub.asStateFlow()


    fun getEmployeeDetail(){
        viewModelScope.launch(Dispatchers.IO) {
            employeeRepo.getEmployeeDetails(employeeId, employeeClubId)
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
                            _getEmployeeDetail.tryEmit(response.data.data)
                        }
                    }
                }
        }
    }

    fun setNote(note : String){
        _employeeNote.value = note
    }

    fun validateNote(){
        when{
            _employeeNote.value.isNullOrEmpty() -> CustomToast.makeToast("Please enter note")
            else -> addNoteApiCall()
        }
    }

    //---------------------------------Employee Note-----------------------------------------

    fun updateNote(){
        if(isEmployeeNoteUpdate){
            updateEmployeeNote()
        }else{
            addNoteApiCall()
        }
    }


    private fun addNoteApiCall() {
        viewModelScope.launch(Dispatchers.IO) {
            employeeRepo.addEmployeeNote(employeeId.toString(),
                employeeClubId.toString(),
                _employeeNote.value ?: "",
                getCurrentDate(DATE_FORMAT_YYYY_MM_DD)
            ).collect { response ->
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
//                        _employeeNoteResponse.tryEmit(response.data.data)
                        _employeeNote.value = null
                        getEmployeeDetail()
                    }
                }
            }

        }
    }

    private fun updateEmployeeNote() {
        viewModelScope.launch(Dispatchers.IO) {
            employeeRepo.updateEmployeeNote(
                noteId.toString(),
                _employeeNote.value ?: "",
            ).collect { response ->
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
//                        _employeeNoteResponse.tryEmit(response.data.data)
                        _employeeNote.value = null
                        getEmployeeDetail()
                    }
                }
            }

        }
    }

    fun deleteEmployeeNote() {
        viewModelScope.launch(Dispatchers.IO) {
            employeeRepo.deleteEmployeeNote(
                noteId.toString()
            ).collect { response ->
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
//                        _employeeNoteResponse.tryEmit(response.data.data)
                        _employeeNote.value = null
                        getEmployeeDetail()
                    }
                }
            }

        }
    }

    //---------------------------------EmployeeRating-----------------------------------------

    fun fetchEmployeeRatings(employeeId: Int){

        viewModelScope.launch(Dispatchers.IO) {
            employeeRepo.getEmployeeRatings(employeeId).collect { response->
                when(response){
                    is DataState.Error -> {
                        _isLoading.value = false
                    }
                    DataState.Loading -> {
                        _isLoading.value = true
                    }
                    is DataState.Success -> {
                        _isLoading.value = false
                        _employeeRatings.tryEmit(response.data.data)
                    }
                }
            }
        }

    }

    //---------------------------------ChangeClub-----------------------------------------

    fun changeEmployeeClub(employeeId: String , clubId : Int){

        viewModelScope.launch(Dispatchers.IO) {
            employeeRepo.assignClub(employeeId.toString() , clubId.toString()).collect { response->
                when(response){
                    is DataState.Error -> {
                        CustomToast.makeFancyToast(response.errorMessage , CustomToast.ERROR)
                        _isLoading.value = false
                    }
                    DataState.Loading -> {
                        _isLoading.value = true
                    }
                    is DataState.Success -> {
                        _isLoading.value = false
                        _employeeChangeClub.tryEmit(response.data.data)
                    }
                }
            }
        }

    }


}