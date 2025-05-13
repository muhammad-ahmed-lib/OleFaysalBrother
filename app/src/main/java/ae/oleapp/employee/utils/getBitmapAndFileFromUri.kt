package ae.oleapp.employee.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

suspend fun getBitmapAndFileFromUri(
    context: Context,
    uri: Uri
): Pair<Bitmap, File> = withContext(Dispatchers.IO) {
    try {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)

        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
        }

        return@withContext Pair(bitmap, file)
    } catch (e: Exception) {
        e.printStackTrace()
        throw IllegalArgumentException("Failed to load image from URI")
    }
}

suspend fun getFileFromUri(context: Context, uri: Uri): File = withContext(Dispatchers.IO) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open input stream from URI")

        val fileName = "document_${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, fileName)

        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }

        return@withContext file
    } catch (e: Exception) {
        e.printStackTrace()
        throw IllegalArgumentException("Failed to load file from URI")
    }
}
