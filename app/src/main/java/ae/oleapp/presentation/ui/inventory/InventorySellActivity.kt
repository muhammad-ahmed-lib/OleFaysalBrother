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
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider


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

    private val TAG="InventorySellActivityInfo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventorySellBinding.inflate(layoutInflater)
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
        adapter = GenericAdapter(
            items = list,
            bindingInflater = InventorySellRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text = item.name
            binding.amountTv.text ="AED "+item.selling_price.toString()
            binding.stockTv.text = "Stock: "+item.current_stock.toString()
           // binding.counterTv.text = item.count.toString()
            binding.icon.loadImage(item.photo)

            binding.plusBtn.setOnClickListener {
             //   item.count++
              //  binding.counterTv.text = item.count.toString()
            }

            binding.minosBtn.setOnClickListener {
              //  if (item.count > 0) {
              //      item.count--
              //      binding.counterTv.text = item.count.toString()
               // }
            }

            binding.root.setOnClickListener {
                val i=Intent(this,AddInventoryProductActivity::class.java)
                i.putExtra("PRODUCT_ID",item.id)
                startActivity(i)
            }
        }

        binding.moneyRec.adapter = adapter

        binding.adItemBg.setOnClickListener {
            openActivity<AddInventoryProductActivity>()
        }
    }

    override fun onResume() {
        super.onResume()


    }
}