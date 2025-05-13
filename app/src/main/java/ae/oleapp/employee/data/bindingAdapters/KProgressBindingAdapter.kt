package ae.oleapp.employee.data.bindingAdapters

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import com.kaopiz.kprogresshud.KProgressHUD

object KProgressBindingAdapter {

    @JvmStatic
    @BindingAdapter("showKProgress")
    fun showKProgress(view: View, show: Boolean) {
        val context = view.context
        val activity = context as? AppCompatActivity ?: return

        val tag = "kprogress_loader"
        val existingHud = view.getTag(tag.hashCode()) as? KProgressHUD

        if (show) {
            if (existingHud == null) {
                val hud = KProgressHUD.create(activity)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show()

                view.setTag(tag.hashCode(), hud)
            }
        } else {
            existingHud?.dismiss()
            view.setTag(tag.hashCode(), null)
        }
    }


}
