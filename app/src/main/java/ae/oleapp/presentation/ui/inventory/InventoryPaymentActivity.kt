package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ae.oleapp.R
import ae.oleapp.abstraction.api_client.ApiClient
import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.CartItem
import ae.oleapp.abstraction.models.Employee
import ae.oleapp.abstraction.models.GenericModelClass
import ae.oleapp.abstraction.models.NewSaleResponse
import ae.oleapp.abstraction.repository.InventoryRepository
import ae.oleapp.databinding.ActivityInventoryBinding
import ae.oleapp.databinding.ActivityInventoryPaymentBinding
import ae.oleapp.databinding.InventoryMoneyRecItemBinding
import ae.oleapp.databinding.InventoryPaymentRecItemBinding
import ae.oleapp.employee.utils.gone
import ae.oleapp.employee.utils.visible
import ae.oleapp.presentation.ui.adapter.GenericAdapter
import ae.oleapp.presentation.viewmodels.InventoryViewModel
import ae.oleapp.presentation.viewmodels.InventoryViewModelFactory
import ae.oleapp.utils.TinyDB
import ae.oleapp.utils.loadImage
import ae.oleapp.utils.showKProgress
import ae.oleapp.utils.showToast
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class InventoryPaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInventoryPaymentBinding
    private var selectedPaymentMode: String? = null
    private lateinit var viewModel: InventoryViewModel
    private val TAG="InventoryPaymentActivityInfo"
    private lateinit var selectedItems: List<CartItem>
    private var totalAmount = 0.0
    private var itemsTotal = 0
    private var discount = 0.0
    private val itemList by lazy { ArrayList<CartItemModel>() }
    private lateinit var adapter: GenericAdapter<CartItemModel, InventoryPaymentRecItemBinding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = InventoryRepository(this)
        viewModel = ViewModelProvider(
            this,
            InventoryViewModelFactory(repository)
        )[InventoryViewModel::class.java]
        val cashCard = binding.cashCardBtn
        val posCard = binding.posCardBtn
        val staffCard = binding.staffCardBtn

        totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0)
        itemsTotal = intent.getIntExtra("ITEMS_TOTAL", 0)
        discount = intent.getDoubleExtra("DISCOUNT", 0.0)
        selectedItems = intent.getSerializableExtra("SELECTED_ITEMS") as? ArrayList<CartItem> ?: emptyList()
        binding.totalAmountTv.text=totalAmount.toString()
        binding.itemTotalTv.text=itemsTotal.toString()
        binding.grandTotal.text=discount.toString()
        if (selectedItems.isNotEmpty()) {
            adapter = GenericAdapter(
                items = itemList,
                bindingInflater = InventoryPaymentRecItemBinding::inflate
            ) { binding, item, _ ->
                binding.titleTv.text = item.name
                binding.totalItemTv.text=item.price.toString()+"x"+item.quantity
            }
            binding.moneyRec.adapter = adapter
        }


        val cashText = binding.cashTv
        val posText = binding.posTv
        val staffText = binding.staffTv
        updateSelection(cashCard, cashText, "Cash")
        cashCard.setOnClickListener {
            updateSelection(cashCard, cashText, "Cash")
        }

        posCard.setOnClickListener {
            updateSelection(posCard, posText, "POS")
        }

        staffCard.setOnClickListener {
            updateSelection(staffCard, staffText, "Staff")
        }
        viewModel.fetchEmployees(1)
        observeSalesData()

        binding.submitBtn.setOnClickListener {
            submitSale()
        }
        binding.backBtn.setOnClickListener {
            finish()
        }
    }
    private fun submitSale() {
        // Validate payment mode selection
        if (selectedPaymentMode == null) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
            return
        }

        // Prepare required data
        val clubId = "1".toRequestBody("text/plain".toMediaTypeOrNull()) // Replace with actual club ID
        val discount = "0".toRequestBody("text/plain".toMediaTypeOrNull()) // Replace with actual discount
        val date = System.currentTimeMillis().toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val cart = Gson().toJson(selectedItems).toRequestBody("application/json".toMediaTypeOrNull())

        // Handle employee ID (if Staff mode selected)
        val employeeId = if (selectedPaymentMode == "Staff") {
            (binding.employeSpinner.selectedItem as? Employee)?.id?.toString()
                ?.toRequestBody("text/plain".toMediaTypeOrNull())
        } else {
            null
        }

        // Handle notes
        val notes = binding.noteEd.text.toString().takeIf { it.isNotBlank() }
            ?.toRequestBody("text/plain".toMediaTypeOrNull())

        // Handle receipt (if POS mode selected)
        val receiptPart = if (selectedPaymentMode == "POS") {
            // You'll need to implement file selection logic
            // For now, we'll pass null
            null
        } else {
            null
        }

        // Get auth token (replace with your actual token retrieval)
        val token = "Bearer ${TinyDB(this).getString("access_token")}"
        // Make the API call
        createSale(
            clubId = clubId,
            discount = discount,
            employeeId = employeeId,
            notes = notes,
            receipt = receiptPart,
            cart = cart,
            date = date,
            token = token
        )
    }

    override fun onResume() {
        super.onResume()


    }
    private fun observeSalesData() {
        viewModel.employeData.observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    binding.root.showKProgress(true)
                    Log.d(TAG, "observeSalesData: Loading")
                }

                is ApiResponse.Success -> {
                    binding.root.showKProgress(false)
                    response.data?.let { employees ->
                        val adapter = EmployeeSpinnerAdapter(this@InventoryPaymentActivity, employees)
                        binding.employeSpinner.adapter = adapter
                        Log.d(TAG, "setupObservers: $employees")
                        // Set up item selection listener
                        binding.employeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                // Safely get the selected employee
                                val selectedEmployee = adapter.getItem(position)
                                selectedEmployee?.let { employee ->
                                    // Do something with the selected employee
                                    Log.d("EmployeeSelection", "Selected: ${employee.name}")
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // Handle no selection (optional)
                            }
                        }
                    }
                }

                is ApiResponse.Error -> {
                    binding.root.showKProgress(false)
                    Toast.makeText(this, response.error ?: "Error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    private fun updateSelection(selected: CardView, textView: TextView, paymentMode: String) {
        // Reset all to default
        resetCard(binding.cashCardBtn, binding.cashTv)
        resetCard(binding.posCardBtn, binding.posTv)
        resetCard(binding.staffCardBtn, binding.staffTv)

        // Highlight selected
        selected.setCardBackgroundColor(ContextCompat.getColor(this, R.color.v5greenColor))
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))

        selectedPaymentMode = paymentMode
        when (selectedPaymentMode) {
            "Cash" -> {
                binding.employeSpinner.gone()
                binding.recipCard.gone()
            }

            "POS" -> {
                binding.employeSpinner.gone()
                binding.recipCard.visible()
            }

            "Staff" -> {
                binding.employeSpinner.visible()
                binding.recipCard.gone()
            }
        }
        Log.d("PaymentMode", "Selected: $selectedPaymentMode")
    }

    private fun resetCard(card: CardView, text: TextView) {
        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        text.setTextColor(ContextCompat.getColor(this, R.color._26334a))
    }


    fun createSale(
        clubId: RequestBody,
        discount: RequestBody,
        employeeId: RequestBody?,
        notes: RequestBody?,
        receipt: MultipartBody.Part?,
        cart: RequestBody,
        date: RequestBody,
        token: String
    ): LiveData<Result<NewSaleResponse>> {
        val result = MutableLiveData<Result<NewSaleResponse>>()

        ApiClient.inventoryService.createSale(
            clubId,
            discount,
            employeeId,
            notes,
            receipt,
            cart,
            date,
            token
        ).enqueue(object : Callback<NewSaleResponse> {
            override fun onResponse(call: Call<NewSaleResponse>, response: Response<NewSaleResponse>) {
                if (response.isSuccessful) {
                    result.postValue(Result.success(response.body()!!))
                    showToast("Sale Created")
                    finish()
                } else {
                    result.postValue(Result.failure(Exception("Error: ${response.errorBody()?.string()}")))
                }
            }

            override fun onFailure(call: Call<NewSaleResponse>, t: Throwable) {
                result.postValue(Result.failure(t))
            }
        })

        return result
    }
}

class EmployeeSpinnerAdapter(
    context: Context,
    private val employees: List<Employee>
) : ArrayAdapter<Employee>(context, android.R.layout.simple_spinner_item, employees) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.text = employees[position].name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.text = employees[position].name
        return view
    }
}