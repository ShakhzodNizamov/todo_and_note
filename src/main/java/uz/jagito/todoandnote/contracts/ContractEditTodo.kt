package uz.jagito.todoandnote.contracts

import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData

interface ContractEditTodo {
    interface Model {
        fun getNote(id: Long): TodoData
        fun getLabels():List<LabelData>
        fun insertNote(note: TodoData): Long
        fun update(note: TodoData): Int
        fun getLabelById(id: Long): LabelData
    }

    interface View {
        var returnCode: Int
        fun getDeadline(): Long
        fun getTitleNote(): String
        fun getDescription(): String

        fun getNoteOf(): String
        fun getHashTag(): String


        fun setDeadline(time: String)
        fun setTitle(title: String)
        fun setDescription(text: String)
        fun setTodoOf(noteOf: String)
        fun setHashTag(tags: String)


        fun finishActivity(addedNote: TodoData)
        fun cancelSave()
        fun showToast(toast: String)
        fun showDatePickerDialog()
        fun showColorDialog()
        fun showLabelsDialog(labels: List<LabelData>)
    }

    interface Presenter {
        var deadlineTime: Long
        fun loadView(id: Long)
        fun openDateTimePickerDialog()
        fun openColorDialog()
        fun openLabelsDialog()
        fun saveData()
        fun setPriority(priority: Byte)
        fun setLabel(label: LabelData)
        fun onClickCancel()
    }
}