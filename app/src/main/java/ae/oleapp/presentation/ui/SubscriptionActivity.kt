package ae.oleapp.presentation.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ae.oleapp.R
import ae.oleapp.abstraction.models.GenericModelClass
import ae.oleapp.databinding.ActivityInventoryBinding
import ae.oleapp.databinding.ActivitySubscription2Binding
import ae.oleapp.databinding.InventoryMoneyRecItemBinding
import ae.oleapp.databinding.InventoryRecItemBinding
import ae.oleapp.databinding.SubscriptionRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.ui.inventory.InventoryMoneyModelClass
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.utils.loadImage
import ae.oleapp.utils.openActivity

data class SubModel(
    val name:String,
    val type:String,
    val price:String,
    val offer:String,
)

class SubscriptionActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySubscription2Binding.inflate(layoutInflater) }

    private val subList by lazy { ArrayList<SubModel>() }
    private lateinit var subRecAdapter: GenericAdapter<SubModel, SubscriptionRecItemBinding>
    private val TAG = "SubscriptionActivityInfo"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAdapters()

    }
    private fun setupAdapters() {
        subList.clear()
        subList.add(SubModel(
            name = "Monthly",
            type = "Include SMS",
            price = "AED 324",
            offer = "Save 20%"
        ))
        subList.add(SubModel(
            name = "Yearly",
            type = "Include SMS",
            price = "AED 324",
            offer = "Save 50%"
        ))
        subList.add(SubModel(
            name = "Weekly",
            type = "Include SMS",
            price = "AED 324",
            offer = "Save 20%"
        ))
        subList.add(SubModel(
            name = "Monthly",
            type = "Include SMS",
            price = "AED 324",
            offer = "Save 20%"
        ))
        // Initialize money recycler adapter
        subRecAdapter = GenericAdapter(
            items = subList,
            bindingInflater = SubscriptionRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.subscriptionNameTv.text = item.name
            binding.subOfferTv.text = item.offer
            binding.subscriptionPriceTv.text =item.price
            binding.smsTypeTv.text =item.type

        }
        binding.subRec.adapter = subRecAdapter

    }

}