package ae.oleapp.employee.ui.bookingHistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.R
import ae.oleapp.databinding.FragmentBookingHistoryFilterBinding
import ae.oleapp.employee.utils.CustomToast
import ae.oleapp.employee.utils.viewBinding
import android.app.Dialog
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BookingHistoryFilterFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding { FragmentBookingHistoryFilterBinding.inflate(it) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener { dialog ->
                val bottomSheet = (dialog as BottomSheetDialog)
                    .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

                bottomSheet?.let {
                    val behavior = BottomSheetBehavior.from(it)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    it.setBackgroundResource(R.drawable.rounded_top_sheet)
                    it.requestLayout()
                }
                setCancelable(false)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRV()
        setupClick()
    }

    private fun setupRV() {
        BookingFilterAdapter(onFilterClick = {clickedFilter ->
        }).apply {
            binding.filterRV.adapter = this
        }
    }

    private fun setupClick() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}