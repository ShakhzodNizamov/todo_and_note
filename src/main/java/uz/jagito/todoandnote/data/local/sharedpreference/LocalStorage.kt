package uz.jagito.todoandnote.data.local.sharedpreference

import android.content.Context
import uz.jagito.todoandnote.utils.extensions.BooleanPreference
import uz.jagito.todoandnote.utils.extensions.StringPreference

class LocalStorage private constructor(context: Context) {

    companion object {
        lateinit var instance: LocalStorage
        fun init(context: Context) {
            instance =
                LocalStorage(context)
        }
    }

    private var pref = context.getSharedPreferences("data", Context.MODE_PRIVATE)

    var isFirstTime by BooleanPreference(pref,true)
    var userName by StringPreference(pref,"UserName")
    var email by StringPreference(pref,"example@gmail.com")
    var avatarUrl by StringPreference(pref,"sample")
}