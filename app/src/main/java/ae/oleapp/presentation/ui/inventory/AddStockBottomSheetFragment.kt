package ae.oleapp.presentation.ui.inventory

import ae.oleapp.databinding.FragmentAddStockBottomSheetBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddStockBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddStockBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStockBottomSheetBinding.inflate(inflater, container, false)

        // Transparent background
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            dismiss()
        }


    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
