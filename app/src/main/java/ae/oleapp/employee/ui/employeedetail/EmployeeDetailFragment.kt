package ae.oleapp.employee.ui.employeedetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.R
import ae.oleapp.databinding.FragmentEmployeeDetailBinding
import ae.oleapp.employee.ui.BaseFragment
import ae.oleapp.employee.ui.addemployee.adapter.GenericViewPagerAdapter
import ae.oleapp.employee.ui.employeedetailsummary.EmployeeDetailSummaryFragment
import ae.oleapp.employee.ui.employeenote.EmployeeNoteFragment
import ae.oleapp.employee.ui.employeeprofile.EmployeeProfileFragment
import ae.oleapp.employee.ui.employeesalary.EmployeeSalaryFragment
import ae.oleapp.employee.utils.viewBinding
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel

class EmployeeDetailFragment : BaseFragment() {

    private val binding by viewBinding { FragmentEmployeeDetailBinding.inflate(it) }
    private lateinit var adapter: GenericViewPagerAdapter

    private val viewModel : EmployeeDetailVM by viewModel<EmployeeDetailVM>()

    private val args : EmployeeDetailFragmentArgs by navArgs()

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
        init()
        setupViewPager()
        setupTabsRV()
        setupClicks()
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.ratingCV.setOnClickListener {
            val navController = findNavController()
            navController.setGraph(R.navigation.app_navigation)
            navController.navigate(R.id.action_employeeDetailFragment_to_employeeRatingFragment)
        }

        binding.btnCall.setOnClickListener {
           makeCall(viewModel.getEmployeeDetail?.value?.phone ?: "")
        }
    }

    private fun setupTabsRV() {
        EmployeeDetailTabAdapter(tabList = employeeDetailTabsList , onTabSelected = {
            binding.employeeDetailVP.currentItem = employeeDetailTabsList.indexOf(it)
        }).apply {
            binding.tabRV.adapter = this
        }
    }

    private fun setupViewPager() {


        adapter = GenericViewPagerAdapter(this, getEmployeeTabFragments())
        binding.employeeDetailVP.adapter = adapter
    }

    private fun init(){
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        viewModel.employeeId = args.clickedEmployeeId
        viewModel.employeeClubId = args.clickedEmployeeClubId
        viewModel. getEmployeeDetail()
    }

    private fun getEmployeeTabFragments(): List<Fragment> = listOf(
        EmployeeDetailSummaryFragment(),
        EmployeeNoteFragment(),
        EmployeeProfileFragment(),
        EmployeeSalaryFragment()
    )


}