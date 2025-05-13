package ae.oleapp.employee.ui.bookingHistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.R
import ae.oleapp.databinding.FragmentBookingHistoryBinding
import ae.oleapp.employee.utils.viewBinding
import androidx.navigation.fragment.findNavController

class BookingHistoryFragment : Fragment() {

    private val binding by viewBinding { FragmentBookingHistoryBinding.inflate(it) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClicks()
        setupRV()
    }

    private fun setupRV() {
        BookingHistoryAdapter().apply {
            binding.bookingHistoryListRV.adapter = this
        }
    }

    private fun setupClicks() {
        binding.btnFilter.setOnClickListener {
            findNavController().navigate(R.id.action_bookingHistoryFragment_to_bookingHistoryFilterFragment)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}