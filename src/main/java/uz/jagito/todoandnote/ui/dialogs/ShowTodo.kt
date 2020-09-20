package uz.jagito.todoandnote.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.show_todo_layout.view.*
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import uz.jagito.todoandnote.utils.extensions.convertLongToTime

class ShowTodo(context: Context) : AlertDialog(context) {

    private val contentView =
        LayoutInflater.from(context).inflate(R.layout.show_todo_layout, null, false)


    init {
        setView(contentView)
        contentView.closeDialog.setOnClickListener { dismiss() }
    }

    fun setTodoData(todoData: TodoData) = with(contentView) {
        todoTitleDialog.text = todoData.title
        descriptionDialog.text = todoData.description
        deadlineTimeDialog.text =
            if (todoData.deadline > 0L) convertLongToTime(todoData.deadline) else "--:--"
        hashTagDialog.text = todoData.hashTag
        labelNameDialog.text = todoData.labelName
    }
}