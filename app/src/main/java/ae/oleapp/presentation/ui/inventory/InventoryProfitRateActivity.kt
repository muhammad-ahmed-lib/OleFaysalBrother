package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.GenericModelClass
import ae.oleapp.abstraction.models.ProfitReport
import ae.oleapp.abstraction.models.SaleReportData
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.ActivityInventoruSaleOrderBinding
import ae.oleapp.databinding.ActivityInventoryProfitRateBinding
import ae.oleapp.databinding.InventorySalesOrderRecItemBinding
import ae.oleapp.databinding.ProfiteDiscountRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.loadImage
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
                    Log.d(TAG, "observeSalesData: Loading")
                }

                is ApiResponse.Success -> {
                    response.data?.let { summary ->
                        list.clear()
                        list.addAll(summary)
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
            bindingInflater = InventorySalesOrderRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.amountTv.text=item.selling_price.toString()
            binding.orderByNameTv.text=item.name
            binding.quantityTv.text="Qty: ${item.quantity_sold}"
            binding.icon.loadImage(R.drawable.water_pack_img)
        }

        binding.rec.adapter = adapter
    }
    override fun onResume() {
        super.onResume()
        profitDisList.clear()

        profitDisList.add(
            InventorySellOrderModelClass(
                orderNo = "Profit",
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