package ae.oleapp.presentation.ui.inventory

import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.FragmentItemDetailsBottomSheetBinding
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ItemDetailsBottomSheetFragment (val myId:Int=-1): BottomSheetDialogFragment() {

    private var _binding: FragmentItemDetailsBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InventoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailsBottomSheetBinding.inflate(inflater, container, false)

        // Transparent background
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = InventoryRepository(requireContext())
        viewModel = ViewModelProvider(this, InventoryViewModelFactory(repository))[InventoryViewModel::class.java]

        binding.backBtn.setOnClickListener {
            dismiss()
        }
        observeSummary()

    }
    private fun observeSummary() {
        if (id==-1)return
        viewModel.getSaleDetails(myId)
        viewModel.salesDetailResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    // Show loading if needed
                }
                is ApiResponse.Success -> {
                    response.data?.let {summary->
                        // Set order number safely
                        binding.orderNoTv.text = summary.order_number.toString() ?: "N/A"
                        binding.grandTotalTv.text = summary.total_amount.toString() ?: "N/A"
                        binding.itemNameTv.text = summary.items.firstOrNull()?.inventory?.name.toString() ?: "N/A"
                        binding.orderNo.text = summary.items.firstOrNull()?.inventory?.name.toString() ?: "N/A"
                        binding.itemNameCountTv.text = summary.items.firstOrNull()?.inventory?.quantity.toString() ?: "N/A"

                        // Set created date safely
                        summary.created_at.let { createdAt ->
                            binding.dateTv.text = createdAt // or use `date` if needed
                        }

                        // Set sale date safely
                        summary.sale_date?.let { saleDate ->
                            binding.discountTv.text = saleDate // or use `date` if needed
                        }
                    }

                }
                is ApiResponse.Error -> {
                  //  binding.titleTv.text = "Error: ${response.error}"
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
