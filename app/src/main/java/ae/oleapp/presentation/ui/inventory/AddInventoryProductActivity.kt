package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.ActivityAddInventoryProductBinding
import ae.oleapp.employee.utils.gone
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.getPurchaseModel
import ae.oleapp.utils.loadImage
import ae.oleapp.utils.showKProgress
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.net.toFile
import androidx.lifecycle.ViewModelProvider
import java.io.File

class AddInventoryProductActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddInventoryProductBinding
    private lateinit var viewModel: InventoryViewModel
    private var selectedImageFile: File? = null
    private var isUpdateMode = false
    private var productId: String? = null
    private val TAG="AddInventoryProductActivityInfo"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddInventoryProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        val repository = InventoryRepository(this)
        viewModel = ViewModelProvider(
            this,
            InventoryViewModelFactory(repository)
        )[InventoryViewModel::class.java]

        // Check if we're in update mode
        checkIntentForUpdate()

        setupUI()
        setupClickListeners()
        setupObservers()

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun checkIntentForUpdate() {
        intent?.getPurchaseModel("PURCHASE_MODEL")?.let {
            if (it.isUpdate){
                isUpdateMode = true
                productId = it.editId
                Log.d(TAG, "checkIntentForUpdate: will Update $productId")
                binding.titleTv.text = getString(R.string.update_product)
                binding.itemNameEd.setText(it.name)
                binding.purchasePriceEd.setText(it.purchasePrice)
                binding.salePriceEd.setText(it.salePrice)
                binding.quantityEd.setText(it.quantity)
                binding.clubIdEd.setText(it.clubId)

                if (it.imageUrl != null) {
                    binding.imagePreview.loadImage(it.imageUrl)
                    binding.iamgehint.gone()
                    binding.imageHintTv.gone()
                }
            }
        }
        /* intent?.extras?.let { bundle ->
            if (bundle.containsKey("PRODUCT_ID")) {
                isUpdateMode = true
                productId = bundle.getString("PRODUCT_ID")
                binding.titleTv.text = getString(R.string.update_product)
                // Load existing product data here if needed
            }
        }*/
    }

    private fun setupUI() {
        // Set up any additional UI configurations
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.pickImageBtn.setOnClickListener {
            openImagePicker()
        }

        binding.submitBtn.setOnClickListener {
            if (validateInputs()) {
                if (isUpdateMode) {
                    updateProduct()
                } else {
                    addProduct()
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.addProductResponse.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    showProgress(true)
                }
                is ApiResponse.Success -> {
                    showProgress(false)
                    showSuccess("Product added successfully!")
                    finish()
                }
                is ApiResponse.Error -> {
                    showProgress(false)
                    showError(response.error.toString())
                }
            }
        }

        viewModel.updateProductResponse.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> showProgress(true)
                is ApiResponse.Success -> {
                    showProgress(false)
                    showSuccess("Product updated successfully!")
                    finish()
                }
                is ApiResponse.Error -> {
                    showProgress(false)
                    showError(response.error.toString())
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val clubId = binding.clubIdEd.text.toString()
        val name = binding.itemNameEd.text.toString()
        val purchasePrice = binding.purchasePriceEd.text.toString()
        val salePrice = binding.salePriceEd.text.toString()
        val quantity = binding.quantityEd.text.toString()

        if (clubId.isEmpty()) {
            binding.clubIdEd.error = "Club ID is required"
            return false
        }

        if (name.isEmpty()) {
            binding.itemNameEd.error = "Item name is required"
            return false
        }

        if (purchasePrice.isEmpty()) {
            binding.purchasePriceEd.error = "Purchase price is required"
            return false
        }

        if (salePrice.isEmpty()) {
            binding.salePriceEd.error = "Sale price is required"
            return false
        }

        if (quantity.isEmpty()) {
            binding.quantityEd.error = "Quantity is required"
            return false
        }

        return true
    }

    private fun addProduct() {
        val clubId = binding.clubIdEd.text.toString().toInt()
        val name = binding.itemNameEd.text.toString()
        val purchasePrice = binding.purchasePriceEd.text.toString().toDouble()
        val salePrice = binding.salePriceEd.text.toString().toDouble()
        val quantity = binding.quantityEd.text.toString().toInt()

        viewModel.addInventoryProduct(
            clubId = clubId,
            name = name,
            purchasePrice = purchasePrice,
            sellingPrice = salePrice,
            quantity = quantity,
            photoFile = selectedImageFile
        )
    }

    private fun updateProduct() {
        val clubId = binding.clubIdEd.text.toString().toInt()
        val name = binding.itemNameEd.text.toString()
        val purchasePrice = binding.purchasePriceEd.text.toString().toDouble()
        val salePrice = binding.salePriceEd.text.toString().toDouble()
        val quantity = binding.quantityEd.text.toString().toInt()

        productId?.let { id ->
            viewModel.updateInventoryProduct(
                productId = id,
                clubId = clubId,
                name = name,
                purchasePrice = purchasePrice,
                sellingPrice = salePrice,
                quantity = quantity,
                photoFile = selectedImageFile
            )
        } ?: run {
            showError("Product ID is missing")
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageFile = uri.toFile()
                // Display the selected image
                binding.imagePreview.loadImage(selectedImageFile)
            }
        }
    }

    private fun showProgress(show: Boolean) {
        // Implement your progress dialog
        binding.root.showKProgress(show)

    }

    private fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }
}