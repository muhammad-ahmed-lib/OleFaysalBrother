package ae.oleapp.presentation.ui

import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.GenericModelClass
import ae.oleapp.abstraction.models.InventorySummary
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.ActivityStatictsBinding
import ae.oleapp.databinding.StatisClubRecItemBinding
import ae.oleapp.databinding.StatisMonthRecItemBinding
import ae.oleapp.databinding.StatisYearRecItemBinding
import ae.oleapp.databinding.StatisticsRecItemBinding
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.ui.inventory.InventoryMoneyModelClass
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.loadImage
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

class StatictsActivity : AppCompatActivity() {
    private val monthList by lazy { ArrayList<GenericModelClass>() }
    private val yearList by lazy { ArrayList<GenericModelClass>() }
    private val clubList by lazy { ArrayList<GenericModelClass>() }
    private val matchesList by lazy { ArrayList<InventoryMoneyModelClass>() }
    private lateinit var binding: ActivityStatictsBinding
    private lateinit var viewModel: InventoryViewModel
    private lateinit var monthRecAdapter: GenericAdapter<GenericModelClass, StatisMonthRecItemBinding>
    private lateinit var yearRecAdapter: GenericAdapter<GenericModelClass,  StatisYearRecItemBinding>
    private lateinit var clubRecAdapter: GenericAdapter<GenericModelClass, StatisClubRecItemBinding>
    private lateinit var matchesRecAdapter: GenericAdapter<InventoryMoneyModelClass, StatisticsRecItemBinding>
    private val TAG = "inventoryInfo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStatictsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = InventoryRepository(this)
        viewModel = ViewModelProvider(
            this,
            InventoryViewModelFactory(repository)
        )[InventoryViewModel::class.java]

