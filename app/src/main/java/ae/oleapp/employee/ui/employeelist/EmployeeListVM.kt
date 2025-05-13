package ae.oleapp.employee.ui.employeelist


import ae.oleapp.employee.data.model.response.GetAllEmployeeResponse
import ae.oleapp.employee.data.model.response.GetClub
import ae.oleapp.employee.data.model.response.GetClub.Companion.storeList
import ae.oleapp.employee.data.repo.EmployeeRepo
import ae.oleapp.employee.utils.CustomToast
import ae.oleapp.employee.utils.DataState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EmployeeListVM (private val employeeRepo: EmployeeRepo ) : ViewModel() {


    private val _employeeListState = MutableStateFlow<List<GetAllEmployeeResponse>>(emptyList())
    val employeeListState: StateFlow<List<GetAllEmployeeResponse>> = _employeeListState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading :StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _activeEmployeeData = MutableStateFlow<GetAllEmployeeResponse?>(null)
    val activeEmployeeData: StateFlow<GetAllEmployeeResponse?> = _activeEmployeeData.asStateFlow()

    private val _isActiveEmployee = MutableStateFlow(false)
    val isActiveEmployee: StateFlow<Boolean> = _isActiveEmployee.asStateFlow()

    private val _getClubListState = MutableStateFlow<List<GetClub>>(emptyList())
    val getClubListState : StateFlow<List<GetClub>> = _getClubListState.asStateFlow()


    init {
        getClubList()
    }

    fun fetchEmployees(clubId: Int) {
        viewModelScope.launch {
            employeeRepo.getAllEmployees(clubId).collect { response ->
                when (response) {
                    is DataState.Error -> {
                        _isLoading.value = false
                        CustomToast.makeFancyToast(response.errorMessage , CustomToast.ERROR)
                    }
                    DataState.Loading -> {
                        _isLoading.value = true
                    }
                    is DataState.Success -> {
                        _isLoading.value = false
                        _employeeListState.tryEmit(response.data.data)
                        updateIsActive()
                    }
                }

            }
        }
    }

    private fun updateIsActive() {
        val activeEmp = _employeeListState.value.firstOrNull { it.employeeOfTheMonth ?: false }
        _activeEmployeeData.value = activeEmp
        _isActiveEmployee.value = activeEmp != null
    }

    private fun getClubList(latitude: Double = 0.0, longitude: Double = 0.0, date: String = "") {
        viewModelScope.launch {
            employeeRepo.getOwnerClubList(latitude, longitude, date).collect { response ->

                when (response) {
                    is DataState.Error -> {
                        CustomToast.makeToast(response.errorMessage)
                    }
                    DataState.Loading -> {
                    }
                    is DataState.Success -> {
                        _getClubListState.tryEmit(response.data.data)
                        storeList(response.data.data)

                    }
                }
            }
        }
    }




}