package uz.jagito.todoandnote.ui.presenters

import android.os.Handler
import android.os.Looper
import uz.jagito.todoandnote.contracts.ContractTodo
import uz.jagito.todoandnote.data.UserData
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import java.util.concurrent.Executors

class TodoPresenter(
    private val model: ContractTodo.Model,
    private val view: ContractTodo.View
) : ContractTodo.Presenter {

    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun loadData() {
        runOnWorkerThread {
            val l = model.getAllData()
            val userData = model.loadUserData()
            runOnUIThread {
                view.loadData(l)
                view.setUserData(userData)
            }
        }
    }

    override fun clickAddTodo() {
        view.openAddTodoActivity(-1)
    }

    override fun clickItemSingleNote(ID: Long) {
        view.openAddTodoActivity(ID)
    }

    override fun openAddLabelDialog() {
        runOnWorkerThread {
            val l = model.getAllLabels()
            runOnUIThread {
                view.openAddLabelDialog(l)
            }
        }
    }

    override fun addLabel(labelData: LabelData) {
        runOnWorkerThread {
            val idLabel = model.addLabel(labelData)
            labelData.id = idLabel
            runOnUIThread {
                view.addLabelToAdapter(labelData)
                view.addLabelToViewPagerAdapter(labelData)
            }
        }
    }

    override fun deleteLabel(labelData: LabelData) {
        runOnWorkerThread {
            model.deleteTodoByLabelId(labelData.id)
            model.deleteLabel(labelData)
            runOnUIThread {
                loadData()
                view.deleteLabelFromViewPagerAdapter(labelData)

            }
        }
    }

    override fun onDelete(todoData: TodoData) {
        runOnWorkerThread {
            todoData.deleted = true
            model.updateData(todoData)
            runOnUIThread { view.deleteTodoFromAdapter(todoData) }
        }
    }

    override fun onDone(todoData: TodoData) {
        runOnWorkerThread {
            todoData.done = true
            model.updateData(todoData)
            runOnUIThread { view.deleteTodoFromAdapter(todoData) }
        }
    }

    override fun onCancel(todoData: TodoData) {
        runOnWorkerThread {
            todoData.cancelled = true
            model.updateData(todoData)
            runOnUIThread { view.deleteTodoFromAdapter(todoData) }
        }
    }

    override fun setUserData(userData: UserData) {
        runOnWorkerThread {
            model.saveUserData(userData)
            runOnUIThread {
                view.setUserData(userData)
            }
        }
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