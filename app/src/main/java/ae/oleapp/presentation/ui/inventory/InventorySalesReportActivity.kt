package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ae.oleapp.R
import ae.oleapp.databinding.ActivityInventoryProfitRateBinding
import ae.oleapp.databinding.ActivityInventorySalesReportBinding
import ae.oleapp.databinding.InventorySalesOrderRecItemBinding
import ae.oleapp.databinding.InventorySalesReportRecItemBinding
import ae.oleapp.databinding.ProfiteDiscountRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.utils.loadImage

class InventorySalesReportActivity : AppCompatActivity() {
    private val list by lazy { ArrayList<InventorySellOrderModelClass>() }
    private val profitDisList by lazy { ArrayList<InventorySellOrderModelClass>() }

    private lateinit var binding: ActivityInventorySalesReportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventorySalesReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()
        list.clear()
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
            bindingInflater = InventorySalesReportRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.amountTv.text=item.price
            binding.qtyTv.text="Qty: ${item.qnt}"
            binding.icon.loadImage(item.icon)
        }

        binding.rec.adapter = adapter

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