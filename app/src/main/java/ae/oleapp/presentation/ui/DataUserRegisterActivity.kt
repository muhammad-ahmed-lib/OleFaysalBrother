package ae.oleapp.presentation.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ae.oleapp.R
import ae.oleapp.abstraction.models.GenericModelClass
import ae.oleapp.databinding.ActivityDataUserRegisterBinding
import ae.oleapp.databinding.ActivitySubscription2Binding
import ae.oleapp.databinding.DataUserRegisterRecItemBinding
import ae.oleapp.databinding.SubscriptionRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter

class DataUserRegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityDataUserRegisterBinding.inflate(layoutInflater) }

    private val userList by lazy { ArrayList<GenericModelClass>() }
    private lateinit var adapter: GenericAdapter<GenericModelClass, DataUserRegisterRecItemBinding>
    private val TAG = "DataUserRegisterActivityInfo"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAdapters()

        binding.backBtn.setOnClickListener {
            finish()
        }

    }
    private fun setupAdapters() {
        userList.clear()
        userList.add(GenericModelClass(
            "Completed Profile",
            302
        ))
        userList.add(GenericModelClass(
            "Active Profile",
            302
        ))
        userList.add(GenericModelClass(
            "Suspended Profile",
            302
        ))
        userList.add(GenericModelClass(
            "Registered New Users",
            302
        ))
        userList.add(GenericModelClass(
            "Completed Profile",
            302
        ))
        adapter = GenericAdapter(
            items = userList,
            bindingInflater = DataUserRegisterRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTV.text = item.title
            binding.counterTv.text = item.icon.toString()

        }
        binding.rec.adapter = adapter

    }

}