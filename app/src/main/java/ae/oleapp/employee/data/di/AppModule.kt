package ae.oleapp.employee.data.di

import ae.oleapp.employee.data.repo.EmployeeRepo
import ae.oleapp.employee.data.repo.EmployeeRepoImp
import ae.oleapp.employee.ui.EmployeeActivity
import ae.oleapp.employee.ui.addemployee.AddEmployeeVM
import ae.oleapp.employee.ui.employeedetail.EmployeeDetailVM
import ae.oleapp.employee.ui.employeelist.EmployeeListVM
import ae.oleapp.employee.ui.employeeprofile.viewDocument.DocumentVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

object AppModule {
    val allModules = listOf(
        networkModule,
        repositoryModule,
        viewModelModule,
    )
}

val repositoryModule = module {
    single<EmployeeRepo> { EmployeeRepoImp(get(named("partnerApi")),get(named("newApi"))) }
}

val viewModelModule = module {
    viewModel { AddEmployeeVM(get() , get()) }
    viewModel { EmployeeListVM(get()) }
    viewModel { EmployeeDetailVM(get()) }
    viewModel { DocumentVM(get() , get()) }
}