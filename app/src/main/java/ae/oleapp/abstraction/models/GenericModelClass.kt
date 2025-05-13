package ae.oleapp.abstraction.models

import android.app.Activity


data class GenericModelClass(
    val title:String,
    val icon:Int,
    val screen: Class<out Activity>? = null
)