package uz.jagito.todoandnote.contracts

import uz.jagito.todoandnote.data.ListTodoAndLabelData
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData

interface ContractAllTodo {
    interface Model {
        fun getAllData(): List<ListTodoAndLabelData>
        fun updateData(todoData: TodoData)
        fun cloneData(todoData: TodoData): Long
    }

    interface View {
        fun loadViewPager(list: List<ListTodoAndLabelData>)
        fun removeFromAdapter(todoData: TodoData)
        fun updateFromAdapter(todoData: TodoData)
        fun editTodo(id: Long)
        fun editTodo(todoData: TodoData)
        fun showTodo(todoData: TodoData)
        fun addToCancelledList(todoData: TodoData)
        fun addToDoneList(todoData: TodoData)
    }

    interface Presenter {
        fun loadData()
        fun onDelete(todoData: TodoData)
        fun onClone(todoData: TodoData)
        fun onCancel(todoData: TodoData)
        fun onDone(todoData: TodoData)
        fun editTodo(id: Long)
        fun showTodo(todoData: TodoData)
    }
}