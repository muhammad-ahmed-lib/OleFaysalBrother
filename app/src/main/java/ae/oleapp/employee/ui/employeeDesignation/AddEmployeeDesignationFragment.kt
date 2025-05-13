package ae.oleapp.employee.ui.employeeDesignation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.R
import ae.oleapp.databinding.FragmentAddEmployeeDesignationBinding
import ae.oleapp.employee.data.model.response.GetDesignationDataResponse
import ae.oleapp.employee.ui.addemployee.AddEmployeeVM
import ae.oleapp.employee.ui.employeelist.EmployeeListAdapter
import ae.oleapp.employee.ui.employeelist.EmployeeListVM
import ae.oleapp.employee.utils.CustomToast
import ae.oleapp.employee.utils.viewBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddEmployeeDesignationFragment : Fragment() {

    private val binding by viewBinding { FragmentAddEmployeeDesignationBinding.inflate(it)  }

    private val viewModel by viewModel<AddEmployeeVM>()
    private val permissionAdapter by lazy { PermissionAdapter().apply { binding.permissionRV.adapter = this } }

    private val args by navArgs<AddEmployeeDesignationFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPermissions()
    }

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


        init()
        setupClicks()
        setupObserver()
    }

    private fun init(){
        args.employeeDesignationUpdateModel?.let {
            viewModel.desingationId = it.designationId ?: 0
            viewModel.setIsEditMode(it.isEditMode)
            binding.employeeDesignationTitleET.setText(it.employeeDesignation)
            viewModel.setClubId(it.employeeClubId ?: 0)
        }
    }


    private fun setupObserver() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.getPermissionState.collectLatest {
                        permissionAdapter.submitList(it)
                        permissionAdapter.preSelectedPermission(
                            args.employeeDesignationUpdateModel?.employeePermission ?: emptyList()
                        )
                    }
                }

            }
        }

    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAdd.setOnClickListener {
            if (binding.employeeDesignationTitleET.text.toString().isNotEmpty()) {
                val permissionIdList = permissionAdapter.getSelectedPermissionIds()
                viewModel.addDesignation(binding.employeeDesignationTitleET.text.toString() ,  permissionIdList.toString() , viewModel.clubId.value ?: 0)

            }else{
                CustomToast.makeToast("Please enter designation")
            }
        }
    }

}