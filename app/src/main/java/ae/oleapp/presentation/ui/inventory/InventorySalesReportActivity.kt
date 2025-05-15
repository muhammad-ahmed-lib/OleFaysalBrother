package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.SaleReportData
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.ActivityInventorySalesReportBinding
import ae.oleapp.databinding.InventorySalesReportRecItemBinding
import ae.oleapp.databinding.ProfiteDiscountRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.loadImage
import ae.oleapp.utils.splitToDayMonthAndYear
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class InventorySalesReportActivity : AppCompatActivity() {
    private val list by lazy { ArrayList<SaleReportData>() }
    private val profitDisList by lazy { ArrayList<InventorySellOrderModelClass>() }
    private lateinit var viewModel: InventoryViewModel
    private val TAG = "InventorySalesReportActivityInfo"
    private lateinit var binding: ActivityInventorySalesReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventorySalesReportBinding.inflate(layoutInflater)
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
        viewModel.fetchSalesReport(1) // Call it here after ViewModel is ready

        viewModel.salesReportResponse.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    Log.d(TAG, "observeSalesData: Loading")
                }
                is ApiResponse.Success -> {
                    response.data?.let { summary ->
                        list.clear()
                        list.add(summary)
                        rec(summary)
                        Log.d(TAG, "SalesReport $summary")
                    }
                }
                is ApiResponse.Error -> {
                    Toast.makeText(this, response.error ?: "Error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun rec(summary: SaleReportData) {
        // First adapter for inventory items
        val adapter = GenericAdapter(
            items = list,
            bindingInflater = InventorySalesReportRecItemBinding::inflate
        ) { binding, item, _ ->
            // Get the first inventory item from the first report
            val firstItem = item.report.firstOrNull()?.items?.firstOrNull()
            val inventoryName = firstItem?.inventory?.name ?: "No Name"
            val quantity = firstItem?.quantity ?: 0
            val amount = firstItem?.inventory?.selling_price ?: 0
            val quantityText = "Qty: $quantity"

            binding.amountTv.text = "AED $amount"
            binding.titleTv.text = inventoryName
            binding.qtyTv.text = quantityText
            binding.icon.loadImage(R.drawable.water_pack_img)
        }

        binding.rec.adapter = adapter

        // Prepare profit/discount list
        profitDisList.clear()

        // Get date information safely
        val firstReport = summary.report.firstOrNull()

        val date = firstReport?.sale_date ?: ""
        val (dayMonth, year) = if (date.isNotEmpty()) {
            date.splitToDayMonthAndYear()
        } else {
            Pair("", "")
        }

        // Update stats - ensure proper string conversion
        binding.currentAmountTv.text = "AED "+summary.stats.total_sales.toString() ?: "0"
        binding.currentDateMonthTv.text = dayMonth     // e.g., "18 May"
        binding.currentYearTv.text = year

        // Add items to profitDisList with proper null checks
        profitDisList.add(
            InventorySellOrderModelClass(
                orderNo = "Items Total",
                price = "AED ${summary.stats.total_orders ?: 0}",
                orderedDate = date,
                orderBy = firstReport?.sold_by?.name ?: "Unknown",
                qnt = summary.stats.total_quantity?.toString() ?: "0",
                icon = R.drawable.sale_inventory
            )
        )

        profitDisList.add(
            InventorySellOrderModelClass(
                orderNo = "Discount",
                price = "AED ${summary.stats.total_discount ?: 0}",
                orderedDate = date,
                orderBy = firstReport?.sold_by?.name ?: "Unknown",
                qnt = summary.stats.total_quantity?.toString() ?: "0",
                icon = R.drawable.sale_inventory
            )
        )

        // Second adapter for profit/discount
        val profitDiscountAdapter = GenericAdapter(
            items = profitDisList,
            bindingInflater = ProfiteDiscountRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text = item.orderNo
            binding.amountTv.text = item.price
        }
        binding.profitDiscountRec.adapter = profitDiscountAdapter
    }

}