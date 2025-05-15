package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.InventoryStockData
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.ActivityInventoryPurchaseBinding
import ae.oleapp.databinding.ActivityInventoryStockBinding
import ae.oleapp.databinding.InventoryPurchaseRecItemBinding
import ae.oleapp.databinding.InventorySellRecItemBinding
import ae.oleapp.databinding.InventoryStockRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.loadImage
import ae.oleapp.utils.openActivity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class InventoryStockActivity : AppCompatActivity() {
    private val list by lazy { ArrayList<InventoryStockData>() }
    private lateinit var viewModel: InventoryViewModel
    private lateinit var binding: ActivityInventoryStockBinding
    private val TAG="InventoryStockActivityInfo"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryStockBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
        viewModel.inventoryStockResponse.observe(this) { response ->
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
            bindingInflater = InventoryStockRecItemBinding::inflate
        ) { binding, user, _ ->
            user.inventory.firstOrNull().let { item ->
                binding.titleTv.text = item?.name ?: "No Name"
                binding.stockTv.text = "Stock: ${item?.current_stock ?: 0}"
                binding.root.setOnClickListener {
                    // From your Activity or Fragment:
                    val productId =item?.id.toString()
                    val bottomSheet = AddStockBottomSheetFragment.newInstance(productId, onDone = {
                        if (it){
                            viewModel.getInventoryStockReport()
                            observeSalesData()
                        }
                    })
                    bottomSheet.show(supportFragmentManager, "AddStockBottomSheet")
                }
            } ?: run {
                binding.titleTv.text = "No Item"
                binding.stockTv.text = "Stock: 0"
            }
        }
        binding.moneyRec.adapter = adapter
    }

    override fun onResume() {
        super.onResume()


    }
}