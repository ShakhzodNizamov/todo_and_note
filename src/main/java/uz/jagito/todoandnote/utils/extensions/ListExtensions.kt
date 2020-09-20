package uz.jagito.todoandnote.utils.extensions

import com.google.gson.Gson

fun <T> List<T>.toJson(): String {
    return Gson().toJson(this)
}