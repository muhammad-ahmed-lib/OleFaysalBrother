package ae.oleapp.presentation.ui.inventory

import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.FragmentAddStockBottomSheetBinding
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.showKProgress
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddStockBottomSheetFragment(
    private val productId: String? = null,
    private val onDone: ((Boolean) -> Unit)? = null // Callback function
) : BottomSheetDialogFragment() {

    private var _binding: FragmentAddStockBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InventoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStockBottomSheetBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val repository = InventoryRepository(requireContext())

        val factory = InventoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[InventoryViewModel::class.java]

        setupClickListeners()
        setupObservers()
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            dismiss()
        }

        binding.submitBtn.setOnClickListener {
            updateStock()
        }
    }

    private fun setupObservers() {
        viewModel.stockUpdateResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Loading -> showProgress(true)
                is ApiResponse.Success -> {
                    showProgress(false)
                    showSuccess("Stock updated successfully!")
                    onDone?.invoke(true)
                    dismiss()

                }
                is ApiResponse.Error -> {
                    showProgress(false)
                    showError(response.error.toString())
                    onDone?.invoke(false)
                }
            }
        }
    }

    private fun updateStock() {
        val quantityText = binding.qtyEd.text.toString()

        if (quantityText.isBlank()) {
            binding.qtyEd.error = "Quantity is required"
            return
        }

        val quantity = quantityText.toIntOrNull() ?: run {
            binding.qtyEd.error = "Invalid quantity"
            return
        }

        if (productId != null) {
            viewModel.updateStock(
                productId = productId,
                quantity = quantity,
                description = null // Add description if needed
            )
        }
    }

    private fun showProgress(show: Boolean) {
        binding.root.showKProgress(show)
        // Implement your progress indicator
        binding.submitBtn.isEnabled = !show
    }

    private fun showSuccess(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(productId: String,onDone: ((Boolean) -> Unit)? = null): AddStockBottomSheetFragment {
            return AddStockBottomSheetFragment(productId,onDone)
        }
    }
}