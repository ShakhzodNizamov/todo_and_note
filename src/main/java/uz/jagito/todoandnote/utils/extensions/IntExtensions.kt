package uz.jagito.todoandnote.utils.extensions

import android.content.res.Resources

val Int.px: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
