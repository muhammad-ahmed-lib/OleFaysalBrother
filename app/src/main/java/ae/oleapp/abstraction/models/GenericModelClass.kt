package ae.oleapp.abstraction.models

import android.app.Activity
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody


data class GenericModelClass(
    val title:String,
    val icon:Int,
    val screen: Class<out Activity>? = null
)

data class SalesOrderResponse(
    val data: List<SalesOrderModelClass>,
    val status: Int,
    val message: String
)

data class SalesOrderModelClass(
    val id: Int,
    val order_number: String?,
    val total_amount: Double,
    val discount: Double?,
    val tax_amount: Double?,
    val notes: String?,
    val receipt: String?,
    val payment_method: String?,
    val sale_date: String?,
    val created_at: String?,
    val used_by: Any?,      // Can be more specific if you have a model
    val sold_by: Any?,      // Same as above
    val total_quantity: Int,
    val currency: String?
)
data class InventorySummaryResponse(
    val data: InventorySummary,
    val status: Int,
    val message: String
)

data class InventorySummary(
    val date: String,
    val sales: InventoryMetric,
    val purchase: InventoryMetric,
    val profit: InventoryMetric,
    val currency: String
)

data class InventoryMetric(
    val total: Double,
    val currency: String,
    val percentage: String,
    val trend: String
)

data class ProfitReport(
    val product_id: Int,
    val name: String,
    val purchase_price: Double,
    val selling_price: Double,
    val quantity_sold: Int,
    val total_sales: Double,
    val total_cost: Double,
    val total_discount: Double,
    val total_profit: Double
)

data class ProfitReportResponse(
    val data: List<ProfitReport>,
    val status: Int,
    val message: String
)
data class InventoryStockResponse(
    val data: InventoryStockData,
    val status: Int,
    val message: String
)

data class InventoryStockData(
    val inventory: List<InventoryItem>,
    val total_products: Int,
    val total_purchase_value: Double,
    val total_selling_value: Double
)

data class InventoryItem(
    val id: Int,
    val name: String,
    val purchase_price: Double,
    val selling_price: Double,
    val photo: String,
    val current_stock: Int,
    val created_at: String
)

data class SaleResponse(
    val data: Sale,
    val status: Int,
    val message: String
)

data class Sale(
    val id: Int,
    val order_number: String?,
    val total_amount: Double,
    val discount: Double,
    val tax_amount: Double,
    val notes: String,
    val receipt: String,
    val payment_method: String,
    val club_id: Int,
    val items: List<SaleItem>,
    val logs: List<SaleLog>,
    val sale_date: String,
    val created_at: String,
    val used_by: Any?,  // Define model if structure is known
    val sold_by: Any?,  // Define model if structure is known
    val currency: String
)

data class SaleItem(
    val id: Int,
    val quantity: Int,
    val unit_price: Double,
    val total_price: Double,
    val discount: Double,
    val inventory: Inventory
)

data class Inventory(
    val id: Int,
    val name: String,
    val photo: String,
    val purchase_price: Double,
    val selling_price: Double,
    val quantity: Int
)

data class SaleLog(
    val id: Int,
    val action: String,
    val notes: String,
    val date: String,
    val user: LogUser
)

data class LogUser(
    val id: Int,
    val name: String
)


data class UpdateProductResponse(
    val data: InventoryProduct,
    val status: Int,
    val message: String
)


// InventoryProductResponse.kt
data class InventoryProductResponse(
    val data: List<InventoryProduct>,
    val status: Int,
    val message: String
)

data class InventoryAddProductResponse(
    val data: InventoryProduct,
    val status: Int,
    val message: String
)

// InventoryProduct.kt
data class InventoryProduct(
    val id: Int,
    val name: String,
    val description: String?,
    val photo: String?,
    val category: String?,
    val barcode: String?,
    val sku: String?,
    val purchase_price: Double,
    val selling_price: Double,
    val min_stock_level: Int,
    val unit_of_measure: String,
    val is_active: Boolean,
    val created_at: String,
    val current_stock: Int,
    var count: Int
)

data class UpdateStockRequest(
    @SerializedName("product_id")
    val productId: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("description")
    val description: String? = null
)

data class StockUpdateResponse(
    val status: Int,
    val message: String,
    val data: StockUpdateData?
)

data class StockUpdateData(
    val product_id: String,
    val new_quantity: Int,
    val previous_quantity: Int,
    val updated_at: String
)

data class SaleReportResponse(
    val data: SaleReportData,
    val status: Int,
    val message: String
)

data class SaleReportData(
    val report: List<ReportItem>,
    val stats: SaleStats
)

data class ReportItem(
    val id: Int,
    val total_amount: Double,
    val discount: Double,
    val tax_amount: Double,
    val payment_method: String?,
    val notes: String?,
    val receipt: String?,
    val items: List<SaleReportItem>,
    val sale_date: String?,
    val created_at: String?,
    val used_by: UsedBy?,
    val sold_by: SoldBy?
)

