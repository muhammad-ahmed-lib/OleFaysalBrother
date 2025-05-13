package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ae.oleapp.R
import ae.oleapp.databinding.ActivityInventoryPurchaseBinding
import ae.oleapp.databinding.ActivityInventorySellBinding
import ae.oleapp.databinding.InventoryPurchaseRecItemBinding
import ae.oleapp.databinding.InventorySellRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.utils.loadImage
import ae.oleapp.utils.openActivity

class InventoryPurchaseActivity : AppCompatActivity() {
    private val list by lazy { ArrayList<InventorySellModelClass>() }

    private lateinit var binding: ActivityInventoryPurchaseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryPurchaseBinding.inflate(layoutInflater)
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
            bindingInflater = InventoryPurchaseRecItemBinding::inflate
        ) { binding, user, _ ->
            binding.titleTv.text = user.title
            binding.amountTv.text = user.amount
            binding.stockTv.text = user.stock
            binding.icon.loadImage(user.icon)
            binding.root.setOnClickListener {
                openActivity<InventoryAddItemActivity>()
            }

        }
        binding.addItemBtn.setOnClickListener {
            openActivity<InventoryAddItemActivity>()
        }

        binding.moneyRec.adapter = adapter

    }
}