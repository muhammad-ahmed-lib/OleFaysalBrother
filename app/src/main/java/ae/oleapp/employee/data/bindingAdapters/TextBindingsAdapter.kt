package ae.oleapp.employee.data.bindingAdapters

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

object TextBindingsAdapter {
    @JvmStatic
    @SuppressLint("DefaultLocale")
    @BindingAdapter("bindAnyText", "defaultText", requireAll = false)
    fun bindAnyText(view: TextView, value: Any?, defaultText: String? = "") {
        val textToShow = when (value) {
            is String -> value
            is Int -> value.toString()
            is Float -> String.format("%.2f", value)
            is Double -> String.format("%.2f", value)
            is Boolean -> if (value) "Yes" else "No"
            else -> defaultText ?: ""
        }
        view.text = if (textToShow.isNotBlank()) textToShow else defaultText ?: ""
    }

    @JvmStatic
    @BindingAdapter("visibleIfEmpty")
    fun <T> View.visibleIfEmpty(list: List<T>?) {
        this.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("goneIfEmpty")
    fun <T> View.goneIfEmpty(list: List<T>?) {
        this.visibility = if (list.isNullOrEmpty()) View.GONE else View.VISIBLE
    }
}