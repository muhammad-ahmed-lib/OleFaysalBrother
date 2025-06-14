package ae.oleapp.presentation.ui.sms

import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.GenericModelClass
import ae.oleapp.abstraction.models.SmsData
import ae.oleapp.abstraction.repository.SmsRepository
import ae.oleapp.databinding.ActivitySmsModuleBinding
import ae.oleapp.databinding.SmsHomeRecItemBinding
import ae.oleapp.databinding.SmsTopRecItemBinding
import ae.oleapp.presentation.ui.BuySmsDialogFragment
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.viewmodels.SmsViewModel
import ae.oleapp.presentation.viewmodels.SmsViewModelFactory
import ae.oleapp.utils.InsetsWithKeyboardCallback
import ae.oleapp.utils.openActivity
import ae.oleapp.utils.showKProgress
import ae.oleapp.utils.showToast
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider

class SmsModuleActivity : AppCompatActivity() {
    private val topRecList by lazy { ArrayList<GenericModelClass>() }
    private val list by lazy { ArrayList<SmsData>() }
    private lateinit var topRecAdapter: GenericAdapter<GenericModelClass,SmsTopRecItemBinding>
    private lateinit var adapter: GenericAdapter<SmsData, SmsHomeRecItemBinding>
    private lateinit var binding: ActivitySmsModuleBinding
    private lateinit var viewModel: SmsViewModel

    private val EXTRA_USER_TOTAL_SMS = "userTotalSms"
    private val EXTRA_SMS_COST = "smsCost"
    private val TAG = "SmsModuleActivityInfo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmsModuleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val insetsWithKeyboardCallback = InsetsWithKeyboardCallback(window)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, insetsWithKeyboardCallback)
        ViewCompat.setWindowInsetsAnimationCallback(binding.main, insetsWithKeyboardCallback)
        val repository = SmsRepository(this)
        viewModel = ViewModelProvider(
            this,
            SmsViewModelFactory(repository)
        )[SmsViewModel::class.java]

        observeSalesData()

        binding.backBtn.setOnClickListener {
            finish()
        }

         binding.deliveredSmsBtn.setOnClickListener {
             openActivity<DeliveredSmsActivity>()
        }

        viewModel.fetchHomeSms(1)


    }

    private fun observeSalesData() {
        viewModel.smsHomeResponse.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    binding.root.showKProgress(true)
                    Log.d(TAG, "observeSalesData: Loading")
                }
                is ApiResponse.Success -> {
                    binding.root.showKProgress(false)
                    response.data?.let { summary ->
                        binding.addItemBtn.setOnClickListener {
                            val i=Intent(this,SmsDetailsBuyActivity::class.java)
                            i.putExtra(EXTRA_USER_TOTAL_SMS,summary.remaining)
                            i.putExtra(EXTRA_SMS_COST,summary.smsCost)
                            startActivity(i)
                        }
                        list.clear()
                        list.addAll(summary.data)
                        binding.availableMessagesTv.text=summary.totalPurchased.toString()
                        topRecList.add(GenericModelClass(title = "Used", icon = summary.totalUsed))
                        topRecList.add(GenericModelClass(title = "Remaining", icon = summary.remaining))
                        homeRec()
                        topRec()
                        binding.topRecCard.setOnClickListener {
                            val bottomSheet = BuySmsDialogFragment.newInstance("1"
                                ,cost=summary.smsCost,  onPurchaseComplete= {
                                if (it){
                                    if (::adapter.isInitialized){
                                        adapter.notifyDataSetChanged()
                                    }
                                    if (::topRecAdapter.isInitialized){
                                        topRecAdapter.notifyDataSetChanged()
                                    }
                                    viewModel.fetchHomeSms(1)
                                    showToast("Purchased Successfully")
                                }
                            })
                            bottomSheet.show(supportFragmentManager, "AddStockBottomSheet")
                        }
                    }
                }
                is ApiResponse.Error -> {
                    binding.root.showKProgress(false)
                    Toast.makeText(this, response.error ?: "Error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun topRec() {
        topRecAdapter = GenericAdapter(
            items = topRecList,
            bindingInflater = SmsTopRecItemBinding::inflate
        ) { binding, item, position ->
            binding.titleTv.text = item.title
            binding.amountTv.text = item.icon.toString()
        }
        binding.topRec.adapter = topRecAdapter

    }

    @SuppressLint("SetTextI18n")
    private fun homeRec() {
        adapter = GenericAdapter(
            items = list,
            bindingInflater = SmsHomeRecItemBinding::inflate
        ) { binding, item, position ->
            binding.titleTv.text = "Total: ${item.totalSms} SMS"
            binding.amountTv.text = "AED ${item.amount}"
            binding.status.text = item.status
            if (item.status=="PENDING"){
                binding.status.setTextColor(getColor(R.color.messageProgressYellowColor))
            }else{
                binding.status.setTextColor(getColor(R.color.v5greenColor))
            }
        }
        binding.rec.adapter = adapter
    }

}