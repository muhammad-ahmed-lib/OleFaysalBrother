package ae.oleapp.presentation.ui.inventory

import ae.oleapp.R
import ae.oleapp.abstraction.models.GenericModelClass
import ae.oleapp.databinding.ActivityInventoryBinding
import ae.oleapp.databinding.InventoryMoneyRecItemBinding
import ae.oleapp.databinding.InventoryRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.utils.loadImage
import ae.oleapp.utils.openActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


data class InventoryMoneyModelClass(
    val title: String,
    val amount: String,
    val totalPercentage: String,
    val icon: Int
)

class InventoryActivity : AppCompatActivity() {
    private val moneyRecList by lazy { ArrayList<InventoryMoneyModelClass>() }
    private val list by lazy { ArrayList<GenericModelClass>() }
    private lateinit var binding: ActivityInventoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onResume() {
        super.onResume()
        moneyRecList.clear()
        list.clear()
        moneyRecList.add(
            InventoryMoneyModelClass(
                title = "Total Sell",
                icon = R.drawable.sale_inventory,
                amount = "AED 3429999",
                totalPercentage = "29 %"
            )
        )
        moneyRecList.add(
            InventoryMoneyModelClass(
                title = "Total Purchase",
                icon = R.drawable.sale_inventory,
                amount = "AED 3429999",
                totalPercentage = "29 %"
            )
        )

        val moneyRecAdapter = GenericAdapter(
            items = moneyRecList,
            bindingInflater = InventoryMoneyRecItemBinding::inflate
        ) { binding, user, _ ->
            binding.titleTv.text = user.title
            binding.amountTv.text = user.amount
            binding.totalSellPercentageTv.text = user.totalPercentage
            binding.icon.loadImage(user.icon)
        }

        binding.moneyRec.adapter = moneyRecAdapter


        list.add(
            GenericModelClass(
                title = "Sell",
                icon = R.drawable.sale_inventory,
                screen = InventorySellActivity::class.java
            )
        )
        list.add(
            GenericModelClass(
                title = "Purchase",
                icon = R.drawable.purchase_inventory,
                screen = InventoryPurchaseActivity::class.java

            )
        )
        list.add(
            GenericModelClass(
                title = "Sales Report",
                icon = R.drawable.sales_report,
                screen = InventorySalesReportActivity::class.java
            )
        )
        list.add(
            GenericModelClass(
                title = "Purchase Report",
                icon = R.drawable.sales_report
            )
        )
        list.add(
            GenericModelClass(
                title = "Stock",
                icon = R.drawable.stock_inventory,
                screen = InventoryStockActivity::class.java
            )
        )
        list.add(
            GenericModelClass(
                title = "Sales Order",
                icon = R.drawable.reports_inventory,
                screen = InventorySaleOrderActivity::class.java

            )
        )
        list.add(
            GenericModelClass(
                title = "Profit Report",
                icon = R.drawable.reports_inventory,
                screen = InventoryProfitRateActivity::class.java

            )
        )


        val adapter = GenericAdapter(
            items = list,
            bindingInflater = InventoryRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text = item.title
            binding.icon.loadImage(item.icon)
            binding.root.setOnClickListener {
                item.screen?.let { openActivity(it) }
            }
        }

        binding.rec.adapter = adapter

    }
}