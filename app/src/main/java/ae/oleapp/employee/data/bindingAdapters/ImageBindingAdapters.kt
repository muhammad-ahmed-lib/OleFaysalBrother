package ae.oleapp.employee.data.bindingAdapters

import ae.oleapp.employee.utils.loadImage
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter

object ImageBindingAdapters {

    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImage(view: ImageView, imageUrl: String?) {
        view.loadImage(imageUrl)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageUri(view: ImageView, imageUri: Uri?) {
        view.setImageURI(imageUri)
    }

    @JvmStatic
    @BindingAdapter("setImageUri")
    fun setImageUri1(view: ImageView, imageUri: Uri?) {
        view.setImageURI(imageUri)
    }
}