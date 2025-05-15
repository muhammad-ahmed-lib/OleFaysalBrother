package ae.oleapp.presentation.viewmodels

import ae.oleapp.abstraction.errorhandling.ApiResponse
import ae.oleapp.abstraction.models.InventoryProduct
import ae.oleapp.abstraction.models.InventoryStockData
import ae.oleapp.abstraction.models.InventorySummary
import ae.oleapp.abstraction.models.ProfitReport
import ae.oleapp.abstraction.models.Sale
import ae.oleapp.abstraction.models.SaleReportData
import ae.oleapp.abstraction.models.SalesOrderModelClass
import ae.oleapp.abstraction.models.StockUpdateData
import ae.oleapp.abstraction.repository.InventoryRepository
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File

class InventoryViewModel(
    private val repository: InventoryRepository,
) : ViewModel() {

    private val _salesDetailsResponse = MutableLiveData<ApiResponse<Sale>>()
    val salesDetailResponse: LiveData<ApiResponse<Sale>> = _salesDetailsResponse


    private val _salesResponse = MutableLiveData<ApiResponse<List<SalesOrderModelClass>>>()
    val salesOrderResponse: LiveData<ApiResponse<List<SalesOrderModelClass>>> = _salesResponse

    private val _inventoryStockResponse = MutableLiveData<ApiResponse<InventoryStockData>>()
    val inventoryStockResponse: LiveData<ApiResponse<InventoryStockData>> = _inventoryStockResponse

    private val _profitReport = MutableLiveData<ApiResponse<List<ProfitReport>>>()
    val profitReport: LiveData<ApiResponse<List<ProfitReport>>> = _profitReport

    private val _homePageSummary = MutableLiveData<ApiResponse<InventorySummary>>()
    val homePageSummary: LiveData<ApiResponse<InventorySummary>> = _homePageSummary

    private val _productsResponse = MutableLiveData<ApiResponse<List<InventoryProduct>>>()
    val productsResponse: LiveData<ApiResponse<List<InventoryProduct>>> = _productsResponse


    init {
        getSales()
        getInventoryStockReport()  // Fetch inventory stock report initially
        getProfitReport()
        getHomePageSummary()
        getInventoryProducts()
        fetchSalesReport(1,1)
    }


    private val _addProductResponse = MutableLiveData<ApiResponse<InventoryProduct>>()
    val addProductResponse: LiveData<ApiResponse<InventoryProduct>> = _addProductResponse

    fun addInventoryProduct(
        clubId: Int,
        name: String,
        purchasePrice: Double,
        sellingPrice: Double,
        quantity: Int,
        description: String? = null,
        category: String? = null,
        barcode: String? = null,
        sku: String? = null,
        photoFile: File? = null
    ) {
        _addProductResponse.postValue(ApiResponse.Loading())

        viewModelScope.launch {
            repository.addInventoryProduct(
                clubId = clubId,
                name = name,
                purchasePrice = purchasePrice,
                sellingPrice = sellingPrice,
                quantity = quantity,
                description = description,
                category = category,
                barcode = barcode,
                sku = sku,
                photoFile = photoFile
            ) { result ->
                result.onSuccess { product ->
                    _addProductResponse.postValue(ApiResponse.Success(product))
                }.onFailure { error ->
                    _addProductResponse.postValue(ApiResponse.Error(error.message ?: "Failed to add product"))
                }
            }
        }
    }

    // InventoryViewModel.kt
    private val _updateProductResponse = MutableLiveData<ApiResponse<InventoryProduct>>()
    val updateProductResponse: LiveData<ApiResponse<InventoryProduct>> = _updateProductResponse

    fun updateInventoryProduct(
        productId: String,
        clubId: Int,
        name: String,
        purchasePrice: Double,
        sellingPrice: Double,
        quantity: Int,
        description: String? = null,
        category: String? = null,
        barcode: String? = null,
        sku: String? = null,
        photoFile: File? = null
    ) {
        _updateProductResponse.postValue(ApiResponse.Loading())

        viewModelScope.launch {
            repository.updateInventoryProduct(
                productId = productId,
                clubId = clubId,
                name = name,
                purchasePrice = purchasePrice,
                sellingPrice = sellingPrice,
                quantity = quantity,
                description = description,
                category = category,
                barcode = barcode,
                sku = sku,
                photoFile = photoFile
            ) { result ->
                result.onSuccess { product ->
                    _updateProductResponse.postValue(ApiResponse.Success(product))
                }.onFailure { error ->
                    _updateProductResponse.postValue(
                        ApiResponse.Error(error.message ?: "Failed to update product")
                    )
                }
            }
        }
    }


    private val _stockUpdateResponse = MutableLiveData<ApiResponse<StockUpdateData>>()
    val stockUpdateResponse: LiveData<ApiResponse<StockUpdateData>> = _stockUpdateResponse

    fun updateStock(productId: String, quantity: Int, description: String? = null) {
        _stockUpdateResponse.postValue(ApiResponse.Loading())

        viewModelScope.launch {
            repository.updateStock(
                productId = productId,
                quantity = quantity,
                description = description
            ) { result ->
                result.onSuccess { data ->
                    _stockUpdateResponse.postValue(ApiResponse.Success(data))
                }.onFailure { error ->
                    _stockUpdateResponse.postValue(ApiResponse.Error(error.message ?: "Stock update failed"))
                }
            }
        }
    }



    fun getInventoryProducts() {
        _productsResponse.postValue(ApiResponse.Loading())
        Log.d("InventoryViewModel", "Loading inventory products...")

        viewModelScope.launch {
            repository.fetchInventoryProducts { result ->
                result.onSuccess { productsList ->
                    Log.d("InventoryViewModel", "Fetched ${productsList.size} products.")
                    productsList.forEach { product ->
                        Log.d("InventoryViewModel", "Product ID: ${product.id}, Name: ${product.name}")
                    }
                    _productsResponse.postValue(ApiResponse.Success(productsList))
                }.onFailure { error ->
                    Log.e("InventoryViewModel", "Failed to fetch products: ${error.message}", error)
                    _productsResponse.postValue(ApiResponse.Error(error.message ?: "Unknown error"))
                }
            }
        }
    }

    private val _salesReportResponse = MutableLiveData<ApiResponse<SaleReportData>>()
    val salesReportResponse: LiveData<ApiResponse<SaleReportData>> = _salesReportResponse


    fun fetchSalesReport( clubId: Int = 1,
                          employeId: Int = 1) {
        _productsResponse.postValue(ApiResponse.Loading())
        Log.d("InventoryViewModel", "Loading inventory products...")

        viewModelScope.launch {
            repository.fetchSalesReport(clubId,employeId) { result ->
                result.onSuccess { productsList ->
                    _salesReportResponse.postValue(ApiResponse.Success(productsList))
                }.onFailure { error ->
                    Log.e("InventoryViewModel", "Failed to fetch products: ${error.message}", error)
                    _salesReportResponse.postValue(ApiResponse.Error(error.message ?: "Unknown error"))
                }
            }
        }
    }


    // Fetch Sales Orders
    fun getSales() {
        _salesResponse.postValue(ApiResponse.Loading())
        Log.d("InventoryViewModel", "Loading sales orders...")

        viewModelScope.launch {
            repository.fetchSalesOrders { result ->
                result.onSuccess { salesList ->
                    Log.d("InventoryViewModel", "Fetched ${salesList.size} sales orders.")
                    salesList.forEach { sale ->
                        Log.d("InventoryViewModel", "Sale ID: ${sale.id}, Total Amount: ${sale.total_amount}")
                    }
                    _salesResponse.postValue(ApiResponse.Success(salesList))
                }.onFailure { error ->
                    Log.e("InventoryViewModel", "Failed to fetch sales orders: ${error.message}", error)
                    _salesResponse.postValue(ApiResponse.Error(error.message ?: "Unknown error"))
                }
            }
        }
    }

    fun getSaleDetails(id:Int) {
        _salesDetailsResponse.postValue(ApiResponse.Loading())
        Log.d("InventoryViewModel", "Loading sales orders...")

        viewModelScope.launch {
            repository.fetchSalesOrderDetails(id) { result ->
                result.onSuccess { salesList ->
                    _salesDetailsResponse.postValue(ApiResponse.Success(salesList))
                }.onFailure { error ->
                    Log.e("InventoryViewModel", "Failed to fetch sales orders: ${error.message}", error)
                    _salesDetailsResponse.postValue(ApiResponse.Error(error.message ?: "Unknown error"))
                }
            }
        }
    }


    // Fetch Inventory Stock Report
    fun getInventoryStockReport(searchQuery: String = "") {
        _inventoryStockResponse.postValue(ApiResponse.Loading())
        Log.d("InventoryViewModel", "Loading inventory stock report...")

        viewModelScope.launch {
            repository.fetchInventoryStockReport(searchQuery = searchQuery) { result ->
                result.onSuccess { inventoryStockData ->
                    Log.d("InventoryViewModel", "Fetched inventory stock data: ${inventoryStockData.total_products} products.")
                    _inventoryStockResponse.postValue(ApiResponse.Success(inventoryStockData))
                }.onFailure { error ->
                    Log.e("InventoryViewModel", "Failed to fetch inventory stock report: ${error.message}", error)
                    _inventoryStockResponse.postValue(ApiResponse.Error(error.message ?: "Unknown error"))
                }
            }
        }
    }

    fun getProfitReport() {
        _profitReport.postValue(ApiResponse.Loading())
        viewModelScope.launch {
            repository.fetchProfitReport { result ->
                result.onSuccess { report ->
                    Log.d("InventoryViewModel", "Fetched inventory ProfitReport: ${report}.")

                    _profitReport.postValue(ApiResponse.Success(report))
                }.onFailure { error ->
                    Log.e("InventoryViewModel", "Profit report error: ${error.message}", error)
                    _profitReport.postValue(ApiResponse.Error(error.message ?: "Unknown error"))
                }
            }
        }
    }

    fun getHomePageSummary() {
        _homePageSummary.postValue(ApiResponse.Loading())
        viewModelScope.launch {
            repository.fetchInventorySummary { result ->
                result.onSuccess { summary ->
                    Log.d("InventoryViewModel", "Fetched inventory HomePageSummary: ${summary}.")
                    _homePageSummary.postValue(ApiResponse.Success(summary))
                }.onFailure { error ->
                    Log.e("InventoryViewModel", "Home summary error: ${error.message}", error)
                    _homePageSummary.postValue(ApiResponse.Error(error.message ?: "Unknown error"))
                }
            }
        }
    }

}
