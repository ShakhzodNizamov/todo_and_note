package uz.jagito.todoandnote.data

import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData

data class ListTodoAndLabelData(var listNote: ArrayList<TodoData>, val labelData: LabelData){
    fun remove(todoData: TodoData){

    }
}