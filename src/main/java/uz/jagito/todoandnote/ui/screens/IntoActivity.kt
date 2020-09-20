package uz.jagito.todoandnote.ui.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_into.*
import kotlinx.android.synthetic.main.activity_into.pager
import kotlinx.android.synthetic.main.activity_main_content.*
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.data.IntroData
import uz.jagito.todoandnote.data.local.sharedpreference.LocalStorage
import uz.jagito.todoandnote.ui.adapters.IntroPageAdapter

class IntoActivity : AppCompatActivity() {
    private val local = LocalStorage.instance
    private val data = arrayListOf(
        IntroData(
            R.drawable.ic_timer,
            "Set deadlines for your tasks and set reminders"
        ),
        IntroData(
            R.drawable.ic_pinned_notes,
            "Pin issues, easily find your notes using tags"
        ),
        IntroData(
            R.drawable.ic_todo_list,
            "Make a plan of actions, make notes on completed tasks"
        )
    )
    private val adapter = IntroPageAdapter(data)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_into)

        pager.adapter = adapter
        TabLayoutMediator(tabLayoutInto, pager) { tab, possition -> }.attach()

        buttonNext.setOnClickListener {
            if (pager.currentItem != data.size - 1) {
                pager.setCurrentItem(pager.currentItem + 1, true)
            } else {
                local.isFirstTime = false
                finish()
                startActivity(Intent(this,MainActivity::class.java))
            }
        }
    }
}