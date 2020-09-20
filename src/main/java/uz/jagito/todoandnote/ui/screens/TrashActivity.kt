package uz.jagito.todoandnote.ui.screens

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_trash.*
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.app.App
import uz.jagito.todoandnote.data.local.room.AppDatabase
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import uz.jagito.todoandnote.ui.adapters.TrashRecyclerAdapter
import uz.jagito.todoandnote.ui.dialogs.ShowTodo
import java.util.concurrent.Executors

class TrashActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private val db = AppDatabase.getDatabase(App.instance)
    private val todoDao = db.todoDao()
    private val adapter = TrashRecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)
        setSupportActionBar(trashToolbar)
        supportActionBar?.title = null

        runOnWorkerThread {
            val deletedList = todoDao.getTrashList()
            runOnUIThread {
                adapter.submitList(deletedList)
            }
        }

        listTrash.adapter = adapter
        listTrash.layoutManager = GridLayoutManager(this, 2)

        adapter.setOnClickListener { showTodo(it) }

        adapter.setOnDeleteListener { todo ->
            AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure delete todo?")
                .setPositiveButton("Ok") { _, _ ->
                    showMessage("Delete successful!")
                    runOnWorkerThread {
                        val delete = todoDao.delete(todo)
                        if (delete == 1)
                            runOnUIThread {
                                adapter.delete(todo)
                            }
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()
                .show()
        }

        adapter.setOnRestoreListener {
            AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure restore todo?")
                .setPositiveButton("Ok") { _, _ ->
                    showMessage("Restore successful!")
                    it.apply {
                        deleted = false
                        cancelled = false
                        done = false
                    }
                    runOnWorkerThread {
                        todoDao.update(it)
                        runOnUIThread {
                            adapter.delete(it)
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()
                .show()
        }

        trashBackBtn.setOnClickListener {
            setResult(7)
            finish()
        }

        deleteAllTodoFromTrash.setOnClickListener {
            val d = AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure delete all todo data?")
                .setNegativeButton("NO", null)
                .setPositiveButton("YES") { _, _ ->
                    runOnWorkerThread {
                        todoDao.deleteAllTrashData()
                        runOnUIThread {
                            adapter.submitList(ArrayList<TodoData>())
                        }
                    }
                }
            d.create().show()
        }
    }

    private fun showTodo(it: TodoData) {
        ShowTodo(this).apply {
            setTodoData(it)
            show()
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun runOnWorkerThread(f: () -> Unit) {
        executor.execute { f() }
    }

    private fun runOnUIThread(f: () -> Unit) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            f()
        } else {
            handler.post { f() }
        }
    }

    override fun onBackPressed() {
        setResult(7)
        super.onBackPressed()
    }
}