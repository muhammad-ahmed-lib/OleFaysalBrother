package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ae.oleapp.R
import ae.oleapp.databinding.ActivityInventoruSaleOrderBinding
import ae.oleapp.databinding.ActivityInventoryPurchaseBinding
import ae.oleapp.databinding.InventoryPurchaseRecItemBinding
import ae.oleapp.databinding.InventorySalesOrderRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.utils.loadImage

data class InventorySellOrderModelClass(
    val orderNo: String,
    val orderedDate: String,
    val orderBy: String,
    val price: String,
    val qnt: String,
    val icon: Int
)

class InventorySaleOrderActivity : AppCompatActivity() {
    private val list by lazy { ArrayList<InventorySellOrderModelClass>() }

    private lateinit var binding: ActivityInventoruSaleOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoruSaleOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()
        list.clear()

        list.add(
            InventorySellOrderModelClass(
                orderNo = "Order Number: 234",
                price = "AED 349",
                orderedDate = "3-May-2023",
                orderBy = "Ahmed",
                qnt = "341",
                icon = R.drawable.sale_inventory
            )
        )


        list.add(
            InventorySellOrderModelClass(
                orderNo = "Order Number: 234",
                price = "AED 349",
                orderedDate = "3-May-2023",
                orderBy = "Ahmed",
                qnt = "341",
                icon = R.drawable.sale_inventory
            )
        )
        val adapter = GenericAdapter(
            items = list,
            bindingInflater = InventorySalesOrderRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.orderNoTv.text=item.orderNo
            binding.amountTv.text=item.price
            binding.orderDateTv.text=item.orderedDate
            binding.orderByNameTv.text=item.orderBy
            binding.quantityTv.text="Qty: ${item.qnt}"
            binding.icon.loadImage(item.icon)
        }

        binding.rec.adapter = adapter

    }
}