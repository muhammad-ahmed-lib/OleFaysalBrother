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
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

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
            binding.orderNoTv.text=item.order_number
            binding.amountTv.text=item.total_amount.toString()
            binding.orderDateTv.text=item.sale_date
            binding.orderByNameTv.text=item.sold_by.toString()
            binding.quantityTv.text="Qty: 0"
            binding.icon.loadImage(R.drawable.water_pack_img)
            binding.root.setOnClickListener {
                ItemDetailsBottomSheetFragment(myId = item.id).show(supportFragmentManager, "AddStockBottomSheet")
            }
        }

        binding.rec.adapter = adapter
    }
    override fun onResume() {
        super.onResume()




    }
}