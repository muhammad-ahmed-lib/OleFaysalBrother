package ae.oleapp.presentation.ui.sms

import ae.oleapp.R
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.PlayerCountData
import ae.oleapp.abstraction.models.SmsRequest
import ae.oleapp.abstraction.repository.SmsRepository
import ae.oleapp.databinding.ActivitySmsDetailsBuyBinding
import ae.oleapp.databinding.MessagesRecItemBinding
import ae.oleapp.employee.data.bindingAdapters.KProgressBindingAdapter
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.viewmodels.SmsViewModel
import ae.oleapp.presentation.viewmodels.SmsViewModelFactory
import ae.oleapp.utils.SmsCalculationResult
import ae.oleapp.utils.SmsCalculator
import ae.oleapp.utils.currentFormattedDate
import ae.oleapp.utils.currentFormattedTime
import ae.oleapp.utils.loadImage
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SmsDetailsBuyActivity : AppCompatActivity() {

    private val playerList by lazy { ArrayList<PlayerCountData>() }
    private lateinit var binding: ActivitySmsDetailsBuyBinding
    private lateinit var viewModel: SmsViewModel
    private lateinit var recAdapter: GenericAdapter<PlayerCountData, MessagesRecItemBinding>

    private val TAG = "SmsDetailsBuyActivity"
    private var recipientCount = 0
    private var userAvailableSms = 0
    private var smsCost = 0.2
    private var selectedType = "All Payers" // Default type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmsDetailsBuyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViewModel()
        setupUI()
        observeData()
    }

    private fun initializeViewModel() {
        val repository = SmsRepository(this)
        viewModel = ViewModelProvider(
            this,
            SmsViewModelFactory(repository)
        )[SmsViewModel::class.java]
        viewModel.fetchPlayersCount()
    }

    private fun setupUI() {
        // Initialize with intent extras
        userAvailableSms = intent.getIntExtra("userTotalSms", 0)
        smsCost = intent.getDoubleExtra("smsCost", 0.2)

        binding.dateTv.currentFormattedDate = ""
        binding.timeTv.currentFormattedTime = ""

        setupSmsTextWatcher()
        setupClickListeners()
        updateInitialUI()
    }

    private fun setupSmsTextWatcher() {
        binding.messageEd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let { message ->
                    val result = calculateSmsDetails(message.toString())
                    handleSmsLimit(result)
                    updateSmsCalculationUI(result)
                }
            }
        })
    }


    private fun calculateSmsDetails(message: String): SmsCalculationResult {
        return SmsCalculator.calculateSmsDetails(
            message,
            recipientCount,
            smsCost,
            userAvailableSms
        )
    }

    private fun handleSmsLimit(result: SmsCalculationResult) {
        if (!result.hasEnoughSms && binding.messageEd.text?.isNotEmpty() == true) {
            Toast.makeText(
                this,
                "You don't have enough SMS credits. Please buy more.",
                Toast.LENGTH_SHORT
            ).show()

            val message = binding.messageEd.text.toString()
            if (message.isNotEmpty()) {
                // Remove last char safely
                binding.messageEd.setText(message.dropLast(1))
                binding.messageEd.setSelection(binding.messageEd.text?.length ?: 0)
            }
        }
    }


    private fun updateInitialUI() {
        val result = calculateSmsDetails("")
        updateSmsCalculationUI(result)
    }

    private fun updateSmsCalculationUI(result: SmsCalculationResult) {
        binding.apply {
            totalSmsTv.text = "${result.totalSms} SMS"
            totalAmountTv.text = "%.2f AED".format(result.totalCost)

            // Update button state based on SMS availability
            btnSend.isEnabled = result.hasEnoughSms && recipientCount > 0
            sendReTV.text = when {
                !result.hasEnoughSms -> "Not Enough SMS Credits"
                recipientCount == 0 -> "Select Recipients"
                else -> getString(R.string.send_request)
            }
        }
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener { finish() }

        binding.btnSend.setOnClickListener {
            val message = binding.messageEd.text.toString()
            if (validateInput(message)) {
                sendSmsRequest(message)
            }
        }
    }

    private fun validateInput(message: String): Boolean {
        return when {
            message.isEmpty() -> {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
                false
            }

            recipientCount == 0 -> {
                Toast.makeText(this, "Please select recipients", Toast.LENGTH_SHORT).show()
                false
            }

            else -> true
        }
    }

    private fun sendSmsRequest(message: String) {
        val result = calculateSmsDetails(message)
        if (!result.hasEnoughSms) {
            Toast.makeText(this, "Not enough SMS credits", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("hh:mma", Locale.getDefault()).format(Date())

        val smsRequest = SmsRequest(
            clubId = "1", // Replace with actual club ID
            totalSms = result.totalSms,
            type = selectedType,
            amount = result.totalCost,
            smsDate = currentDate,
            smsTime = currentTime,
            smsText = message
        )

        val staticRequest = SmsRequest(
            clubId = "1",
            totalSms = 120,
            type = "All Payers",
            amount = 60.0,
            smsDate = "2025-12-01",
            smsTime = "08:30:00",
            smsText = "Fantastic work, team! You really gave it your all well done!"
        )
        viewModel.sendSmsRequest(smsRequest)
    }

    private fun observeData() {
        viewModel.playerCount.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> Log.d(TAG, "Loading player counts")
                is ApiResponse.Success -> handlePlayerCountSuccess(response.data?.data)
                is ApiResponse.Error -> showError(response.error.toString())
            }
        }

        viewModel.smsResponse.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {

                }

                is ApiResponse.Success -> {

                    handleSmsSendSuccess()
                }

                is ApiResponse.Error -> {
                    handleSmsSendError(response.error)
                }
            }
        }
    }

    private fun handlePlayerCountSuccess(data: List<PlayerCountData>?) {
        data?.let {
            playerList.clear()
            playerList.addAll(it)
            setupRecipientRecyclerView()

            // Auto-select first item if available
            if (playerList.isNotEmpty()) {
                updateRecipientSelection(playerList[0])
            }
        }
    }

    private fun setupRecipientRecyclerView() {
        recAdapter = GenericAdapter(
            items = playerList,
            bindingInflater = MessagesRecItemBinding::inflate
        ) { binding, item, _ ->
            binding.titleTv.text = item.type
            binding.stockTv.text = item.count.toString()

            // Highlight selected item
            if (item.type == selectedType)
                binding.radioBtn.loadImage(R.drawable.green_bg_circular_shape)
            else
                binding.radioBtn.loadImage(R.drawable.sms_radio_btn)
            binding.root.setOnClickListener {
                updateRecipientSelection(item)
            }
        }
        binding.rec.adapter = recAdapter
    }

    private fun updateRecipientSelection(item: PlayerCountData) {
        selectedType = item.type
        recipientCount = item.count
        recAdapter.notifyDataSetChanged()

        val currentText = binding.messageEd.text.toString()
        val result = calculateSmsDetails(currentText)
        updateSmsCalculationUI(result)
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.btnSend.isEnabled = !isLoading
        binding.sendReTV.text = if (isLoading) "Sending..." else getString(R.string.send_request)
    }

    private fun handleSmsSendSuccess() {
        Toast.makeText(this, "SMS sent successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun handleSmsSendError(error: String?) {
        setLoadingState(false)
        showError(error ?: "Failed to send SMS")
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}