package uz.jagito.todoandnote.app

import android.app.Application
import uz.jagito.todoandnote.data.local.sharedpreference.LocalStorage

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        LocalStorage.init(this)
    }

    companion object {
        lateinit var instance: App
    }
}
