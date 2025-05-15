package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.InventoryProduct
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.ActivityInventoryPurchaseBinding
import ae.oleapp.databinding.ActivityInventorySellBinding
import ae.oleapp.databinding.InventoryPurchaseRecItemBinding
import ae.oleapp.databinding.InventorySellRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.loadImage
import ae.oleapp.utils.openActivity
import ae.oleapp.utils.putPurchaseModel
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

data class PurchasePassModelClass(
    val editId: String,
    val clubId: String?=null,
    val name: String?=null,
    val purchasePrice: String?=null,
    val salePrice: String?=null,
    val quantity: String?=null,
    val imageUrl: String?=null,
    val isUpdate: Boolean=false
)

class InventoryPurchaseActivity : AppCompatActivity() {
    private val list by lazy { ArrayList<InventoryProduct>() }
    private lateinit var adapter: GenericAdapter<InventoryProduct, InventorySellRecItemBinding>
    private lateinit var viewModel: InventoryViewModel

    private val TAG="InventoryPurchaseActivityInfo"
    private lateinit var binding: ActivityInventoryPurchaseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtn.setOnClickListener {
            finish()
        }

        val repository = InventoryRepository(this)
        viewModel = ViewModelProvider(
            this,
            InventoryViewModelFactory(repository)
        )[InventoryViewModel::class.java]


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
            bindingInflater = InventoryPurchaseRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text = item.name
            binding.amountTv.text ="AED "+item.selling_price.toString()
            binding.stockTv.text = "Stock: "+item.current_stock.toString()
            binding.icon.loadImage(item.photo)
            binding.root.setOnClickListener {
                Log.d(TAG, "rec: ${item.id}")
                val i= Intent(this,AddInventoryProductActivity::class.java)
                i.putPurchaseModel(model = PurchasePassModelClass(
                    clubId = "1",
                    name = item.name,
                    purchasePrice = item.purchase_price.toString(),
                    salePrice = item.selling_price.toString(),
                    editId = item.id.toString(),
                    imageUrl = item.photo.toString(),
                    quantity = "0",
                    isUpdate = true
                ))
                startActivity(i)
            }

        }
        binding.addItemBtn.setOnClickListener {
            val i= Intent(this,AddInventoryProductActivity::class.java)
            i.putPurchaseModel(model = PurchasePassModelClass(
                editId = "-1",
                isUpdate = false
            ))
            startActivity(i)

        }

        binding.moneyRec.adapter = adapter
    }
    override fun onResume() {
        super.onResume()
        viewModel.getInventoryProducts()
        observeSalesData()

    }
}