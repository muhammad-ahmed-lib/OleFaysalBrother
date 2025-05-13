package ae.oleapp.employee.ui

import ae.oleapp.booking.bottomSheets.CallSheetFragment
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    fun makeCall(phone: String) {
        val existingFragment = parentFragmentManager.findFragmentByTag("CallSheetFragment")
        if (existingFragment != null) {
            (existingFragment as DialogFragment).dismiss()
        }

        val dialogFragment = CallSheetFragment()
        dialogFragment.setDialogCallback(object : CallSheetFragment.ResultDialogCallback {
            override fun callClicked(df: DialogFragment) {
                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phone")
                }
                startActivity(callIntent)
            }

            override fun callAppClicked(df: DialogFragment) {}

            override fun whatsappClicked(df: DialogFragment) {
                val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/$phone")
                }
                startActivity(whatsappIntent)
            }
        })

        dialogFragment.show(parentFragmentManager, "CallSheetFragment")
    }

}