        setupAdapters()
        observeSalesData()
        setupClickListeners()
    }

    private fun setupAdapters() {
        // Initialize money recycler adapter

    }

    private fun observeSalesData() {
        viewModel.homePageSummary.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    Log.d(TAG, "observeSalesData: Loading")
                }

                is ApiResponse.Success -> {
                    response.data?.let { summary ->
                        updateMoneyData(summary)
                    }
                }

                is ApiResponse.Error -> {
                    Toast.makeText(this, response.error ?: "Error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun updateMoneyData(summary: InventorySummary) {


    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        clubRec()
        monthRec()
        yearRec()
        matchesRec()
        cancelLineChart()
        bookingLineChart()
    }

    private fun matchesRec() {
        matchesList.clear()
        // Ad sales data
        matchesList.add(
            InventoryMoneyModelClass(
                title = "Total Matches",
                amount = "325",
                totalPercentage = "",

                icon = R.drawable.stroke_circle_white
            )
        )
        matchesList.add(
            InventoryMoneyModelClass(
                title = "Canceled Matches",
                amount = "325",
                totalPercentage = "",

                icon = R.drawable.stroke_circle_white
            )
        )
        matchesList.add(
            InventoryMoneyModelClass(
                title = "Booking Matches",
                amount = "325",
                totalPercentage = "",

                icon = R.drawable.yellow_football_profile
            )
        )
        matchesList.add(
            InventoryMoneyModelClass(
                title = "Completed Matches",
                amount = "325",
                totalPercentage = "",

                icon = R.drawable.stroke_circle_white
            )
        )


        matchesRecAdapter = GenericAdapter(
            items = matchesList,
            bindingInflater = StatisticsRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text = item.amount
            binding.dscTv.text = item.title
            binding.radioBtn.loadImage(item.icon)
        }
        binding.matchesRec.adapter = matchesRecAdapter
    }

    private fun monthRec() {
        var selectedItem = "Jan"
        monthList.clear()
        // Ad sales data
        monthList.add(
            GenericModelClass(
                title = "Jan",
                icon = R.drawable.total_sell_img
            )
        )
        monthList.add(
            GenericModelClass(
                title = "Feb",
                icon = R.drawable.total_sell_img
            )
        )
        monthList.add(
            GenericModelClass(
                title = "Mar",
                icon = R.drawable.total_sell_img
            )
        )
        monthList.add(
            GenericModelClass(
                title = "Apr",
                icon = R.drawable.total_sell_img
            )
        )

        monthRecAdapter = GenericAdapter(
            items = monthList,
            bindingInflater = StatisMonthRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text = item.title
            if (selectedItem == item.title) {
                binding.titleTv.setBackgroundResource(R.drawable.statis_selected_rec_item)
            } else {
                binding.titleTv.setBackgroundResource(R.drawable.statis_unselected_rec_item)
            }
        }
        binding.monthRec.adapter = monthRecAdapter
    }
    private fun cancelLineChart(){
        val lineChart=binding.cancelLineChart as LineChart
// Sample data - replace with your actual data
        val entries = listOf(
            Entry(0f, 4f),  // Day 1
            Entry(1f, 8f),  // Day 2
            Entry(2f, 6f),  // Day 3
            Entry(3f, 2f),  // Day 4
            Entry(4f, 7f),  // Day 5
            Entry(5f, 5f)   // Day 6
        )
// Create dataset with cubic line configuration
        val dataSet = LineDataSet(entries, "Performance Metrics").apply {
            color = Color.parseColor("#4CAF50")  // Green color
            valueTextColor = Color.BLACK
            lineWidth = 3f
            setDrawCircles(true)
            circleRadius = 5f
            circleHoleRadius = 3f
            setCircleColor(Color.parseColor("#FBE14C"))
            setDrawValues(false)  // Hide values on points

            // Cubic line configuration
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f  // Adjust curve intensity (0-1)

            // Area fill configuration
            fillDrawable = ContextCompat.getDrawable(this@StatictsActivity, R.drawable.cancel_line_chart_bg)
            setDrawFilled(true)
            fillAlpha = 100  // Transparency (0-255)
        }



// Create line data
        val lineData = LineData(dataSet)

// Configure X axis
        lineChart.xAxis.apply {

          //  position = XAxix.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            textColor = Color.BLACK
            axisLineColor = Color.parseColor("#BDBDBD")
            granularity = 1f  // Interval between labels
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value.toInt()) {
                        0 -> "Mon"
                        1 -> "Tue"
                        2 -> "Wed"
                        3 -> "Thu"
                        4 -> "Fri"
                        5 -> "Sat"
                        else -> ""
                    }
                }
            }
        }

// Configure left Y axis
        lineChart.axisLeft.apply {
            textColor = Color.BLACK
            axisLineColor = Color.parseColor("#BDBDBD")
            setDrawGridLines(true)
            gridColor = Color.parseColor("#EEEEEE")
            axisMinimum = 0f  // Start from 0
            granularity = 1f  // Interval between grid lines
        }

// Disable right Y axis
        lineChart.axisRight.isEnabled = false

// Configure chart appearance
        lineChart.apply {
            data = lineData
            description.isEnabled = false
            legend.isEnabled = true
            legend.textColor = Color.BLACK
            legend.formSize = 12f
            legend.form = Legend.LegendForm.LINE

            // Touch and zoom configuration
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            setDrawBorders(false)

            // Animation
            animateY(1500, Easing.EaseInOutCubic)

            // Extra styling
            setNoDataText("No data available")
            setNoDataTextColor(Color.GRAY)
            setDrawMarkers(true)
        }

