package ae.oleapp.employee.utils

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.File


fun Context.downloadMedia(fileUrl: String, fileName: String = "file.pdf") {
    try {
        val request = DownloadManager.Request(Uri.parse(fileUrl)).apply {
            setTitle("Downloading")
            setDescription("Please wait...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }

        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    } catch (e: Exception){
        CustomToast.makeFancyToast(e.localizedMessage ?:"Something Went Wrong", CustomToast.ERROR)
    }

}

fun shareContent(context: Context, contentUri: Uri?, contentUrl: String?, mimeType: String) {
    val shareIntent = Intent(Intent.ACTION_SEND)

    // Case 1: Local File (Image/PDF) sharing
    if (contentUri != null) {
        // FileProvider se content URI generate karo for security
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.type = mimeType
    }
    // Case 2: URL sharing
    else if (!contentUrl.isNullOrEmpty()) {
        shareIntent.putExtra(Intent.EXTRA_TEXT, contentUrl)
        shareIntent.type = "text/plain"
    } else {
        // Invalid case: na file hai na URL
        return
    }

    // Chooser intent to let user select app
    val chooserIntent = Intent.createChooser(shareIntent, "Share via")
    context.startActivity(chooserIntent)
}

// Utility to get MIME type based on file extension
fun getMimeType(file: File): String {
    return when (file.extension.lowercase()) {
        "jpg", "jpeg", "png" -> "image/*"
        "pdf" -> "application/pdf"
        else -> "*/*" // Fallback MIME type
    }
}