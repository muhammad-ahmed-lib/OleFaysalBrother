package ae.oleapp.employee.ui.employeerating

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.R
import ae.oleapp.databinding.FragmentEmployeeRatingDetailBinding
import ae.oleapp.employee.utils.viewBinding

class EmployeeRatingDetailFragment : Fragment() {

    private val binding by viewBinding { FragmentEmployeeRatingDetailBinding.inflate(it) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClicks()
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {  }
    }


}