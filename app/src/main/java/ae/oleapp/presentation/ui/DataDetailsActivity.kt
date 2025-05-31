package ae.oleapp.presentation.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.GenericModelClass
import ae.oleapp.abstraction.models.InventorySummary
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.ActivityDataDetailsBinding
import ae.oleapp.databinding.ActivityStatictsBinding
import ae.oleapp.databinding.DataDetailsRecItemBinding
import ae.oleapp.databinding.StatisClubRecItemBinding
import ae.oleapp.databinding.StatisMonthRecItemBinding
import ae.oleapp.databinding.StatisYearRecItemBinding
import ae.oleapp.databinding.StatisticsRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.ui.inventory.InventoryMoneyModelClass
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.loadImage
import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.max

class DataDetailsActivity : AppCompatActivity() {
    private val countryList by lazy { ArrayList<GenericModelClass>() }
    private val userList by lazy { ArrayList<InventoryMoneyModelClass>() }
    private lateinit var binding: ActivityDataDetailsBinding
    private lateinit var viewModel: InventoryViewModel
    private lateinit var countryAdapter: GenericAdapter<GenericModelClass, StatisMonthRecItemBinding>
    private lateinit var userRecAdapter: GenericAdapter<InventoryMoneyModelClass, DataDetailsRecItemBinding>
    private val TAG = "DataDetailsActivityInfo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDataDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = InventoryRepository(this)
        viewModel = ViewModelProvider(
            this,
            InventoryViewModelFactory(repository)
        )[InventoryViewModel::class.java]

        observeSalesData()
        setupClickListeners()
    }


    private fun observeSalesData() {

    }



    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.titleTv.setOnClickListener {
            AlerSideMenuDialogFragment().show(supportFragmentManager,"")
        }
    }

    override fun onResume() {
        super.onResume()
        userRec()
        countryRec()
    }

    private fun userRec() {
        userList.clear()
        // Ad sales data
        userList.add(
            InventoryMoneyModelClass(
                title = "Ahmed",
                amount = "14 Jan, 2022",
                totalPercentage = "United Arab Emirates",
                icon = R.drawable.flag_uae
            )
        )

        userRecAdapter = GenericAdapter(
            items = userList,
            bindingInflater = DataDetailsRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.userNameTv.text = item.title
            binding.joinedDateTv.text ="Joined: ${item.amount}"
            binding.countryNameTv.text = item.title
            binding.countryFlagImg.loadImage(item.icon)
        }
        binding.userRec.adapter = userRecAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun countryRec() {
        var selectedItem = "234"
        countryList.clear()
        // Ad sales data
        countryList.add(
            GenericModelClass(
                title = "UAE",
                icon = 234
            )
        )
        countryList.add(
            GenericModelClass(
                title = "Saudi",
                icon = 123
            )
        )
        countryList.add(
            GenericModelClass(
                title = "PAK",
                icon = 92
            )
        )


        countryAdapter = GenericAdapter(
            items = countryList,
            bindingInflater = StatisMonthRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text = item.title+" (${item.icon})"
            if (selectedItem == item.title) {
                binding.titleTv.setBackgroundResource(R.drawable.statis_selected_rec_item)
            } else {
                binding.titleTv.setBackgroundResource(R.drawable.statis_unselected_rec_item)
            }
        }
        binding.countryRec.adapter = countryAdapter
    }


}