package uz.jagito.todoandnote.utils.extensions

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import java.text.SimpleDateFormat
import java.util.*


fun String.toLongDate(pattern: String = "dd MM yyyy HH:mm"): Long =
    SimpleDateFormat(pattern, Locale.getDefault()).parse(this).time

fun String.toDate(): Date =
    SimpleDateFormat("dd MM yyyy HH:mm", Locale.getDefault()).parse(this)

fun String.toList(): List<TodoData> {
    val type = object : TypeToken<List<TodoData>>() {}.type
    return Gson().fromJson(this, type)
}

fun String.toTagList(): List<String> {
    val type = object : TypeToken<List<String>>() {}.type
    return Gson().fromJson(this, type)
}


