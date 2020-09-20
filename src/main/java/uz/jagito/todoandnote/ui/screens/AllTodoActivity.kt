package uz.jagito.todoandnote.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_all_todo.*
import kotlinx.android.synthetic.main.activity_main_content.toolbar
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.contracts.ContractAllTodo
import uz.jagito.todoandnote.data.ListTodoAndLabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import uz.jagito.todoandnote.data.repositories.AllTodoRepository
import uz.jagito.todoandnote.ui.adapters.AllTodoViewPagerAdapter
import uz.jagito.todoandnote.ui.dialogs.ShowTodo
import uz.jagito.todoandnote.ui.presenters.AllTodoPresenter
import java.util.*
import kotlin.collections.ArrayList

class AllTodoActivity : AppCompatActivity(), ContractAllTodo.View {
    private lateinit var presenter: AllTodoPresenter
    private lateinit var viewPagerAdapter: AllTodoViewPagerAdapter
    private val requestCode = 6
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_todo)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null

        presenter = AllTodoPresenter(this, AllTodoRepository())
        presenter.loadData()
        loadView()
    }

    private fun loadView() {
        backBtn.setOnClickListener {
            setResult(7)
            finish()
        }
    }

    override fun loadViewPager(list: List<ListTodoAndLabelData>) {
        viewPagerAdapter = AllTodoViewPagerAdapter(ArrayList(list), this)
        allTodoViewPager.adapter = viewPagerAdapter

        viewPagerAdapter.setOnDeleteListener { showConfirm("Delete", it) }
        viewPagerAdapter.setOnCloneListener { showConfirm("Clone", it) }
        viewPagerAdapter.setOnCancelListener { showConfirm("Cancel", it) }
        viewPagerAdapter.setOnDoneListener { showConfirm("Done", it) }
        viewPagerAdapter.setOnEditListener { presenter.editTodo(it.id) }
        viewPagerAdapter.setOnItemClickListener { presenter.showTodo(it) }

        TabLayoutMediator(tabLayoutAllTodo, allTodoViewPager) { tab, position ->
            tab.text = viewPagerAdapter.list[position].labelData.label
        }.attach()
    }

    override fun removeFromAdapter(todoData: TodoData) {
        viewPagerAdapter.remove(todoData)
    }

    override fun updateFromAdapter(todoData: TodoData) {
        viewPagerAdapter.update(todoData)
    }

    override fun editTodo(id: Long) {
        val data = Intent(this, TodoItemActivity::class.java)
        data.putExtra("TODO_ID", id)
        startActivityForResult(data, requestCode)
    }

    override fun editTodo(todoData: TodoData) {
        val data = Intent()
        data.putExtra("TODO_ID", todoData.id)
        startActivityForResult(data, requestCode)
    }

    override fun showTodo(todoData: TodoData) {
        ShowTodo(this).apply {
            setTodoData(todoData)
            show()
        }
    }

    override fun addToCancelledList(todoData: TodoData) {
        viewPagerAdapter.addToCancelledList(todoData)
    }

    override fun addToDoneList(todoData: TodoData) {
        viewPagerAdapter.addToDoneList(todoData)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*
        if (resultCode == 6 && requestCode == resultCode) {
            val note = data?.getSerializableExtra("added") as TodoData
            viewPagerAdapter.add(note)
        } */
        presenter.loadData()
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showConfirm(key: String, todoData: TodoData) {
        AlertDialog.Builder(this)
            .setTitle("Confirm")
            .setMessage("Are you sure ${key.toLowerCase(Locale.getDefault())} todo?")
            .setPositiveButton(key) { _, _ ->
                showMessage("$key successful!")
                when(key){
                    "Delete" ->{ presenter.onDelete(todoData) }
                    "Clone" ->{presenter.onClone(todoData)}
                    "Cancel" ->{presenter.onCancel(todoData)}
                    "Done" ->{presenter.onDone(todoData)}
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun showMessage(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        setResult(7)
        super.onBackPressed()
    }
}