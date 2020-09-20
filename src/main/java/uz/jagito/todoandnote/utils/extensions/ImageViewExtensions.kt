package uz.jagito.todoandnote.utils.extensions

import android.R.attr.path
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.exifinterface.media.ExifInterface
import com.squareup.picasso.Picasso
import uz.jagito.todoandnote.app.App
import java.io.IOException
import java.io.InputStream


fun ImageView.setTint(@ColorRes colorRes: Int) {
    val color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        resources.getColor(colorRes, context.theme)
    } else {
        resources.getColor(colorRes)
    }
    setColorFilter(color)
}

fun ImageView.loadFromUrl(url: String) {
    Picasso.get()
        .load(url)
        .resize(80.dp, 80.dp)
        .centerCrop()
        .into(this)

}

fun Bitmap.rotate(uri: Uri): Bitmap {

    // the URI you've received from the other app
    var inputS: InputStream? = null
    var exifInterface: ExifInterface? = null
    try {
        inputS = App.instance.contentResolver.openInputStream(uri)
        exifInterface =
            ExifInterface(inputS!!)
        // Now you can extract any Exif tag you want
        // Assuming the image is a JPEG or supported raw format
    } catch (e: IOException) {
        // Handle any errors
    } finally {
        if (inputS != null) {
            try {
                inputS.close()
            } catch (ignored: IOException) {
            }
        }
    }

    var degrees = 0f
    if (exifInterface != null) {
        val orientation: Int = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        degrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    }

    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun getAutoRotation(uri: Uri): Float{

    // the URI you've received from the other app
    var inputS: InputStream? = null
    var exifInterface: ExifInterface? = null
    try {
        inputS = App.instance.contentResolver.openInputStream(uri)
        exifInterface =
            ExifInterface(inputS!!)
        // Now you can extract any Exif tag you want
        // Assuming the image is a JPEG or supported raw format
    } catch (e: IOException) {
        // Handle any errors
    } finally {
        if (inputS != null) {
            try {
                inputS.close()
            } catch (ignored: IOException) {
            }
        }
    }

    var degrees = 0f
    if (exifInterface != null) {
        val orientation: Int = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        degrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
    }
    return degrees
}
