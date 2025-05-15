package ae.oleapp.abstraction.repository

import ae.oleapp.abstraction.api_client.ApiClient
import ae.oleapp.abstraction.models.CartItem
import ae.oleapp.abstraction.models.Employee
import ae.oleapp.abstraction.models.EmployeeResponse
import ae.oleapp.abstraction.models.InventoryAddProductResponse
import ae.oleapp.abstraction.models.InventoryProduct
import ae.oleapp.abstraction.models.InventoryProductResponse
import ae.oleapp.abstraction.models.InventoryStockData
import ae.oleapp.abstraction.models.InventoryStockResponse
import ae.oleapp.abstraction.models.InventorySummary
import ae.oleapp.abstraction.models.InventorySummaryResponse
import ae.oleapp.abstraction.models.NewSaleResponse
import ae.oleapp.abstraction.models.ProfitReport
import ae.oleapp.abstraction.models.ProfitReportResponse
import ae.oleapp.abstraction.models.Sale
import ae.oleapp.abstraction.models.SaleReportData
import ae.oleapp.abstraction.models.SaleReportResponse
import ae.oleapp.abstraction.models.SaleResponse
import ae.oleapp.abstraction.models.SalesOrderModelClass
import ae.oleapp.abstraction.models.SalesOrderResponse
import ae.oleapp.abstraction.models.StockUpdateData
import ae.oleapp.abstraction.models.StockUpdateResponse
import ae.oleapp.abstraction.models.UpdateProductResponse
import ae.oleapp.abstraction.models.UpdateStockRequest
import ae.oleapp.utils.TinyDB
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class InventoryRepository(private val context: Context) {

    private val accessToken = TinyDB(context).getToken().also {
        Log.d("InventoryRepository", "Access Token: $it")
    }


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
        photoFile: File? = null,
        callback: (Result<InventoryProduct>) -> Unit
    ) {
        // Convert required parameters to RequestBody
        val clubIdBody = clubId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val purchasePriceBody = purchasePrice.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val sellingPriceBody = sellingPrice.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val quantityBody = quantity.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        // Convert optional parameters to RequestBody (if not null)
        val descriptionBody = description?.toRequestBody("text/plain".toMediaTypeOrNull())
        val categoryBody = category?.toRequestBody("text/plain".toMediaTypeOrNull())
        val barcodeBody = barcode?.toRequestBody("text/plain".toMediaTypeOrNull())
        val skuBody = sku?.toRequestBody("text/plain".toMediaTypeOrNull())

        // Prepare photo file if exists
        val photoPart = photoFile?.let { file ->
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestFile
            )
        }

        // Create the call to the Retrofit service
        val call = ApiClient.inventoryService.addInventoryProduct(
            "Bearer $accessToken",
            clubIdBody,
            nameBody,
            purchasePriceBody,
            sellingPriceBody,
            quantityBody,
            descriptionBody,
            categoryBody,
            barcodeBody,
            skuBody,
            photoPart
        )

        // Make the asynchronous API call
        call.enqueue(object : Callback<InventoryAddProductResponse> {
            override fun onResponse(
                call: Call<InventoryAddProductResponse>,
                response: Response<InventoryAddProductResponse>
            ) {
                if (response.isSuccessful) {
                    val product = response.body()?.data
                    if (product != null) {
                        callback(Result.success(product))
                    } else {
                        callback(Result.failure(Throwable("No product data in response")))
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    callback(Result.failure(Throwable("Error: ${response.code()} - $errorBody")))
                }
            }

            override fun onFailure(call: Call<InventoryAddProductResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }


    // InventoryRepository.kt
    fun updateInventoryProduct(
        productId: String,
        clubId: Int,
        name: String,
        purchasePrice: Double,
        sellingPrice: Double,
        quantity: Int,
        photoFile: File? = null,
        callback: (Result<InventoryProduct>) -> Unit
    ) {
        try {
            // Convert all parameters to RequestBody
            val productIdBody = productId.toRequestBody("text/plain".toMediaTypeOrNull())
            val clubIdBody = clubId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val purchasePriceBody = purchasePrice.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val sellingPriceBody = sellingPrice.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val quantityBody = quantity.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            // Prepare photo file if exists
            val photoPart = photoFile?.let { file ->
                file.asRequestBody("image/*".toMediaTypeOrNull()).let { requestFile ->
                    MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestFile
                    )
                }
            }

            // Log the request details for debugging
            Log.d("API_REQUEST", "Updating product with ID: $productId")
            Log.d("API_REQUEST", "Club ID: $clubId, Name: $name")
            Log.d("API_REQUEST", "Prices: $purchasePrice/$sellingPrice, Quantity: $quantity")
            Log.d("API_REQUEST", "Photo: ${photoFile?.name ?: "null"}")

            // Create the call to the Retrofit service
            val call = ApiClient.inventoryService.updateInventoryProduct(
                "Bearer $accessToken",
                productIdBody,
                clubIdBody,
                nameBody,
                purchasePriceBody,
                sellingPriceBody,
                quantityBody,
                photoPart
            )

            // Make the asynchronous API call
            call.enqueue(object : Callback<UpdateProductResponse> {
                override fun onResponse(
                    call: Call<UpdateProductResponse>,
                    response: Response<UpdateProductResponse>
                ) {
                    val responseBody = response.body()

                    if (response.isSuccessful && responseBody != null) {
                        // Check the API-level status code
                        if (responseBody.status == 200) { // Or whatever success code your API uses
                            responseBody.data.let { product ->
                                callback(Result.success(product))
                            } ?: run {
                                callback(Result.failure(Throwable("Product data is null")))
                            }
                        } else {
                            // API-level error (like 400 in your case)
                            callback(Result.failure(Throwable(responseBody.message ?: "API error ${responseBody.status}")))
                        }
                    } else {
                        // HTTP-level error
                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                        callback(Result.failure(Throwable("HTTP error ${response.code()}: $errorBody")))
                    }
                }

                override fun onFailure(call: Call<UpdateProductResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
        } catch (e: Exception) {
            Log.e("API_EXCEP_TION", "Failed to create request", e)
            callback(Result.failure(e))
        }
    }
    fun updateStock(
        productId: String,
        quantity: Int,
        description: String? = null,
        callback: (Result<StockUpdateData>) -> Unit
    ) {
        val request = UpdateStockRequest(
            productId = productId,
            quantity = quantity,
            description = description
        )

        val call = ApiClient.inventoryService.updateStock(
            "Bearer $accessToken",
            request
        )

        call.enqueue(object : Callback<StockUpdateResponse> {
            override fun onResponse(
                call: Call<StockUpdateResponse>,
                response: Response<StockUpdateResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status == 200 && responseBody.data != null) {
                        callback(Result.success(responseBody.data))
                    } else {
                        callback(Result.failure(Throwable(responseBody?.message ?: "Update failed")))
                    }
                } else {
                    val error = response.errorBody()?.string() ?: "Unknown error"
                    callback(Result.failure(Throwable("Error: ${response.code()} - $error")))
                }
            }

            override fun onFailure(call: Call<StockUpdateResponse>, t: Throwable) {
                callback(Result.failure(t))
            }
        })
    }



    fun fetchInventoryProducts(clubId: Int = 1, callback: (Result<List<InventoryProduct>>) -> Unit) {
        // Create the call to the Retrofit service
        val call = ApiClient.inventoryService.getInventoryProducts("Bearer $accessToken", clubId)

        // Make the asynchronous API call
        call.enqueue(object : retrofit2.Callback<InventoryProductResponse> {
            override fun onResponse(
                call: Call<InventoryProductResponse>,
                response: retrofit2.Response<InventoryProductResponse>
            ) {
                if (response.isSuccessful) {
                    val products = response.body()?.data ?: emptyList()
                    // Successfully fetched data, returning it
                    callback(Result.success(products))
                } else {
                    // In case of error (e.g., 404, 500)
                    callback(Result.failure(Throwable("Error: ${response.code()} ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<InventoryProductResponse>, t: Throwable) {
                // Handling failure (e.g., network failure)
                Log.e("InventoryRepository", "Network request failed: ${t.message}")
                callback(Result.failure(t))
            }
        })
    }

    fun fetchSalesReport(
        clubId: Int = 1,
        employeId: Int?=null,
        callback: (Result<SaleReportData>) -> Unit
    ) {
        val call = ApiClient.inventoryService.getSalesReport("Bearer $accessToken", clubId, employeId)
        call.enqueue(object : Callback<SaleReportResponse> {
            override fun onResponse(
                call: Call<SaleReportResponse>,
                response: Response<SaleReportResponse>
            ) {
                if (response.isSuccessful) {
                    val reportData = response.body()?.data
                    if (reportData != null) {
                        callback(Result.success(reportData))
                    } else {
                        callback(Result.failure(Throwable("Empty report data")))
                    }
                } else {
                    callback(Result.failure(Throwable("Error: ${response.code()} ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<SaleReportResponse>, t: Throwable) {
                Log.e("InventoryRepository", "Network request failed: ${t.message}")
                callback(Result.failure(t))
            }
        })
    }


    fun fetchSalesOrders(clubId: Int = 1, callback: (Result<List<SalesOrderModelClass>>) -> Unit) {
        // Create the call to the Retrofit service
        val call = ApiClient.inventoryService.getSalesOrders("Bearer $accessToken", clubId)

        // Make the asynchronous API call
        call.enqueue(object : retrofit2.Callback<SalesOrderResponse> {
            override fun onResponse(
                call: Call<SalesOrderResponse>,
                response: retrofit2.Response<SalesOrderResponse>
            ) {
                if (response.isSuccessful) {
                    val sales = response.body()?.data ?: emptyList()
                    // Successfully fetched data, returning it
                    callback(Result.success(sales))
                } else {
                    // In case of error (e.g., 404, 500)
                    callback(Result.failure(Throwable("Error: ${response.code()} ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<SalesOrderResponse>, t: Throwable) {
                // Handling failure (e.g., network failure)
                Log.e("InventoryRepository", "Network request failed: ${t.message}")
                callback(Result.failure(t))
            }
        })
    }
    fun fetchSalesOrderDetails(clubId: Int = 1, callback: (Result<Sale>) -> Unit) {
        val call = ApiClient.inventoryService.getSalesOrderById("Bearer $accessToken", clubId)

        call.enqueue(object : retrofit2.Callback<SaleResponse> {
            override fun onResponse(
                call: Call<SaleResponse>,
                response: retrofit2.Response<SaleResponse>
            ) {
                if (response.isSuccessful) {
                    val sale = response.body()?.data
                    if (sale != null) {
                        callback(Result.success(sale))
                    } else {
                        callback(Result.failure(Throwable("Sale data is null")))
                    }
                } else {
                    callback(Result.failure(Throwable("Error: ${response.code()} ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<SaleResponse>, t: Throwable) {
                Log.e("InventoryRepository", "Network request failed: ${t.message}")
                callback(Result.failure(t))
            }
        })
    }


    fun fetchInventoryStockReport(clubId: Int = 1, searchQuery: String = "", callback: (Result<InventoryStockData>) -> Unit) {
        // Create the call to the Retrofit service
        val call = ApiClient.inventoryService.getInventoryStockReport("Bearer $accessToken", clubId, searchQuery)

        // Make the asynchronous API call
        call.enqueue(object : retrofit2.Callback<InventoryStockResponse> {
            override fun onResponse(
                call: Call<InventoryStockResponse>,
                response: retrofit2.Response<InventoryStockResponse>
            ) {
                if (response.isSuccessful) {
                    val inventoryStock = response.body()?.data ?: return
                    // Successfully fetched data, returning it
                    callback(Result.success(inventoryStock))
                } else {
                    // In case of error (e.g., 404, 500)
                    callback(Result.failure(Throwable("Error: ${response.code()} ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<InventoryStockResponse>, t: Throwable) {
                // Handling failure (e.g., network failure)
                Log.e("InventoryRepository", "Network request failed: ${t.message}")
                callback(Result.failure(t))
            }
        })
    }


    fun fetchProfitReport(clubId: Int = 1, callback: (Result<List<ProfitReport>>) -> Unit) {
        val call = ApiClient.inventoryService.getProfitReport("Bearer $accessToken", clubId)

        call.enqueue(object : retrofit2.Callback<ProfitReportResponse> {
            override fun onResponse(
                call: Call<ProfitReportResponse>,
                response: retrofit2.Response<ProfitReportResponse>
            ) {
                if (response.isSuccessful) {
                    val report = response.body()?.data ?: emptyList()
                    callback(Result.success(report))
                } else {
                    callback(Result.failure(Throwable("Error: ${response.code()} ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<ProfitReportResponse>, t: Throwable) {
                Log.e("InventoryRepository", "Failed to fetch profit report: ${t.message}", t)
                callback(Result.failure(t))
            }
        })
    }


    fun fetchInventorySummary(clubId: Int = 1, callback: (Result<InventorySummary>) -> Unit) {
        val call = ApiClient.inventoryService.getInventorySummary("Bearer $accessToken", clubId)

        call.enqueue(object : Callback<InventorySummaryResponse> {
            override fun onResponse(
                call: Call<InventorySummaryResponse>,
                response: Response<InventorySummaryResponse>
            ) {
                if (response.isSuccessful) {
                    val summary = response.body()?.data
                    if (summary != null) {
                        callback(Result.success(summary))
                    } else {
                        callback(Result.failure(Throwable("Empty summary data")))
                    }
                } else {
                    callback(Result.failure(Throwable("Error: ${response.code()} ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<InventorySummaryResponse>, t: Throwable) {
                Log.e("InventoryRepository", "Inventory summary fetch failed: ${t.message}", t)
                callback(Result.failure(t))
            }
        })
    }

    fun getEmployees(clubId: Int = 1, callback: (Result<List<Employee>>) -> Unit) {
        val call = ApiClient.inventoryService.getEmployees(clubId,"Bearer $accessToken", )

        call.enqueue(object : Callback<EmployeeResponse> {
            override fun onResponse(
                call: Call<EmployeeResponse>,
                response: Response<EmployeeResponse>
            ) {
                if (response.isSuccessful) {
                    val summary = response.body()?.data
                    if (summary != null) {
                        callback(Result.success(summary))
                    } else {
                        callback(Result.failure(Throwable("Empty summary data")))
                    }
                } else {
                    callback(Result.failure(Throwable("Error: ${response.code()} ${response.message()}")))
                }
            }

            override fun onFailure(call: Call<EmployeeResponse>, t: Throwable) {
                Log.e("InventoryRepository", "Inventory summary fetch failed: ${t.message}", t)
                callback(Result.failure(t))
            }
        })
    }

    // For creating a sale
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

