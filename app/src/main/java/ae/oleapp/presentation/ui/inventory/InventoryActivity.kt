package ae.oleapp.presentation.ui.inventory

import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.GenericModelClass
import ae.oleapp.abstraction.models.InventorySummary
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.ActivityInventoryBinding
import ae.oleapp.databinding.InventoryMoneyRecItemBinding
import ae.oleapp.databinding.InventoryRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.loadImage
import ae.oleapp.utils.openActivity
import ae.oleapp.utils.splitToDayMonthAndYear
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider


data class InventoryMoneyModelClass(
    val title: String,
    val amount: String,
    val totalPercentage: String,
    val icon: Int
)

class InventoryActivity : AppCompatActivity() {
    private val moneyRecList by lazy { ArrayList<InventoryMoneyModelClass>() }
    private val list by lazy { ArrayList<GenericModelClass>() }
    private lateinit var binding: ActivityInventoryBinding
    private lateinit var viewModel: InventoryViewModel
    private lateinit var moneyRecAdapter: GenericAdapter<InventoryMoneyModelClass, InventoryMoneyRecItemBinding>
    private val TAG = "inventoryInfo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = InventoryRepository(this)
        viewModel = ViewModelProvider(
            this,
            InventoryViewModelFactory(repository)
        )[InventoryViewModel::class.java]

        setupAdapters()
        observeSalesData()
        setupClickListeners()
    }

    private fun setupAdapters() {
        // Initialize money recycler adapter
        moneyRecAdapter = GenericAdapter(
            items = moneyRecList,
            bindingInflater = InventoryMoneyRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text = item.title
            binding.amountTv.text = item.amount
            binding.totalSellPercentageTv.text = item.totalPercentage
            binding.icon.loadImage(item.icon)
        }
        binding.moneyRec.adapter = moneyRecAdapter

        // Initialize menu items adapter
        val adapter = GenericAdapter(
            items = list,
            bindingInflater = InventoryRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text = item.title
            binding.icon.loadImage(item.icon)
            binding.root.setOnClickListener {
                item.screen?.let { openActivity(it) }
            }
        }
        binding.rec.adapter = adapter
    }

    private fun observeSalesData() {
        viewModel.homePageSummary.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    Log.d(TAG, "observeSalesData: Loading")
                }

                is ApiResponse.Success -> {
                    response.data?.let { summary ->
                        updateMoneyData(summary)
                    }
                }

                is ApiResponse.Error -> {
                    Toast.makeText(this, response.error ?: "Error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun updateMoneyData(summary: InventorySummary) {
        moneyRecList.clear()
        summary.let {
            Log.d(TAG, "updateMoneyData: $summary")
            val (dayMonth, year) = it.date.splitToDayMonthAndYear()
            binding.currentDateMonthTv.text = dayMonth     // e.g., "18 May"
            binding.currentYearTv.text = year              // e.g., "2025"

            binding.currentAmountTv.text=it.currency+" "+it.profit.total.toString()
        }
        // Add sales data
        moneyRecList.add(
            InventoryMoneyModelClass(
                title = "Total Sell",
                icon = R.drawable.total_sell_img,
                amount = "${summary.currency} ${summary.sales.total}",
                totalPercentage = "" // Add your percentage calculation if available
            )
        )

        // Add purchase data
        moneyRecList.add(
            InventoryMoneyModelClass(
                title = "Total Purchase",
                icon = R.drawable.total_purchase_img,
                amount = "${summary.currency} ${summary.purchase.total}",
                totalPercentage = "" // Add your percentage calculation if available
            )
        )

        moneyRecAdapter.notifyDataSetChanged()
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Initialize static menu items
        initializeMenuItems()
    }

    private fun initializeMenuItems() {
        list.clear()
        list.addAll(
            listOf(
                GenericModelClass(
                    title = "Sell",
                    icon = R.drawable.sell_img,
                    screen = InventorySellActivity::class.java
                ),
                GenericModelClass(
                    title = "Purchase",
                    icon = R.drawable.purchase_img,
                    screen = InventoryPurchaseActivity::class.java
                ),
                GenericModelClass(
                    title = "Stock",
                    icon = R.drawable.stock_img,
                    screen = InventoryStockActivity::class.java
                ),
                GenericModelClass(
                    title = "Sales Report",
                    icon = R.drawable.sell_report_img,
                    screen = InventorySalesReportActivity::class.java
                ),
             /*   GenericModelClass(
                    title = "Purchase Report",
                    icon = R.drawable.sell_report_img,
                    screen = InventoryPurchaseReportActivity::class.java
                ),*/
                GenericModelClass(
                    title = "Sales Order",
                    icon = R.drawable.sales_report_img,
                    screen = InventorySaleOrderActivity::class.java
                ),
                GenericModelClass(
                    title = "Profit Report",
                    icon = R.drawable.sales_report_img,
                    screen = InventoryProfitRateActivity::class.java
                )
            )
        )
    }

}