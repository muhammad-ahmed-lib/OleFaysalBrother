package ae.oleapp.employee.ui.addemployee

import ae.oleapp.R
import ae.oleapp.databinding.FragmentAddEmployeeBinding
import ae.oleapp.employee.data.model.response.GetDesignationDataResponse
import ae.oleapp.employee.utils.viewBinding
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class AddEmployeeFragment : Fragment() {

    private val binding by viewBinding { FragmentAddEmployeeBinding.inflate(it) }
    private var viewCount = 0

    private val viewModel by activityViewModel<AddEmployeeVM>()

    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = uri
            binding.imageUri = selectedImageUri
            viewModel.setImageUri(uri)
        }

    }

    private val args by navArgs<AddEmployeeFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setClubId(args.clickedEmployeeClubId)
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

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.addImageCV.setOnClickListener {
            imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.employeeDesignationCV.setOnClickListener {
            findNavController().navigate(R.id.action_addEmployeeFragment_to_employeeDesignationFragment)
        }
        binding.btnAddEmployee.setOnClickListener {
            viewModel.validateEmployee()
        }
        binding.ccp.setOnCountryChangeListener {
            viewModel.setCountryId(binding.ccp.selectedCountryCode)
        }
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isEmployeeAdded.collectLatest { value ->
                        if (value) {
                            clearEmployeeData()
                        }

                    }
                }
            }
        }
    }



    private fun init(){
        args.employeeUpdateModel?.let {
            viewModel.setName(it.employeeName ?: "")
            viewModel.setEmail(it.employeeEmail ?: "")
            viewModel.setClickedDesignation(GetDesignationDataResponse(name = it.employeeDesignation ?: "" , id = it.employeeDesignationId ?: 0))
            viewModel.setSalary(it.employeeSalary ?: "")
            viewModel.setCountryId(it.employeeCountryId.toString() ?: "")
            binding.ccp.setCountryForPhoneCode(it.employeeCountryId ?: 0)
            viewModel.setPhone(it.employeePhone ?: "")
            viewModel.setClubId(it.employeeClubId ?: 0)
            viewModel.setEmployeeLoginId(it.employeeLoginId ?: "")
            viewModel.setEmployeeId(it.employeeId ?: "")
            viewModel.setPassword(it.employeePassword ?: "")
            viewModel.setIsEmployeeDetailUpdate(it.isEmployeeDetail)
            viewModel.setIsEmployeeCredentialUpdate(it.isEmployeeCredentials)
            viewModel.setIsEditMode(it.isEditMode)
        }
    }

    private fun clearEmployeeData() {
        binding.employeeNameET.text.clear()
        binding.employeeEmailET.text.clear()
        binding.employeePasswordET.text.clear()
        binding.employeeIdET.text.clear()
        binding.employeeSalaryET.text.clear()
        binding.employeeCountryET.text.clear()
        selectedImageUri = null
        binding.employeeDesignationTV.text = "Select Designation"
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetData()
    }

}