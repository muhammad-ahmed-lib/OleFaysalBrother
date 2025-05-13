package ae.oleapp.employee.ui.employeedetailsummary

import ae.oleapp.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.databinding.FragmentEmployeeDetailSummaryBinding
import ae.oleapp.employee.data.model.response.GetEmployeeDetails
import ae.oleapp.employee.ui.changeClub.ChangeClubFragment
import ae.oleapp.employee.ui.employeedetail.EmployeeDetailVM
import ae.oleapp.employee.utils.findNavController
import ae.oleapp.employee.utils.viewBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmployeeDetailSummaryFragment : Fragment() {

    private val binding by viewBinding { FragmentEmployeeDetailSummaryBinding.inflate(it) }

    private val viewModel : EmployeeDetailVM by viewModel<EmployeeDetailVM>(
        ownerProducer = {requireParentFragment()}
    )
    private lateinit var bookingAdapter : BookingDataAdapter
    private var employeeDetail  : GetEmployeeDetails? = null

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

        setupRV()
        setupObserver()
        setupClicks()
    }

    private fun setupClicks() {

        binding.AssignCV.setOnClickListener {
            ChangeClubFragment.newInstance(viewModel.employeeClubId ?: 0  , employeeDetail?.id.toString() ?: "" , childFragmentManager)
        }
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.getEmployeeDetail.collectLatest { employeeDetailResponse ->
                        employeeDetail = employeeDetailResponse
//                        employeeDetailResponse?.bookingData?.let { bookingData -> bookingAdapter.submitList(bookingData) }
                    }
                }


            }
        }

    }

    private fun setupRV() {
        bookingAdapter = BookingDataAdapter(onBookingDataClick = {
            val action = EmployeeDetailSummaryFragmentDirections.actionEmployeeDetailSummaryFragmentToEmployeeBookingHistoryFragment()
            val navController = findNavController(R.id.nav_host_fragment)
            navController.setGraph(R.navigation.app_navigation)
            navController.navigate(action)
        }).apply {
            binding.bookingDataRV.adapter = this
        }
    }

}