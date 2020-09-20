package uz.jagito.todoandnote.ui.screens

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_history.*
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.app.App
import uz.jagito.todoandnote.data.ListTodoAndLabelData
import uz.jagito.todoandnote.data.local.room.AppDatabase
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import uz.jagito.todoandnote.ui.adapters.HistoryViewPagerAdapter
import uz.jagito.todoandnote.ui.dialogs.ShowTodo
import uz.jagito.todoandnote.utils.extensions.currentTimeToLong
import java.util.concurrent.Executors

class HistoryActivity : AppCompatActivity() {
    private val executor = Executors.newSingleThreadExecutor()
    private val db = AppDatabase.getDatabase(App.instance)
    private val todoDao = db.todoDao()
    private lateinit var viewPagerAdapter: HistoryViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        executor.execute {
            val passed = getPassedTodoList()
            val done = todoDao.getDone()
            val cancelled = todoDao.getCancelled()

            runOnUiThread {
                loadViewPager(
                    arrayListOf(
                        ListTodoAndLabelData(
                            done as ArrayList<TodoData>, LabelData("Done")
                        ),
                        ListTodoAndLabelData(
                            cancelled as ArrayList<TodoData>, LabelData("Cancelled")
                        ),
                        ListTodoAndLabelData(
                            passed as ArrayList<TodoData>, LabelData("Passed")
                        )
                    )
                )
            }
            backBtnHistory.setOnClickListener { finish() }
        }
    }

    private fun loadViewPager(list: ArrayList<ListTodoAndLabelData>) {
        viewPagerAdapter = HistoryViewPagerAdapter(list, this)
        historyViewPager.adapter = viewPagerAdapter

        TabLayoutMediator(tabLayoutHistory, historyViewPager) { tab, position ->
            tab.text = viewPagerAdapter.list[position].labelData.label
        }.attach()

        viewPagerAdapter.setOnItemClickListener {
            showTodo(it)
        }
    }

    private fun getPassedTodoList(): List<TodoData> {
        val time = currentTimeToLong()
        return todoDao.getTodoBeforeTime(time)
    }

    private fun showTodo(todoData: TodoData) {
        ShowTodo(this).apply {
            setTodoData(todoData)
            show()
        }
    }

}