package ae.oleapp.utils
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

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