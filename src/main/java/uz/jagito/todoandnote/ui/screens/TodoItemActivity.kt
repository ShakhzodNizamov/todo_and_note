package uz.jagito.todoandnote.ui.screens


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_todo_item.*
import kotlinx.android.synthetic.main.color_dialog.view.*
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.contracts.ContractEditTodo
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import uz.jagito.todoandnote.data.repositories.NoteActivityRepository
import uz.jagito.todoandnote.ui.adapters.LabelDialogAdapter
import uz.jagito.todoandnote.ui.dialogs.LabelListDialog
import uz.jagito.todoandnote.ui.presenters.EditTodoPresenter
import uz.jagito.todoandnote.utils.extensions.convertLongToTime
import java.util.*


class TodoItemActivity : AppCompatActivity(), ContractEditTodo.View {
    lateinit var presenter: ContractEditTodo.Presenter
    override var returnCode = 5
    var deadlineTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_item)

        presenter = EditTodoPresenter(NoteActivityRepository(), this)
        loadViews()
        if (intent.extras != null) {
            val b = intent.extras!!.getLong("TODO_ID")
            presenter.loadView(b)
        } else
            presenter.loadView(-1)
    }

    private fun loadViews() {
        deadline_time.setOnClickListener {
            showDatePickerDialog()
        }
        block_note_ID.setOnClickListener {
            presenter.openLabelsDialog()
        }
        color_item.setOnClickListener { presenter.openColorDialog() }
        done.setOnClickListener { presenter.saveData() }
        cancel_note_btn.setOnClickListener { presenter.onClickCancel() }
    }

    override fun getDeadline() = deadlineTime

    override fun getTitleNote(): String = title_note.text.toString()

    override fun getDescription(): String = descriptionTodo.text.toString()

    override fun getNoteOf(): String = blockNoteOf.text.toString()

    override fun getHashTag(): String {
        return hash_tag_text.text.toString()
    }

    override fun setDeadline(time: String) {
        deadlineTimeDialog.text = time
    }

    override fun setTitle(title: String) {
        title_note.setText(title)
    }

    override fun setDescription(text: String) {
        descriptionTodo.setText(text)
    }

    override fun setTodoOf(noteOf: String) {
        blockNoteOf.text = noteOf
    }

    override fun setHashTag(tags: String) {
        hash_tag_text.setText(tags)
    }

    override fun finishActivity(addedNote: TodoData) {

        val returnIntent = Intent()
        returnIntent.putExtra("added", addedNote)
        setResult(returnCode, returnIntent)
        finish()
    }

    override fun cancelSave() {
        finish()
    }

    override fun showDatePickerDialog() {
        val now = Calendar.getInstance()
        val dialog =
            DatePickerDialog.newInstance { _, year, month, dayOfMonth ->
                TimePickerDialog.newInstance(
                    { _, hourOfDay, minute, second ->

                        val selectTme = Calendar.getInstance()
                        selectTme.set(year, month, dayOfMonth, hourOfDay, minute, second)

                        deadlineTimeDialog.text = convertLongToTime(selectTme.timeInMillis)
                        presenter.deadlineTime = selectTme.timeInMillis
                    },
                    true
                ).apply {
                    setAccentColor("#005A81")
                    show(supportFragmentManager, "")
                }
            }
        dialog.setAccentColor("#005A81")
        dialog.minDate = now
        dialog.show(supportFragmentManager, "")
    }

    override fun showToast(toast: String) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
    }

    override fun showColorDialog() {
        val view = layoutInflater.inflate(R.layout.color_dialog, null, false)
        val d = AlertDialog.Builder(this)
            .setTitle("Choose priority")
            .setView(view)
            .show()
        view.free.setOnClickListener {
            presenter.setPriority(0)
            d.dismiss()
        }
        view.important.setOnClickListener {
            presenter.setPriority(1)
            d.dismiss()
        }
        view.crucial.setOnClickListener {
            presenter.setPriority(2)
            d.dismiss()
        }
    }

    override fun showLabelsDialog(labels: List<LabelData>) {
        val adapter = LabelDialogAdapter(labels)
        val d = LabelListDialog(this, adapter)
        d.show()

        d.setonItemClickListener {
            presenter.setLabel(it)
            d.dismiss()
        }
    }
}
