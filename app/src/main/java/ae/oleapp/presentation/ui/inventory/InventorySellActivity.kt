package ae.oleapp.presentation.ui.inventory

import ae.oleapp.R
import ae.oleapp.databinding.ActivityInventorySellBinding
import ae.oleapp.databinding.InventorySellRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.utils.loadImage
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


data class InventorySellModelClass(
    val title: String,
    val stock: String,
    val amount: String,
    val icon: Int,
    var count: Int=0
)

class InventorySellActivity : AppCompatActivity() {
    private val list by lazy { ArrayList<InventorySellModelClass>() }
    private lateinit var adapter: GenericAdapter<InventorySellModelClass, InventorySellRecItemBinding>
    private lateinit var binding: ActivityInventorySellBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventorySellBinding.inflate(layoutInflater)
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

        adapter = GenericAdapter(
            items = list,
            bindingInflater = InventorySellRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text = item.title
            binding.amountTv.text = item.amount
            binding.stockTv.text = item.stock
            binding.counterTv.text = item.count.toString()
            binding.icon.loadImage(item.icon)

            binding.plusBtn.setOnClickListener {
                item.count++
                binding.counterTv.text = item.count.toString()
            }

            binding.minosBtn.setOnClickListener {
                if (item.count > 0) {
                    item.count--
                    binding.counterTv.text = item.count.toString()
                }
            }
            binding.icon.loadImage(item.icon)
        }

        binding.moneyRec.adapter = adapter

    }
}