package uz.jagito.todoandnote.data.repositories

import uz.jagito.todoandnote.app.App
import uz.jagito.todoandnote.contracts.ContractAllTodo
import uz.jagito.todoandnote.data.ListTodoAndLabelData
import uz.jagito.todoandnote.data.local.room.AppDatabase
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import uz.jagito.todoandnote.utils.extensions.currentTimeToLong

class AllTodoRepository : ContractAllTodo.Model {
    private val db = AppDatabase.getDatabase(App.instance)
    private val todoDao = db.todoDao()


    override fun getAllData(): List<ListTodoAndLabelData> {
        val cancelled = todoDao.getCancelled()
        val done = todoDao.getDone()
        val passed = getPassedTodoList()
        val haveTime = getHaveTimeTodoList()

        return arrayListOf(
            ListTodoAndLabelData(haveTime as ArrayList<TodoData>, LabelData("Have Time")),
            ListTodoAndLabelData(passed as ArrayList<TodoData>, LabelData("Passed")),
            ListTodoAndLabelData(done as ArrayList<TodoData>, LabelData("Done")),
            ListTodoAndLabelData(cancelled as ArrayList<TodoData>, LabelData("Cancelled"))
        )
    }

    override fun updateData(todoData: TodoData) {
        todoDao.update(todoData)
    }

    override fun cloneData(t: TodoData): Long {
        return todoDao.insert(TodoData(
            t.title,
            t.date,
            t.deadline,
            t.hashTag,
            t.description,
            t.priority,
            t.deleted,
            t.done,
            t.cancelled,
            t.outOfTime,
            t.labelID,
            t.labelName
        ))
    }



    private fun getPassedTodoList(): List<TodoData> {
        val time = currentTimeToLong()
        return todoDao.getTodoBeforeTime(time)
    }

    private fun getHaveTimeTodoList(): List<TodoData> {
        val time = currentTimeToLong()
        return todoDao.getTodoAfterTime(time)
    }
}