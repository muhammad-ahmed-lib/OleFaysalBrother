package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.SalesOrderModelClass
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.ActivityInventoruSaleOrderBinding
import ae.oleapp.databinding.ActivityInventoryPurchaseBinding
import ae.oleapp.databinding.InventoryPurchaseRecItemBinding
import ae.oleapp.databinding.InventorySalesOrderRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.loadImage
import ae.oleapp.utils.showKProgress
import ae.oleapp.utils.splitToDayMonthAndYear
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import java.util.Date

data class InventorySellOrderModelClass(
    val orderNo: String,
    val orderedDate: String,
    val orderBy: String,
    val price: String,
    val qnt: String,
    val icon: Int
)

class InventorySaleOrderActivity : AppCompatActivity() {
    private val list by lazy { ArrayList<SalesOrderModelClass>() }
    private lateinit var viewModel: InventoryViewModel
    private val TAG = "InventorySaleOrderActivityInfo"
    private lateinit var adapter: GenericAdapter<SalesOrderModelClass,InventorySalesOrderRecItemBinding>

    private lateinit var binding: ActivityInventoruSaleOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoruSaleOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.setOnClickListener {
            finish()
        }

        val repository = InventoryRepository(this)
        viewModel = ViewModelProvider(
            this,
            InventoryViewModelFactory(repository)
        )[InventoryViewModel::class.java]

        observeSalesData()
    }

    private fun observeSalesData() {
        viewModel.salesOrderResponse.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    binding.root.showKProgress(true)
                    Log.d(TAG, "observeSalesData: Loading")
                }

                is ApiResponse.Success -> {
                    binding.root.showKProgress(false)
                    response.data?.let { summary ->
                        list.clear()
                        list.addAll(summary)
                        rec()
                    }
                }

                is ApiResponse.Error -> {
                    binding.root.showKProgress(false)
                    Toast.makeText(this, response.error ?: "Error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    private fun rec() {
        adapter = GenericAdapter(
            items = list,
            bindingInflater = InventorySalesOrderRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.orderNoTv.text = "Order No: ${item.order_number}"
            binding.amountTv.text = "${item.currency} ${item.total_amount}"
            binding.orderDateTv.text = item.sale_date
            binding.orderByNameTv.text = item.sold_by.toString()
            binding.quantityTv.text = "Qty: ${item.total_quantity}"
            binding.icon.loadImage(R.drawable.water_pack_img)
            binding.root.setOnClickListener {
                ItemDetailsBottomSheetFragment(myId = item.id).show(supportFragmentManager, "AddStockBottomSheet")
            }
        }

        // Initialize with current date
        try {
            val (dayMonth, year) = null.splitToDayMonthAndYear()
            binding.currentdateTv.text = dayMonth
            binding.currentYearTv.text = year
        } catch (e: Exception) {
            binding.currentdateTv.text = ""
            binding.currentYearTv.text = ""
        }

        binding.rec.adapter = adapter

        // Implement search functionality
        binding.searchViewEd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for basic search
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed for basic search
            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s?.toString()?.trim()?.lowercase()
                if (searchText.isNullOrEmpty()) {
                    adapter.updateData(list) // Show full list when search is empty
                } else {
                    val filteredList = list.filter { item ->
                        item.order_number?.lowercase()?.contains(searchText) == true ||
                                item.sold_by?.toString()?.lowercase()?.contains(searchText) == true ||
                                item.sale_date?.lowercase()?.contains(searchText) == true ||
                                item.total_amount.toString().contains(searchText) ||
                                item.currency?.lowercase()?.contains(searchText) == true
                    }
                    adapter.updateData(filteredList)
                }
            }
        })
    }
    override fun onResume() {
        super.onResume()




    }
}