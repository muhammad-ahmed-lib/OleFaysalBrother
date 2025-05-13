package ae.oleapp.employee.data.bindingAdapters

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

object EditTextBindingAdapters {

    @BindingAdapter("textStateFlow")
    fun bind(view: EditText, value: String?) {
        if (view.text.toString() != value) {
            view.setText(value)
        }
    }

    @InverseBindingAdapter(attribute = "textStateFlow" , event = "textStateFlowAttrChanged")
    fun get(view: EditText): String {
        return view.text.toString()
    }

    @BindingAdapter("textStateFlowAttrChanged")
    fun setListener(view: EditText, listener: InverseBindingListener?) {
        view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                listener?.onChange()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}