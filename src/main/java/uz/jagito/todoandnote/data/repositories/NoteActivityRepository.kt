package uz.jagito.todoandnote.data.repositories

import uz.jagito.todoandnote.app.App
import uz.jagito.todoandnote.contracts.ContractEditTodo
import uz.jagito.todoandnote.data.local.room.AppDatabase
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData

class NoteActivityRepository : ContractEditTodo.Model {
    private val db = AppDatabase.getDatabase(App.instance)
    private val todoDao = db.todoDao()
    private val labelDao = db.labelDao()

    override fun getNote(id: Long): TodoData = todoDao.getTodoData(id)[0]

    override fun getLabels(): List<LabelData> {
        return labelDao.getAllLabelsData()
    }

    override fun insertNote(note: TodoData): Long = todoDao.insert(note)

    override fun update(note: TodoData): Int = todoDao.update(note)

    override fun getLabelById(id: Long): LabelData {
        return labelDao.getLabelData(id)
    }
}