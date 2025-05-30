package ae.oleapp.presentation.ui.inventory

import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.InventoryProduct
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.ActivityInventorySellBinding
import ae.oleapp.databinding.InventorySellRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.loadImage
import ae.oleapp.utils.openActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.io.Serializable


data class InventorySellModelClass(
    val title: String,
    val stock: String,
    val amount: String,
    val icon: Int,
    var count: Int=0
)

class InventorySellActivity : AppCompatActivity() {
    private val list by lazy { ArrayList<InventoryProduct>() }
    private lateinit var adapter: GenericAdapter<InventoryProduct, InventorySellRecItemBinding>
    private lateinit var binding: ActivityInventorySellBinding
    private lateinit var viewModel: InventoryViewModel
    private var totalAmount = 0.0 // Track total amount

    private val TAG = "InventorySellActivityInfo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventorySellBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize total amount display
        binding.payableAmountTv.text = "Total: AED 0.00"

        val repository = InventoryRepository(this)
        viewModel = ViewModelProvider(
            this,
            InventoryViewModelFactory(repository)
        )[InventoryViewModel::class.java]

        observeSalesData()

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun observeSalesData() {
        viewModel.productsResponse.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    Log.d(TAG, "observeSalesData: Loading")
                }
                is ApiResponse.Success -> {
                    response.data?.let { summary ->
                        list.clear()
                        list.addAll(summary.map { it.copy(count = 0) }) // Initialize count to 0
                        rec()
                    }
                }
                is ApiResponse.Error -> {
                    Toast.makeText(this, response.error ?: "Error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private var InventoryProduct.count: Int
        get() = try {
            val countField = this.javaClass.getDeclaredField("count")
            countField.isAccessible = true
            countField.getInt(this)
        } catch (e: Exception) {
            0
        }
        set(value) {
            try {
                val countField = this.javaClass.getDeclaredField("count")
                countField.isAccessible = true
                countField.setInt(this, value)
            } catch (e: Exception) {
                Log.e(TAG, "Error setting count", e)
            }
        }
    private fun rec() {
        adapter = GenericAdapter(
            items = list,
            bindingInflater = InventorySellRecItemBinding::inflate
        ) { binding, item, position ->
            binding.titleTv.text = item.name
            binding.amountTv.text = "AED ${item.selling_price}"
            binding.stockTv.text = "Stock: ${item.current_stock}"
            binding.icon.loadImage(item.photo)

            binding.plusBtn.setOnClickListener {
                if (item.count < item.current_stock) { // Check stock availability
                    item.count++
                    binding.counterTv.text = item.count.toString()
                    updateTotalAmount(item.selling_price, true)
                } else {
                    Toast.makeText(this@InventorySellActivity, "Not enough stock", Toast.LENGTH_SHORT).show()
                }
            }

            binding.minosBtn.setOnClickListener {
                if (item.count > 0) {
                    item.count--
                    binding.counterTv.text = item.count.toString()
                    updateTotalAmount(item.selling_price, false)

                }
            }

            binding.root.setOnClickListener {
                val i = Intent(this, AddInventoryProductActivity::class.java)
                i.putExtra("PRODUCT_ID", item.id)
                startActivity(i)
            }
        }

        binding.moneyRec.adapter = adapter

        binding.adItemBg.setOnClickListener {
            openActivity<AddInventoryProductActivity>()
        }
        binding.payBtn.setOnClickListener {
            // Prepare selected items with all required details


            val selectedItems = list.filter { it.count > 0 }.map {
                CartItemModel(
                    productId = it.id,
                    name = it.name,          // Include name
                    price = it.selling_price, // Include price
                    quantity = it.count
                )
            }

            // Create intent and pass data
            val i = Intent(this, InventoryPaymentActivity::class.java).apply {
                putExtra("TOTAL_AMOUNT", totalAmount)
                putExtra("ITEMS_TOTAL", selectedItems.sumOf { it.quantity })
                putExtra("DISCOUNT", 0.0)
                putExtra("SELECTED_ITEMS", ArrayList(selectedItems))
            }
            startActivity(i)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalAmount(price: Double, isAdd: Boolean) {
        totalAmount = if (isAdd) {
            totalAmount + price
        } else {
            totalAmount - price
        }
        binding.payableAmountTv.text = "Total: AED ${String.format("%.2f", totalAmount)}"
        adapter.notifyDataSetChanged()
    }

    // Add this extension to InventoryProduct
    private fun InventoryProduct.copy(count: Int): InventoryProduct {
        return this.copy(
            id = id,
            name = name,
            description = description,
            photo = photo,
            category = category,
            barcode = barcode,
            sku = sku,
            purchase_price = purchase_price,
            selling_price = selling_price,
            min_stock_level = min_stock_level,
            unit_of_measure = unit_of_measure,
            is_active = is_active,
            created_at = created_at,
            current_stock = current_stock
        ).also {
            // This is a hack since data class copy doesn't include custom properties
            // You should ideally add 'count' as a property in InventoryProduct
            try {
                val countField = it.javaClass.getDeclaredField("count")
                countField.isAccessible = true
                countField.setInt(it, count)
            } catch (e: Exception) {
                Log.e(TAG, "Error setting count", e)
            }
        }
    }
}
data class CartItemModel(
    val productId: Int,
    val name: String,       // Add product name
    val price: Double,      // Add product price
    val quantity: Int
) : Serializable