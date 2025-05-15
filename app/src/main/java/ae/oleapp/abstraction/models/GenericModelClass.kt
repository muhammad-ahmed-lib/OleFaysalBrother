package ae.oleapp.abstraction.models

import android.app.Activity
import com.google.gson.annotations.SerializedName


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

data class InventorySummary(
    val date: String,
    val total_sales: Double,
    val total_purchase: Double,
    val total_profit: Double,
    val currency: String
)

data class InventorySummaryResponse(
    val data: InventorySummary,
    val status: Int,
    val message: String
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




// InventoryProductResponse.kt
data class InventoryProductResponse(
    val data: List<InventoryProduct>,
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
    val current_stock: Int
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
