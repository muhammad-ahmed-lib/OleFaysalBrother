package ae.oleapp.employee.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.shashank.sony.fancytoastlib.FancyToast

@SuppressLint("StaticFieldLeak")
object CustomToast {

    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }


    fun makeToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun makeFancyToast(message: String , type : Int){
        Handler(Looper.getMainLooper()).post {
            FancyToast.makeText(context, message, FancyToast.LENGTH_LONG, type, false).show();
        }
    }


    const val SUCCESS: Int = 1
    const val WARNING: Int = 2
    const val ERROR: Int = 3
    const val INFO: Int = 4
    const val DEFAULT: Int = 5
    const val CONFUSING: Int = 6


}