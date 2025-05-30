package ae.oleapp.presentation.ui.sms

import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.SmsUsageRecord
import ae.oleapp.abstraction.repository.SmsRepository
import ae.oleapp.databinding.ActivityDeliveredSmsBinding
import ae.oleapp.databinding.DeliveredSmsRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.viewmodels.SmsViewModel
import ae.oleapp.presentation.viewmodels.SmsViewModelFactory
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class DeliveredSmsActivity : AppCompatActivity() {
    private val list by lazy { ArrayList<SmsUsageRecord>() }
    private lateinit var binding: ActivityDeliveredSmsBinding
    private lateinit var viewModel: SmsViewModel
    private lateinit var adapter: GenericAdapter<SmsUsageRecord, DeliveredSmsRecItemBinding>
    private val TAG = "inventoryInfo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveredSmsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = SmsRepository(this)
        viewModel = ViewModelProvider(
            this,
            SmsViewModelFactory(repository)
        )[SmsViewModel::class.java]

        setupAdapters()
        observeSalesData()
        setupClickListeners()

    }

    @SuppressLint("SetTextI18n")
    private fun setupAdapters() {
        // Initialize money recycler adapter
        adapter = GenericAdapter(
            items = list,
            bindingInflater = DeliveredSmsRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.nameTv.text = item.username
            binding.idNoTv.text = item.phone
            binding.smsCountTv.text = "SMS Count: ${item.smsUsed}"
        }
        binding.moneyRec.adapter = adapter

    }

    private fun observeSalesData() {
        viewModel.fetchHomeSms(1)
        viewModel.smsResponse.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    Log.d(TAG, "observeSalesData: Loading")
                }

                is ApiResponse.Success -> {
                    response.data?.let { summary ->

                    }
                }

                is ApiResponse.Error -> {
                    Toast.makeText(this, response.error ?: "Error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

    }



}