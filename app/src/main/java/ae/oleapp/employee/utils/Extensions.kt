package ae.oleapp.employee.utils

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

fun AppCompatActivity.findNavController(@IdRes navHostFragmentId: Int): NavController {
    val navHostFragment = supportFragmentManager.findFragmentById(navHostFragmentId)
    return (navHostFragment as? NavHostFragment)?.navController
        ?: throw IllegalStateException("NavHostFragment not found with id: $navHostFragmentId")
}

fun Fragment.findNavController(@IdRes navHostFragmentId: Int): NavController {
    return requireActivity().findNavController(navHostFragmentId)
}

fun String?.toRequestBodyOrNull(): RequestBody? {
    return if (this.isNullOrBlank()) null
    else this.toRequestBody("text/plain".toMediaTypeOrNull())
}