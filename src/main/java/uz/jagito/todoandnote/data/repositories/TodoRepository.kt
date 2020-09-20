package uz.jagito.todoandnote.data.repositories

import uz.jagito.todoandnote.app.App
import uz.jagito.todoandnote.contracts.ContractTodo
import uz.jagito.todoandnote.data.ListTodoAndLabelData
import uz.jagito.todoandnote.data.UserData
import uz.jagito.todoandnote.data.local.room.AppDatabase
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import uz.jagito.todoandnote.data.local.sharedpreference.LocalStorage
import java.util.*
import kotlin.collections.ArrayList

class TodoRepository : ContractTodo.Model {
    private val local = LocalStorage.instance
    private val db = AppDatabase.getDatabase(App.instance)
    private val todoDao = db.todoDao()
    private  val labelDao = db.labelDao()

    override fun getAllData(): List<ListTodoAndLabelData> {
        val calendar = Calendar.getInstance()

        val today = calendar.time.time
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = calendar.time.time

        val todoForToday = todoDao.getTodoByRangeTime(today, tomorrow)
        val l = ArrayList<ListTodoAndLabelData>()
        val k = ArrayList<TodoData>()
        k.addAll(todoDao.getAllActiveTodo())

        val labels = labelDao.getAllLabelsData()
//TODO
        l.add(ListTodoAndLabelData(k, LabelData("All active")))
        l.add(ListTodoAndLabelData(todoForToday as ArrayList<TodoData>, LabelData("Today")))

        labels.forEachIndexed { _, labelData ->
            l.add(ListTodoAndLabelData(todoDao.getTodoByLabelId(labelData.id) as ArrayList<TodoData>,labelData))
        }
        return l
    }

    override fun getDataForToday(): List<TodoData> {
        TODO("Not yet implemented")
    }

    override fun insertData(data: TodoData) = todoDao.insert(data)

    override fun deleteData(data: TodoData) = todoDao.delete(data)

    override fun updateData(data: TodoData) = todoDao.update(data)

    override fun getAllLabels(): List<LabelData> {
        return labelDao.getAllLabelsData()
    }

    override fun deleteTodoByLabelId(ID: Long) {
        val l = todoDao.getTodoByLabelId(ID)
        l.forEach {
            it.deleted = true
            it.labelID = -1
            it.labelName = ""
        }
        todoDao.updateAll(l)
    }

    override fun addLabel(labelData: LabelData): Long {
       return labelDao.insert(labelData)
    }

    override fun deleteLabel(labelData: LabelData) {
        labelDao.delete(labelData)
    }

    override fun saveUserData(userData: UserData) {
        local.email = userData.email
        local.userName = userData.name
        local.avatarUrl = userData.pathOfAvatarImage
    }

    override fun loadUserData(): UserData {
        return UserData(local.userName, local.email, local.avatarUrl)
    }
}