data class SaleReportItem(
    val id: Int,
    val quantity: Int,
    val unit_price: Double,
    val total_price: Double,
    val discount: Double,
    val inventory: InventoryReport
)


data class InventoryReport(
    val id: Int,
    val name: String,
    val purchase_price: Double,
    val selling_price: Double,
    val quantity: Int
)

data class UsedBy(
    val name:String?
)

data class SoldBy(
    val id: Int,
    val name: String
)

data class SaleStats(
    val total_orders: Int,
    val total_quantity: Int,
    val total_sales: Int,
    val total_discount: Int
)


data class EmployeeResponse(
    val data: List<Employee>,
    val status: Int,
    val message: String
)

data class Employee(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("country_id") val countryId: Int,
    @SerializedName("country_code") val countryCode: String,
    val number: String,
    val phone: String,
    val picture: String,
    val currency: String,
    @SerializedName("assigned_clubs") val assignedClub: AssignedClub,
    val salary: Int,
    @SerializedName("login_id") val loginId: String,
    @SerializedName("total_ratings") val totalRatings: Int,
    @SerializedName("average_ratings") val averageRatings: String,
    @SerializedName("tip_amount") val tipAmount: Int,
    @SerializedName("added_date") val addedDate: String,
    val designation: Designation,
    @SerializedName("employee_of_the_month") val employeeOfTheMonth: Boolean,
    val month: String
)

data class AssignedClub(
    val id: Int,
    val name: String,
    val country: String,
    val currency: String
)

data class Designation(
    val id: Int,
    val name: String,
    val permission: List<Permission>
)

data class Permission(
    val id: Int,
    val name: String
)

// Request body model
data class SaleRequest(
    @SerializedName("club_id") val clubId: String,  // Required (text)
    @SerializedName("discount") val discount: String,  // Required (text)
    @SerializedName("employee_id") val employeeId: String? = null,  // Optional (text)
    @SerializedName("notes") val notes: String? = null,  // Optional (text)
    @SerializedName("receipt") val receipt: MultipartBody.Part? = null,  // Optional (file)
    @SerializedName("cart") val cart: List<CartItem>,  // Required (list of product_id + quantity)
    @SerializedName("date") val date: String  // Required (text)
)

data class CartItem(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("quantity") val quantity: Int
)

// Response model (adjust based on actual API response)
data class NewSaleResponse(
    val success: Boolean,
    val message: String,
    val saleId: Int? = null
)


data class SmsRequest(
    @SerializedName("club_id") val clubId: String,
    @SerializedName("total_sms") val totalSms: Int,
    @SerializedName("type") val type: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("sms_date") val smsDate: String,
    @SerializedName("sms_time") val smsTime: String,
    @SerializedName("sms_text") val smsText: String
)

data class SmsResponse(
    @SerializedName("error") val error: Map<String, Any> = emptyMap(), // Changed from String to Map
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String
)

data class UpdateSmsRequest(
    @SerializedName("request_id") val requestId: String,
    @SerializedName("club_id") val clubId: String,
    @SerializedName("total_sms") val totalSms: Int,
    @SerializedName("type") val type: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("sms_date") val smsDate: String,
    @SerializedName("sms_time") val smsTime: String,
    @SerializedName("sms_text") val smsText: String
)

data class SmsPurchaseRequest(
    @SerializedName("club_id") val clubId: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("quantity") val quantity: Int
)

data class SmsPurchaseResponse(
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: Int
)

data class SmsHomeResponse(
    @SerializedName("data") val data: List<SmsData>,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("total_purchased") val totalPurchased: Int,
    @SerializedName("total_used") val totalUsed: Int,
    @SerializedName("remaining") val remaining: Int,
    @SerializedName("sms_cost") val smsCost: Double,
    @SerializedName("currency") val currency: String
)

data class SmsData(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("total_sms") val totalSms: Int,
    @SerializedName("sms_text") val smsText: String,
    @SerializedName("sms_date") val smsDate: String,
    @SerializedName("sms_time") val smsTime: String,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("currency") val currency: String
)

data class PlayersCountResponse(
    @SerializedName("data") val data: List<PlayerCountData>,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String,
    @SerializedName("sms_cost") val smsCost: Double
)

data class PlayerCountData(
    @SerializedName("type") val type: String,
    @SerializedName("count") val count: Int
)

data class SmsUsageResponse(
    @SerializedName("data") val usageHistory: List<SmsUsageRecord>,
    @SerializedName("status") val status: Int,
    @SerializedName("message") val message: String
)

data class SmsUsageRecord(
    @SerializedName("username") val username: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("sms_used_count") val smsUsed: Int,
    @SerializedName("date") val date: String  // Format: "30/04/2025 08:32 PM"
)