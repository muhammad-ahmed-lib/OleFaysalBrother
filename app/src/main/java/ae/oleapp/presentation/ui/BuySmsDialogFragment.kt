package ae.oleapp.presentation.ui

import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.SmsPurchaseRequest
import ae.oleapp.abstraction.models.SmsPurchaseResponse
import ae.oleapp.abstraction.repository.SmsRepository
import ae.oleapp.databinding.FragmentBuySmsDialogBinding
import ae.oleapp.presentation.viewmodels.SmsViewModel
import ae.oleapp.presentation.viewmodels.SmsViewModelFactory
import ae.oleapp.utils.InsetsWithKeyboardCallback
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BuySmsDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBuySmsDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SmsViewModel
    private var clubId: String = ""
    private var smsCost: Double = 0.2
    private var onPurchaseComplete: ((Boolean) -> Unit)? = null

    companion object {
        fun newInstance(clubId: String,cost: Double, onPurchaseComplete: ((Boolean) -> Unit)? = null): BuySmsDialogFragment {
            return BuySmsDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("club_id", clubId)
                    putDouble("cost", cost)
                }
                this.onPurchaseComplete = onPurchaseComplete
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuySmsDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return super.onCreateDialog(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clubId = arguments?.getString("club_id") ?: ""
        smsCost = arguments?.getDouble("cost") ?: 0.2
        binding.amountEd.text=smsCost.toString()
        if (clubId.isEmpty()) {
            dismiss()
            return
        }

        // Initialize ViewModel
        val repository = SmsRepository(requireContext())
        viewModel = ViewModelProvider(this, SmsViewModelFactory(repository))[SmsViewModel::class.java]

        setupClickListeners()
        setupObservers()

        binding.qtyEd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                calculateAmount()
            }
        })
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            dismiss()
        }

        binding.submitBtn.setOnClickListener {
            purchaseSms()
        }
    }

    private fun setupObservers() {
        viewModel.purchaseResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    showProgress(true)
                }
                is ApiResponse.Success -> {
                    showProgress(false)
                    response.data?.let { handlePurchaseSuccess(it) }
                    onPurchaseComplete?.invoke(true)
                    dismiss()
                }
                is ApiResponse.Error -> {
                    showProgress(false)
                    response.error?.let { showError(it) }
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun calculateAmount() {
        val quantityText = binding.qtyEd.text.toString()
        if (quantityText.isBlank()) {
            binding.amountEd.text = null
            return
        }

        val quantity = quantityText.toIntOrNull() ?: return

        if (quantity <= 0) {
            binding.amountEd.text = null
            return
        }

        val amount = quantity * smsCost
        binding.amountEd.setText(String.format("%.2f", amount))
    }

    private fun purchaseSms() {
        val quantityText = binding.qtyEd.text.toString()

        if (quantityText.isBlank()) {
            binding.qtyEd.error = "Quantity is required"
            return
        }

        val quantity = quantityText.toIntOrNull() ?: run {
            binding.qtyEd.error = "Invalid quantity"
            return
        }

        if (quantity <= 0) {
            binding.qtyEd.error = "Quantity must be positive"
            return
        }

        // Amount is calculated automatically, so we don't need to validate it separately
        val amountText = binding.amountEd.text.toString()
        val amount = amountText.toDoubleOrNull() ?: run {
            showError("Invalid amount calculation")
            return
        }

        val request = SmsPurchaseRequest(
            clubId = clubId,
            amount = amount,
            quantity = quantity
        )

        viewModel.purchaseSms(request)
    }

    private fun showProgress(show: Boolean) {
     //   binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.submitBtn.isEnabled = !show
    }

    private fun handlePurchaseSuccess(response: SmsPurchaseResponse) {
        showSuccess(response.message ?: "SMS purchased successfully")
        // You could also update some shared preferences or cache with the new balance/total SMS
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
}