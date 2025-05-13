package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ae.oleapp.R
import ae.oleapp.abstraction.models.GenericModelClass
import ae.oleapp.databinding.ActivityInventoruSaleOrderBinding
import ae.oleapp.databinding.ActivityInventoryProfitRateBinding
import ae.oleapp.databinding.InventorySalesOrderRecItemBinding
import ae.oleapp.databinding.ProfiteDiscountRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.utils.loadImage

class InventoryProfitRateActivity : AppCompatActivity() {
    private val list by lazy { ArrayList<InventorySellOrderModelClass>() }
    private val profitDisList by lazy { ArrayList<InventorySellOrderModelClass>() }

    private lateinit var binding: ActivityInventoryProfitRateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryProfitRateBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()
        list.clear()
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