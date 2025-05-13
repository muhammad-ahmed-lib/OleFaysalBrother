package ae.oleapp.employee.utils

import ae.oleapp.R
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadImage(url: String?, placeHolder: Int = R.drawable.ic_launcher_background) {

    Glide.with(this).load(url)
        .transition(DrawableTransitionOptions.withCrossFade())
        .apply(RequestOptions())
//        .placeholder(placeHolder)
        .into(this)
}


fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}