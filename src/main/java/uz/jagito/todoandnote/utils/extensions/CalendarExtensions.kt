package uz.jagito.todoandnote.utils.extensions


import uz.jagito.todoandnote.data.Time
import java.util.*

fun Calendar.checkDate(time: Time): Boolean {
    return time.year.toInt() == this[Calendar.YEAR] && time.month.toInt() == this[Calendar.MONTH] && time.day.toInt() == this[Calendar.DAY_OF_MONTH]
}

fun Calendar.checkTime(time: Time): Boolean {
    return time.hour.toInt() < this[Calendar.HOUR_OF_DAY] || time.minute.toInt() < this[Calendar.MINUTE]
}