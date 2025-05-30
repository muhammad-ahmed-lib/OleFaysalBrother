package ae.oleapp.utils
import ae.oleapp.presentation.ui.inventory.PurchasePassModelClass
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kaopiz.kprogresshud.KProgressHUD

private const val KPROGRESS_TAG = "kprogress_loader"

fun View.showKProgress(show: Boolean) {

    val activity = context as? AppCompatActivity ?: return

    val existingHud = this.getTag(KPROGRESS_TAG.hashCode()) as? KProgressHUD

    if (show) {
        if (existingHud == null) {
            val hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show()

            this.setTag(KPROGRESS_TAG.hashCode(), hud)
        }
    } else {
        existingHud?.dismiss()
        this.setTag(KPROGRESS_TAG.hashCode(), null)
    }
}


fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireActivity().showToast(message,duration)
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}
fun View.showSnackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}
inline fun <reified T : Activity> Context.openActivity() {
    startActivity(Intent(this, T::class.java))
}

fun Context.openActivity(activityClass: Class<out Activity>) {
    startActivity(Intent(this, activityClass))
}

inline fun <reified T : Activity> Context.openActivityWithExtras(vararg pairs: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java)
    pairs.forEach { (key, value) ->
        when (value) {
            is String -> intent.putExtra(key, value)
            is Int -> intent.putExtra(key, value)
            is Boolean -> intent.putExtra(key, value)
            // add other types as needed
        }
    }
    startActivity(intent)
}
fun Activity.hideKeyboard() {
    val view = currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
fun Fragment.hideKeyboard() {
    requireActivity().hideKeyboard()
}
private fun ImageView.canLoadImage(): Boolean {
    val context = this.context
    val activity = when (context) {
        is Activity -> context
        is ContextWrapper -> context.baseContext as? Activity
        else -> null
    }
    return activity?.isDestroyed == false && activity?.isFinishing == false
}

// Load from URL
fun ImageView.loadImage(url: String?) {
    if (!canLoadImage()) return
    Glide.with(this.context)
        .load(url)
        .placeholder(android.R.color.darker_gray)
        .into(this)
}

fun ImageView.loadImage(url: File?) {
    if (!canLoadImage()) return
    Glide.with(this.context)
        .load(url)
        .placeholder(android.R.color.darker_gray)
        .into(this)
}

// Load from drawable resource
fun ImageView.loadImage(@DrawableRes resId: Int) {
    if (!canLoadImage()) return
    Glide.with(this.context)
        .load(resId)
        .into(this)
}

// Load from Drawable
fun ImageView.loadImage(drawable: Drawable?) {
    if (!canLoadImage()) return
    Glide.with(this.context)
        .load(drawable)
        .into(this)
}

// Load from Bitmap
fun ImageView.loadImage(bitmap: Bitmap?) {
    if (!canLoadImage()) return
    Glide.with(this.context)
        .load(bitmap)
        .into(this)
}



fun String?.splitToDayMonthAndYear(): Pair<String, String> {
    if (this == null) {
        // Return current date parts if input is null
        val today = LocalDate.now()
        val dayMonth = today.format(DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH))
        val year = today.year.toString()
        return Pair(dayMonth, year)
    }

    return try {
        val inputFormat = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
        val parsedDate = inputFormat.parse(this)

        val dayMonthFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
        val yearFormat = SimpleDateFormat("yyyy", Locale.ENGLISH)

        val dayMonth = dayMonthFormat.format(parsedDate!!)
        val year = yearFormat.format(parsedDate)

        Pair(dayMonth, year)
    } catch (e: Exception) {
        e.printStackTrace()
        val today = LocalDate.now()
        val dayMonth = today.format(DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH))
        val year = today.year.toString()
        Pair(dayMonth, year)
    }
}
fun Intent.putPurchaseModel(keyPrefix: String="PURCHASE_MODEL", model: PurchasePassModelClass) {
    this.putExtra("${keyPrefix}_editId", model?.editId)
    this.putExtra("${keyPrefix}_clubId", model?.clubId)
    this.putExtra("${keyPrefix}_name", model?.name)
    this.putExtra("${keyPrefix}_purchasePrice", model?.purchasePrice)
    this.putExtra("${keyPrefix}_salePrice", model?.salePrice)
    this.putExtra("${keyPrefix}_quantity", model?.quantity)
    this.putExtra("${keyPrefix}_imageUrl", model?.imageUrl)
    this.putExtra("${keyPrefix}_isUpdate", model?.isUpdate)
}
fun Intent.getPurchaseModel(keyPrefix: String="PURCHASE_MODEL"): PurchasePassModelClass? {
    return try {
        PurchasePassModelClass(
            editId = this.getStringExtra("${keyPrefix}_editId") ?: "",
            clubId = this.getStringExtra("${keyPrefix}_clubId") ?: "",
            name = this.getStringExtra("${keyPrefix}_name") ?: "",
            purchasePrice = this.getStringExtra("${keyPrefix}_purchasePrice") ?: "",
            salePrice = this.getStringExtra("${keyPrefix}_salePrice") ?: "",
            quantity = this.getStringExtra("${keyPrefix}_quantity") ?: "",
            imageUrl = this.getStringExtra("${keyPrefix}_imageUrl") ?: "",
            isUpdate = this.getBooleanExtra("${keyPrefix}_isUpdate",false) ?: false
        )
    } catch (e: Exception) {
        null
    }
}

// Extension property for TextView (Date)
var TextView.currentFormattedDate: String
    get() = this.text.toString()
    set(value) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        this.text = dateFormat.format(Date())
    }

// Extension property for TextView (Time)
var TextView.currentFormattedTime: String
    get() = this.text.toString()
    set(value) {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        this.text = timeFormat.format(Date())
    }

fun String.toFormattedDate(inputFormat: String = "yyyy-MM-dd"): String {
    return try {
        val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
        val date = inputDateFormat.parse(this)
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        outputDateFormat.format(date)
    } catch (e: Exception) {
        "" // or handle the error as you prefer
    }
}

// Extension function to parse a String to Date and then format time as HH:mm:ss
fun String.toFormattedTime(inputFormat: String = "HH:mm:ss"): String {
    return try {
        val inputTimeFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
        val date = inputTimeFormat.parse(this)
        val outputTimeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        outputTimeFormat.format(date)
    } catch (e: Exception) {
        "" // or handle the error as you prefer
    }
}