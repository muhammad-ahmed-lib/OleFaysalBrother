package ae.oleapp.abstraction.interfaces

import ae.oleapp.abstraction.models.EmployeeResponse
import ae.oleapp.abstraction.models.InventoryAddProductResponse
import ae.oleapp.abstraction.models.InventoryProductResponse
import ae.oleapp.abstraction.models.InventoryStockResponse
import ae.oleapp.abstraction.models.InventorySummaryResponse
import ae.oleapp.abstraction.models.NewSaleResponse
import ae.oleapp.abstraction.models.PlayersCountResponse
import ae.oleapp.abstraction.models.ProfitReportResponse
import ae.oleapp.abstraction.models.Sale
import ae.oleapp.abstraction.models.SaleReportResponse
import ae.oleapp.abstraction.models.SaleResponse
import ae.oleapp.abstraction.models.SalesOrderResponse
import ae.oleapp.abstraction.models.SmsHomeResponse
import ae.oleapp.abstraction.models.SmsPurchaseRequest
import ae.oleapp.abstraction.models.SmsPurchaseResponse
import ae.oleapp.abstraction.models.SmsRequest
import ae.oleapp.abstraction.models.SmsResponse
import ae.oleapp.abstraction.models.SmsUsageResponse
import ae.oleapp.abstraction.models.StockUpdateResponse
import ae.oleapp.abstraction.models.UpdateProductResponse
import ae.oleapp.abstraction.models.UpdateSmsRequest
import ae.oleapp.abstraction.models.UpdateStockRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface InventoryApiService {



    @GET("owner/club/sms/delivered/history")
    fun getSmsDeliveryHistory(
        @Header("Authorization") token: String,
        @Query("club_id") clubId: Int
    ): Call<SmsUsageResponse>


    @POST("owner/club/sms/purchase")
    suspend fun purchaseSms(
        @Header("Authorization") accessToken: String,
        @Body request: SmsPurchaseRequest
    ): Response<SmsPurchaseResponse>


    @PUT("owner/club/sms/request")
    suspend fun updateSmsRequest(
        @Header("Authorization") accessToken: String,
        @Body request: UpdateSmsRequest
    ): Response<SmsResponse>


    @POST("owner/club/sms/request")
    suspend fun sendSmsRequest(
        @Header("Authorization") authHeader: String, // Add this
        @Body request: SmsRequest
    ): Response<SmsResponse>


    @GET("owner/inventory")
    fun getInventoryProducts(
        @Header("Authorization") token: String,
        @Query("club_id") clubId: Int
    ): Call<InventoryProductResponse>


    @GET("owner/club/sms/home/data")
    fun getSmsHome(
        @Header("Authorization") token: String,
        @Query("club_id") clubId: Int
    ): Call<SmsHomeResponse>

    @GET("owner/club/sms")
    fun getPlayersCount(
        @Header("Authorization") token: String,
        @Query("club_id") clubId: Int
    ): Call<PlayersCountResponse>


    @GET("owner/inventory/sales/all")
    fun getSalesOrders(
        @Header("Authorization") token: String,
        @Query("club_id") clubId: Int
    ): Call<SalesOrderResponse>

    @GET("owner/inventory/sales/single/{id}")
    fun getSalesOrderById(
        @Header("Authorization") token: String,
        @Path("id") saleId: Int
    ): Call<SaleResponse>

    @GET("owner/inventory/stock/report")
    fun getInventoryStockReport(
        @Header("Authorization") token: String,
        @Query("club_id") clubId: Int,
        @Query("search") searchQuery: String
    ): Call<InventoryStockResponse>


    @GET("owner/inventory/sales/report/profit")
    fun getProfitReport(
        @Header("Authorization") token: String,
        @Query("club_id") clubId: Int,
        @Query("search") search: String = "",
        @Query("employee_id") employeeId: String = ""
    ): Call<ProfitReportResponse>


    @GET("owner/inventory/summary/home")
    fun getInventorySummary(
        @Header("Authorization") token: String,
        @Query("club_id") clubId: Int
    ): Call<InventorySummaryResponse>


    @Multipart
    @POST("owner/inventory")
    fun addInventoryProduct(
        @Header("Authorization") token: String,
        @Part("club_id") clubId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("purchase_price") purchasePrice: RequestBody,
        @Part("selling_price") sellingPrice: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("category") category: RequestBody?,
        @Part("barcode") barcode: RequestBody?,
        @Part("sku") sku: RequestBody?,
        @Part photo: MultipartBody.Part?
    ): Call<InventoryAddProductResponse>

    @Multipart
    @PUT("owner/inventory") // Changed to update endpoint
    fun updateInventoryProduct(
        @Header("Authorization") token: String,
        @Part("product_id") productId: RequestBody,
        @Part("club_id") clubId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("purchase_price") purchasePrice: RequestBody,
        @Part("selling_price") sellingPrice: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Call<UpdateProductResponse>


    @PUT("owner/inventory/stock")
    fun updateStock(
        @Header("Authorization") token: String,
        @Body request: UpdateStockRequest
    ): Call<StockUpdateResponse>


    @GET("owner/inventory/sales/report")
    fun getSalesReport(
        @Header("Authorization") token: String,
        @Query("club_id") clubId: Int,
        @Query("employee_id") employeeId: Int? = null  // Make it nullable and optional
    ): Call<SaleReportResponse>

    @GET("owner/employees")
    fun getEmployees(
        @Query("club_id") clubId: Int,
        @Header("Authorization") token: String,
    ): Call<EmployeeResponse>


    @Multipart
    @POST("owner/inventory/sales")
    fun createSale(
        @Part("club_id") clubId: RequestBody,
        @Part("discount") discount: RequestBody,
        @Part("employee_id") employeeId: RequestBody?,
        @Part("notes") notes: RequestBody?,
        @Part receipt: MultipartBody.Part?,  // For file upload
        @Part("cart") cart: RequestBody,  // Serialized JSON
        @Part("date") date: RequestBody,
        @Header("Authorization") token: String,
        ): Call<NewSaleResponse>
}