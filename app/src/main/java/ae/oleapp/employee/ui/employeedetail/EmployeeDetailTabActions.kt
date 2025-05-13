package ae.oleapp.employee.ui.employeedetail

sealed class EmployeeDetailTabActions {
    object Summary : EmployeeDetailTabActions()
    object Note : EmployeeDetailTabActions()
    object Profile : EmployeeDetailTabActions()
    object Salary : EmployeeDetailTabActions()
}
data class EmployeeDetailTabsData(val tabName:String , val tabAction:EmployeeDetailTabActions)

val employeeDetailTabsList = listOf(
    EmployeeDetailTabsData("Summary" , EmployeeDetailTabActions.Summary),
    EmployeeDetailTabsData("Note" , EmployeeDetailTabActions.Note),
    EmployeeDetailTabsData("Profile" , EmployeeDetailTabActions.Profile),
    EmployeeDetailTabsData("Salary" , EmployeeDetailTabActions.Salary)
)