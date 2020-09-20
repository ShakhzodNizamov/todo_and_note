package uz.jagito.todoandnote.ui.presenters

import android.os.Handler
import android.os.Looper
import uz.jagito.todoandnote.contracts.ContractAllTodo
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import java.util.concurrent.Executors

class AllTodoPresenter(
    private val view: ContractAllTodo.View,
    private val model: ContractAllTodo.Model
) : ContractAllTodo.Presenter {

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun loadData() {
        runOnWorkerThread {
            val l= model.getAllData()
            runOnUIThread {
                view.loadViewPager(l)
            }
        }
    }

    override fun onDelete(todoData: TodoData) {
        runOnWorkerThread {
            todoData.deleted = true
            model.updateData(todoData)
            runOnUIThread {
                view.removeFromAdapter(todoData)
            }
        }
    }

    override fun onClone(todoData: TodoData) {
        runOnWorkerThread {
            val id = model.cloneData(todoData)

            runOnUIThread {
                if (id >= 0){
                    editTodo(id)
                }
            }
        }
    }

    override fun onCancel(todoData: TodoData) {
        runOnWorkerThread {
            todoData.cancelled = true
            model.updateData(todoData)
            runOnUIThread {
                view.removeFromAdapter(todoData)
                view.addToCancelledList(todoData)
            }
        }
    }

    override fun onDone(todoData: TodoData) {
        runOnWorkerThread {
            todoData.done = true
            model.updateData(todoData)
            runOnUIThread {
                view.removeFromAdapter(todoData)
                view.addToDoneList(todoData)
            }
        }
    }

    override fun editTodo(id: Long) {
        view.editTodo(id)
    }

    override fun showTodo(todoData: TodoData) {
        view.showTodo(todoData)
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
}