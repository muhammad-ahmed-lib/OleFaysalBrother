package ae.oleapp.employee.ui.employeelist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.R
import ae.oleapp.databinding.FragmentEmployeeListBinding
import ae.oleapp.employee.ui.BaseFragment
import ae.oleapp.employee.utils.CustomToast
import ae.oleapp.employee.utils.viewBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmployeeListFragment : BaseFragment() {

    private val binding by viewBinding { FragmentEmployeeListBinding.inflate(it) }

    private lateinit var employeeListAdapter: EmployeeListAdapter
    private lateinit var clubListAdapter: ClubListAdapter

    private val viewModel by activityViewModel<EmployeeListVM>()
    private var clubId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clubId =  arguments?.getInt("clubId" , 0) ?: 0
        viewModel.fetchEmployees(clubId)
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
        setUpClicks()
        setupObserver()
    }

    private fun setUpClicks() {
        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnAdd.setOnClickListener {
            val action = EmployeeListFragmentDirections.actionEmployeeListFragmentToAddEmployeeFragment(clubId,null)
            findNavController().navigate(action)
        }


    }

    private fun init(){
        employeeListAdapter = EmployeeListAdapter(onItemClick = { clickedEmployeeDetails ->
            val action =
                EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(
                    clickedEmployeeDetails.id ?: 0,
                    clickedEmployeeDetails.assignedClubs?.id ?: 0
                )
            findNavController().navigate(action)

        },
            onCallClicked = {
                makeCall(it)
            }).apply {
            binding.employeeListRV.adapter = this
        }

        clubListAdapter = ClubListAdapter(onClubSelected = {selectedClub ->
            clubId = selectedClub.id?.toIntOrNull() ?: 0
            viewModel.fetchEmployees(clubId)
        }).apply {
            binding.tabRV.adapter = this
        }

    }

    private fun setupObserver() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.employeeListState.collectLatest {
                        employeeListAdapter.submitList(it)
                    }
                }

                launch {
                    viewModel.getClubListState.collectLatest {
                        clubListAdapter.submitList(it)
                    }
                }

            }
        }

    }

}