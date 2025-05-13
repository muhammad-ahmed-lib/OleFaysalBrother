package ae.oleapp.employee.ui.employeerating

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.R
import ae.oleapp.databinding.FragmentEmployeeRatingBinding
import ae.oleapp.employee.ui.employeedetail.EmployeeDetailVM
import ae.oleapp.employee.utils.viewBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class EmployeeRatingFragment : Fragment() {

    private val binding by viewBinding { FragmentEmployeeRatingBinding.inflate(it) }

    private val viewModel : EmployeeDetailVM by viewModel<EmployeeDetailVM>(
        ownerProducer = {requireParentFragment()}
    )
    private lateinit var adapter: EmployeeRatingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchEmployeeRatings(viewModel.employeeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      setupRV()
        setupClicks()
        setupObserver()

    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRV() {
        adapter = EmployeeRatingsAdapter(onRatingClick = {
            findNavController().navigate(R.id.action_employeeRatingFragment_to_employeeRatingDetailFragment)
        }).apply {
            binding.ratingsRV.adapter = this
        }
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.employeeRatings.collectLatest { ratingList ->
                        adapter.submitList(ratingList)
                    }
                }
            }
        }
    }


}