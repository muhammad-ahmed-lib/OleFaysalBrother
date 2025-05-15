package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.SaleReportData
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.ActivityInventoryProfitRateBinding
import ae.oleapp.databinding.ActivityInventorySalesReportBinding
import ae.oleapp.databinding.InventoryMoneyRecItemBinding
import ae.oleapp.databinding.InventorySalesOrderRecItemBinding
import ae.oleapp.databinding.InventorySalesReportRecItemBinding
import ae.oleapp.databinding.ProfiteDiscountRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.loadImage
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
        viewModel.salesReportResponse.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    Log.d(TAG, "observeSalesData: Loading")
                }

                is ApiResponse.Success -> {
                    response.data?.let { summary ->
                        list.clear()
                        list.add(summary)
                        rec()
                    }
                }

                is ApiResponse.Error -> {
                    Toast.makeText(this, response.error ?: "Error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    private fun rec(){
        val adapter = GenericAdapter(
            items = list,
            bindingInflater = InventorySalesReportRecItemBinding::inflate
        ) { binding, item, _ ->
            val inventoryName = item.report?.firstOrNull()?.items?.firstOrNull()?.inventory?.name ?: "No Name"
            val q = item.report?.firstOrNull()?.items?.firstOrNull()?.inventory?.quantity ?: "0"
            val quantityText = "Qty: ${q ?: 0}"

            binding.amountTv.text = inventoryName
            binding.qtyTv.text = quantityText

            binding.icon.loadImage(R.drawable.water_pack_img)
        }

        binding.rec.adapter = adapter
    }
    override fun onResume() {
        super.onResume()
        profitDisList.clear()

        profitDisList.add(
            InventorySellOrderModelClass(
                orderNo = "Items Total",
                price = "AED 349",
                orderedDate = "3-May-2023",
                orderBy = "Ahmed",
                qnt = "341",
                icon = R.drawable.sale_inventory
            )
        )


        profitDisList.add(
            InventorySellOrderModelClass(
                orderNo = "Discount",
                price = "AED 349",
                orderedDate = "3-May-2023",
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

    }
}