package ae.oleapp.employee.ui.employeeprofile

import ae.oleapp.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.databinding.FragmentEmployeeProfileBinding
import ae.oleapp.employee.data.model.common.UpdateEmployeeData
import ae.oleapp.employee.data.model.common.UpdateEmployeeDocument
import ae.oleapp.employee.data.model.common.UpdateEmployeeRoleWithPermission
import ae.oleapp.employee.ui.employeedetail.EmployeeDetailVM
import ae.oleapp.employee.ui.employeeprofile.viewDocument.DocumentAdapter
import ae.oleapp.employee.utils.findNavController
import ae.oleapp.employee.utils.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class EmployeeProfileFragment : Fragment() {

    private val binding by viewBinding { FragmentEmployeeProfileBinding.inflate(it) }

    private val viewModel : EmployeeDetailVM by viewModel<EmployeeDetailVM>(
        ownerProducer = {requireParentFragment()}
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        setupClicks()
        setupRV()
    }

    private fun setupRV() {
        DocumentAdapter(onItemClick = {clickedItem ->
            val isDocumentPresent = viewModel.getEmployeeDetail.value?.documents?.firstOrNull { it.type == clickedItem.name }

            isDocumentPresent?.let {
                when{
                    !it.front.isNullOrEmpty() || !it.back.isNullOrEmpty() -> {
                        val action = EmployeeProfileFragmentDirections.actionEmployeeProfileFragmentToViewDocumentFragment(
                        UpdateEmployeeDocument(
                            documenId = it.id,
                            employeeId = viewModel.getEmployeeDetail.value?.id ?: 0,
                            DocumentType = clickedItem,
                            isEditMode = true,
                            front = it.front,
                            back = it.back,
                            expiry = it.expiresAt
                        )
                    )
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.setGraph(R.navigation.app_navigation)
                    navController.navigate(action)
                    }
                    !it.document.isNullOrEmpty() -> {
                        val action = EmployeeProfileFragmentDirections.actionEmployeeProfileFragmentToGenericWebViewFragment(
                        UpdateEmployeeDocument(
                            documenId = it.id,
                            employeeId = viewModel.getEmployeeDetail.value?.id ?: 0,
                            DocumentType = clickedItem,
                            isEditMode = true,
                            isPdfCLick = true,
                            document = it.document,
                            expiry = it.expiresAt
                        )
                    )
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.setGraph(R.navigation.app_navigation)
                    navController.navigate(action)
                    }
                }
            } ?: run {
                val action = EmployeeProfileFragmentDirections.actionEmployeeProfileFragmentToChooseDocumentTypeFragment(
                        UpdateEmployeeDocument(
                            employeeId = viewModel.getEmployeeDetail.value?.id ?: 0,
                            DocumentType = clickedItem
                        )
                    )
                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.setGraph(R.navigation.app_navigation)
                    navController.navigate(action)
            }

        }).apply {
            binding.documentRV.adapter = this
        }
    }

    private fun setupClicks() {

        binding.btnChangePermission.setOnClickListener {

            val action =
                EmployeeProfileFragmentDirections.actionEmployeeProfileFragmentToAddEmployeeDesignationFragment(
                    UpdateEmployeeRoleWithPermission(
                        designationId = viewModel.getEmployeeDetail.value?.designation?.id ?: 0,
                        employeeDesignation = viewModel.getEmployeeDetail.value?.designation?.name ?: "",
                        employeePermission = viewModel.getEmployeeDetail.value?.designation?.permission ?: emptyList(),
                        employeeClubId = viewModel.getEmployeeDetail.value?.assignedClubs?.id ?: 0,
                        isEditMode = true
                    )
                )

            val navController = findNavController(R.id.nav_host_fragment)
            navController.setGraph(R.navigation.app_navigation)
            navController.navigate(action)
        }

        binding.btnChangeCredential.setOnClickListener {
            val action = EmployeeProfileFragmentDirections.actionEmployeeProfileFragmentToAddEmployeeFragment(
                UpdateEmployeeData(
                    employeeId = viewModel.getEmployeeDetail.value?.id.toString() ?: "",
                    employeeLoginId = viewModel.getEmployeeDetail.value?.employeeLoginId.toString() ?: "",
                    employeePassword = "",
                    isEmployeeDetail = false,
                    isEmployeeCredentials = true,
                    isEmployeePermission = false,
                    isEditMode = true
                )
            )

            val navController = findNavController(R.id.nav_host_fragment)
            navController.setGraph(R.navigation.app_navigation)
            navController.navigate(action)
        }
        binding.btnEdit.setOnClickListener {
            val action = EmployeeProfileFragmentDirections.actionEmployeeProfileFragmentToAddEmployeeFragment(
                UpdateEmployeeData(
                    employeeName = viewModel.getEmployeeDetail.value?.name ?: "",
                    employeeEmail = viewModel.getEmployeeDetail.value?.email ?: "",
                    employeePhone = viewModel.getEmployeeDetail.value?.number ?: "",
                    employeeCountryId = viewModel.getEmployeeDetail.value?.countryId ?: 0,
                    employeeSalary =  viewModel.getEmployeeDetail.value?.salary?.toString() ?: "",
                    employeeClubId = viewModel.getEmployeeDetail.value?.assignedClubs?.id ?: 0,
                    employeeId = viewModel.getEmployeeDetail.value?.id.toString() ?: "",
                    employeeDesignation = viewModel.getEmployeeDetail.value?.designation?.name?: "",
                    employeeDesignationId = viewModel.getEmployeeDetail.value?.designation?.id ?: 0,
//                    employeePermission = "",
                    isEmployeeDetail = true,
                    isEmployeeCredentials = false,
                    isEmployeePermission = false,
                    isEditMode = true
                )
            )

            val navController = findNavController(R.id.nav_host_fragment)
            navController.setGraph(R.navigation.app_navigation)
            navController.navigate(action)
        }
    }

}