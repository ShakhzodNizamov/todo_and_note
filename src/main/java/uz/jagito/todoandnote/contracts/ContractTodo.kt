package uz.jagito.todoandnote.contracts

import uz.jagito.todoandnote.data.ListTodoAndLabelData
import uz.jagito.todoandnote.data.UserData
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData

interface ContractTodo {
    interface Model {
        fun getAllData(): List<ListTodoAndLabelData>
        fun getDataForToday(): List<TodoData>
        fun insertData(data: TodoData): Long
        fun deleteData(data: TodoData): Int
        fun updateData(data: TodoData): Int
        fun getAllLabels(): List<LabelData>
        fun deleteTodoByLabelId(ID: Long)
        fun addLabel(labelData: LabelData): Long
        fun deleteLabel(labelData: LabelData)
        fun saveUserData(userData: UserData)
        fun loadUserData(): UserData
    }

    interface View {
        fun loadData(data: List<ListTodoAndLabelData>)
        fun openAddTodoActivity(ID: Long)
        fun openAddLabelDialog(list: List<LabelData>)
        /*fun openConfirmDeleteDialog(): Boolean*/
        fun addLabelToAdapter(labelData: LabelData)
        fun deleteLabelFromViewPagerAdapter(labelData: LabelData)
        fun addLabelToViewPagerAdapter(labelData: LabelData)
        fun deleteTodoFromAdapter(todoData: TodoData)
        fun updateTodoFromAdapter(todoData: TodoData)
        fun setUserData(userData: UserData)
    }

    interface Presenter {

        fun loadData()
        fun clickAddTodo()
        fun clickItemSingleNote(ID: Long)
        fun openAddLabelDialog()
        fun addLabel(labelData: LabelData)
        fun deleteLabel(labelData: LabelData)
        fun onDelete(todoData: TodoData)
        fun onDone(todoData: TodoData)
        fun onCancel(todoData: TodoData)
        fun setUserData(userData: UserData)
    }
}