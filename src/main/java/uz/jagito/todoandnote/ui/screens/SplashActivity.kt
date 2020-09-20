package uz.jagito.todoandnote.ui.screens


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.data.local.sharedpreference.LocalStorage
import java.util.concurrent.Executors

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Executors.newSingleThreadExecutor().execute {
            Thread.sleep(1200)
            if (LocalStorage.instance.isFirstTime) {
                startActivity(Intent(this, IntoActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }
    }
}