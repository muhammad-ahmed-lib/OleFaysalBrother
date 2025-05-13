package ae.oleapp.employee.ui.employeeDesignation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.R
import ae.oleapp.databinding.FragmentEmployeeDesignationBinding
import ae.oleapp.employee.data.model.response.GetDesignationDataResponse
import ae.oleapp.employee.ui.addemployee.AddEmployeeVM
import ae.oleapp.employee.utils.CustomToast
import ae.oleapp.employee.utils.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.compat.SharedViewModelCompat.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class EmployeeDesignationFragment : Fragment() {

    private val binding by viewBinding { FragmentEmployeeDesignationBinding.inflate(it) }


    private val viewModel by activityViewModel<AddEmployeeVM>()

    private lateinit var designationsAdapter: DesignationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDesignations(1)
    }

    private fun setupClicks() {
        binding.btnAdd.setOnClickListener {
            val action = EmployeeDesignationFragmentDirections.actionEmployeeDesignationFragmentToAddEmployeeDesignationFragment(null)
            findNavController().navigate(action)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun init(){
        designationsAdapter = DesignationsAdapter(onItemClicked = {clickedDesignation ->
            viewModel.setClickedDesignation(clickedDesignation)
            findNavController().popBackStack()
        }).apply {
            binding.designationListRV.adapter = this
        }
    }

    private fun setupObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.getEmployeeDesignationState.collectLatest {
                        designationsAdapter.submitList(it)
                    }
                }

            }
        }
    }

    private fun onDesignationClicked(designation: GetDesignationDataResponse): GetDesignationDataResponse{
        return designation
    }

}