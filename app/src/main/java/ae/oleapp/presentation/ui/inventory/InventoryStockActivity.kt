package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ae.oleapp.R
import ae.oleapp.databinding.ActivityInventoryPurchaseBinding
import ae.oleapp.databinding.ActivityInventoryStockBinding
import ae.oleapp.databinding.InventoryPurchaseRecItemBinding
import ae.oleapp.databinding.InventorySellRecItemBinding
import ae.oleapp.databinding.InventoryStockRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.utils.loadImage
import ae.oleapp.utils.openActivity

class InventoryStockActivity : AppCompatActivity() {
    private val list by lazy { ArrayList<InventorySellModelClass>() }

    private lateinit var binding: ActivityInventoryStockBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryStockBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()

        list.clear()

        list.add(
            InventorySellModelClass(
                title = "Water Pack",
                amount = "AED 349",
                stock = "Stock 342",
                icon = R.drawable.sale_inventory
            )
        )
        list.add(
            InventorySellModelClass(
                title = "Water Pack",
                amount = "AED 349",
                stock = "Stock 342",
                icon = R.drawable.sale_inventory
            )
        )
        list.add(
            InventorySellModelClass(
                title = "Water Pack",
                amount = "AED 349",
                stock = "Stock 342",
                icon = R.drawable.sale_inventory
            )
        )

        val adapter = GenericAdapter(
            items = list,
            bindingInflater = InventoryStockRecItemBinding::inflate
        ) { binding, user, _ ->
            binding.titleTv.text = user.title
            binding.stockTv.text = user.stock
            binding.root.setOnClickListener {
                AddStockBottomSheetFragment().show(supportFragmentManager,"")
            }

        }
        binding.moneyRec.adapter = adapter

    }
}