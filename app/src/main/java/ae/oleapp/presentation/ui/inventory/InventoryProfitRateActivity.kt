package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.ProfitReport
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.ActivityInventoryProfitRateBinding
import ae.oleapp.databinding.InventoryProfitRateRecItemBinding
import ae.oleapp.databinding.ProfiteDiscountRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.loadImage
import ae.oleapp.utils.showKProgress
import ae.oleapp.utils.splitToDayMonthAndYear
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class InventoryProfitRateActivity : AppCompatActivity() {
    private val list by lazy { ArrayList<ProfitReport>() }
    private val profitDisList by lazy { ArrayList<InventorySellOrderModelClass>() }
    private lateinit var binding: ActivityInventoryProfitRateBinding
    private lateinit var viewModel: InventoryViewModel
    private val TAG = "InventorySalesReportActivityInfo"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryProfitRateBinding.inflate(layoutInflater)
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
        viewModel.profitReport.observe(this) { response ->
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
                        rec(summary)
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
    @SuppressLint("SetTextI18n")
    private fun rec(summary: List<ProfitReport>) {
        val adapter = GenericAdapter(
            items = list,
            bindingInflater = InventoryProfitRateRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.nameTv.text = item.name
            binding.amountTv.text = "${item.total_profit}"
            binding.soldQtTv.text = "Sold Qty: "+item.quantity_sold
            binding.salePriceTv.text = "AED "+item.selling_price
            binding.quantityTv.text = "Qty: ${item.quantity_sold}"
            binding.icon.loadImage(R.drawable.water_pack_img)
        }
        binding.rec.adapter = adapter
        profitDisList.clear()
        val firstItem=summary.firstOrNull()
        profitDisList.add(
            InventorySellOrderModelClass(
                orderNo = "Profit",
                price = "AED "+firstItem?.total_profit,
                orderedDate = null.splitToDayMonthAndYear().toString(),
                orderBy = "Ahmed",
                qnt = firstItem?.quantity_sold.toString(),
                icon = R.drawable.sale_inventory
            )
        )


        profitDisList.add(
            InventorySellOrderModelClass(
                orderNo = "Discount",
                price = "AED "+firstItem?.total_discount,
                orderedDate = null.splitToDayMonthAndYear().toString(),
                orderBy = "Ahmed",
                qnt = "341",
                icon = R.drawable.sale_inventory
            )
        )



        val adpter = GenericAdapter(
            items = profitDisList,
            bindingInflater = ProfiteDiscountRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text=item.orderNo
            binding.amountTv.text=item.price
        }

        binding.profitDiscountRec.adapter = adpter

        binding.currentAmountTv.text="AED "+firstItem?.total_sales.toString()
        try {
            val (dayMonth, year) = null.splitToDayMonthAndYear()
            binding.currentDateMonthTv.text = dayMonth
            binding.currentYearTv.text = year
        } catch (e: Exception) {
            binding.currentDateMonthTv.text = ""
            binding.currentYearTv.text = ""
        }
    }
    override fun onResume() {
        super.onResume()


    }
}