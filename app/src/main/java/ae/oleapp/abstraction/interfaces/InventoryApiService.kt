package ae.oleapp.abstraction.interfaces

import ae.oleapp.abstraction.models.InventoryProductResponse
import ae.oleapp.abstraction.models.InventoryStockResponse
import ae.oleapp.abstraction.models.InventorySummaryResponse
import ae.oleapp.abstraction.models.ProfitReportResponse
import ae.oleapp.abstraction.models.Sale
import ae.oleapp.abstraction.models.SaleReportResponse
import ae.oleapp.abstraction.models.SaleResponse
import ae.oleapp.abstraction.models.SalesOrderResponse
import ae.oleapp.abstraction.models.StockUpdateResponse
import ae.oleapp.abstraction.models.UpdateStockRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
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


    @GET("owner/inventory")
    fun getInventoryProducts(
        @Header("Authorization") token: String,
        @Query("club_id") clubId: Int
    ): Call<InventoryProductResponse>



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
    ): Call<InventoryProductResponse>

    @Multipart
    @POST("owner/inventory") // Changed to update endpoint
    fun updateInventoryProduct(
        @Header("Authorization") token: String,
        @Part("product_id") productId: RequestBody,
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
    ): Call<InventoryProductResponse>


    @PUT("owner/inventory/stock")
    fun updateStock(
        @Header("Authorization") token: String,
        @Body request: UpdateStockRequest
    ): Call<StockUpdateResponse>


    @GET("owner/inventory/sales/report")
    fun getSalesReport(
        @Header("Authorization") token: String,
        @Query("club_id") clubId: Int,
        @Query("employee_id") employeeId: Int
    ): Call<SaleReportResponse>
}