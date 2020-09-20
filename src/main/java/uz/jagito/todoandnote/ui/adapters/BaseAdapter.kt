package uz.jagito.todoandnote.ui.adapters

import uz.jagito.todoandnote.data.local.room.entities.TodoData


interface BaseAdapter {
    fun update(it:TodoData)
    fun delete(it: TodoData)
    fun add(it: TodoData)
}