// Refresh chart
        lineChart.invalidate()
    }
    private fun bookingLineChart(){
        val lineChart=binding.bookingLineChart as LineChart
// Sample data - replace with your actual data
        val entries = listOf(
            Entry(0f, 4f),  // Day 1
            Entry(1f, 8f),  // Day 2
            Entry(2f, 6f),  // Day 3
            Entry(3f, 2f),  // Day 4
            Entry(4f, 7f),  // Day 5
            Entry(5f, 5f)   // Day 6
        )
// Create dataset with cubic line configuration
        val dataSet = LineDataSet(entries, "Booking Metrics").apply {
            color = Color.parseColor("#E62348")  // Green color
            valueTextColor = Color.BLACK
            lineWidth = 3f
            setDrawCircles(true)
            circleRadius = 5f
            circleHoleRadius = 3f
            setCircleColor(Color.parseColor("#E62348"))
            setDrawValues(false)  // Hide values on points

            // Cubic line configuration
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f  // Adjust curve intensity (0-1)

            // Area fill configuration
            fillDrawable = ContextCompat.getDrawable(this@StatictsActivity, R.drawable.booking_line_chart_bg)
            setDrawFilled(true)
            fillAlpha = 100  // Transparency (0-255)
        }



// Create line data
        val lineData = LineData(dataSet)

// Configure X axis
        lineChart.xAxis.apply {

            //  position = XAxix.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            textColor = Color.BLACK
            axisLineColor = Color.parseColor("#BDBDBD")
            granularity = 1f  // Interval between labels
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return when (value.toInt()) {
                        0 -> "Mon"
                        1 -> "Tue"
                        2 -> "Wed"
                        3 -> "Thu"
                        4 -> "Fri"
                        5 -> "Sat"
                        else -> ""
                    }
                }
            }
        }

// Configure left Y axis
        lineChart.axisLeft.apply {
            textColor = Color.BLACK
            axisLineColor = Color.parseColor("#BDBDBD")
            setDrawGridLines(true)
            gridColor = Color.parseColor("#EEEEEE")
            axisMinimum = 0f  // Start from 0
            granularity = 1f  // Interval between grid lines
        }

// Disable right Y axis
        lineChart.axisRight.isEnabled = false

// Configure chart appearance
        lineChart.apply {
            data = lineData
            description.isEnabled = false
            legend.isEnabled = true
            legend.textColor = Color.BLACK
            legend.formSize = 12f
            legend.form = Legend.LegendForm.LINE

            // Touch and zoom configuration
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            setDrawBorders(false)

            // Animation
            animateY(1500, Easing.EaseInOutCubic)

            // Extra styling
            setNoDataText("No data available")
            setNoDataTextColor(Color.GRAY)
            setDrawMarkers(true)
        }

// Refresh chart
        lineChart.invalidate()
    }

    private fun yearRec() {
        var selectedItem = "2021"
        yearList.clear()
        // Ad sales data
        yearList.add(
            GenericModelClass(
                title = "2021",
                icon = R.drawable.total_sell_img
            )
        )
        yearList.add(
            GenericModelClass(
                title = "2022",
                icon = R.drawable.total_sell_img
            )
        )
        yearList.add(
            GenericModelClass(
                title = "2023",
                icon = R.drawable.total_sell_img
            )
        )
        yearList.add(
            GenericModelClass(
                title = "2024",
                icon = R.drawable.total_sell_img
            )
        )

        yearRecAdapter = GenericAdapter(
            items = yearList,
            bindingInflater = StatisYearRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text = item.title
            if (selectedItem == item.title) {
                binding.titleTv.setBackgroundResource(R.drawable.statis_selected_rec_item)
            } else {
                binding.titleTv.setBackgroundResource(R.drawable.statis_unselected_rec_item)
            }
        }
        binding.yearRec.adapter = yearRecAdapter
    }

    private fun clubRec() {
        var selectedItem = "Al Tahadi Club"
        clubList.clear()
        // Ad sales data
        clubList.add(
            GenericModelClass(
                title = "Al Tahadi Club",
                icon = R.drawable.total_sell_img
            )
        )
        clubList.add(
            GenericModelClass(
                title = "Al Forsan",
                icon = R.drawable.total_sell_img
            )
        )
        clubList.add(
            GenericModelClass(
                title = "Al Tahadhi Club",
                icon = R.drawable.total_sell_img
            )
        )

        clubRecAdapter = GenericAdapter(
            items = clubList,
            bindingInflater = StatisClubRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text = item.title
            if (selectedItem == item.title) {
                binding.titleTv.setBackgroundResource(R.drawable.statis_selected_rec_item)
            } else {
                binding.titleTv.setBackgroundResource(R.drawable.statis_unselected_rec_item)
            }
        }
        binding.clubRec.adapter = clubRecAdapter
    }

}