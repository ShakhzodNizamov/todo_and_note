package uz.jagito.todoandnote.ui.presenters

import android.os.Handler
import android.os.Looper
import uz.jagito.todoandnote.contracts.ContractEditTodo
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import uz.jagito.todoandnote.utils.extensions.convertLongToTime
import uz.jagito.todoandnote.utils.extensions.currentTimeToLong
import java.util.concurrent.Executors

class EditTodoPresenter(
    private val model: ContractEditTodo.Model,
    private val view: ContractEditTodo.View
) : ContractEditTodo.Presenter {
    private var labelData = LabelData("", -1)
    private var priority: Byte = 0
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private var loadNote: TodoData? = null
    override fun loadView(id: Long) {
        if (id != -1L) {

            runOnWorkerThread {
                val note = model.getNote(id)
                labelData =
                    if (note.labelID != -1L)
                        model.getLabelById(note.labelID)
                    else
                        LabelData("", -1)

                loadNote = note
                view.returnCode = 6

                deadlineTime = note.deadline
                runOnUIThread {
                    val deadline = convertLongToTime(note.deadline)

                    if (note.deadline > 0) view.setDeadline(deadline)
                    view.setTitle(note.title)
                    view.setDescription(note.description)
                    view.setTodoOf(labelData.label)
                    view.setHashTag(note.hashTag)
                }
            }
        }
        //TODO check when ID != 0
    }

    override var deadlineTime: Long = 0


    override fun openDateTimePickerDialog() {
        view.showDatePickerDialog()
    }

    override fun openColorDialog() {
        view.showColorDialog()
    }

    override fun openLabelsDialog() {
        runOnWorkerThread {
            val labels = model.getLabels()
            runOnUIThread {
                val test = ArrayList<LabelData>()
                test.addAll(labels)

                view.showLabelsDialog(test)
            }
        }
    }

    override fun saveData() {

        if (view.getDescription().isEmpty()) {
            view.showToast("Please enter description")
        } else {
            if (loadNote == null) {
                loadNote = TodoData(
                    view.getTitleNote(),
                    currentTimeToLong(),
                    deadlineTime,
                    view.getHashTag(),
                    view.getDescription(),
                    priority,
                    false,
                    done = false,
                    cancelled = false,
                    outOfTime = false,
                    labelID = labelData.id,
                    labelName = labelData.label
                )
                runOnWorkerThread {
                    val id = model.insertNote(loadNote!!)
                    runOnUIThread {
                        loadNote!!.id = id
                        view.finishActivity(loadNote!!)
                    }
                }
            } else {
                loadNote!!.title = view.getTitleNote()
                loadNote!!.date = currentTimeToLong()
                loadNote!!.deadline = deadlineTime
                loadNote!!.hashTag = view.getHashTag()
                loadNote!!.description = view.getDescription()
                loadNote!!.labelID = labelData.id
                loadNote!!.labelName = labelData.label
                if (currentTimeToLong() < deadlineTime) {
                    loadNote!!.done = false
                    loadNote!!.cancelled = false
                }

                runOnWorkerThread {
                    model.update(loadNote!!)
                    runOnUIThread {
                        view.finishActivity(loadNote!!)
                    }
                }
            }
        }
    }

    override fun setPriority(priority: Byte) {
        this.priority = priority
    }

    override fun onClickCancel() {
        view.cancelSave()
    }

    override fun setLabel(label: LabelData) {
        labelData = label
        view.setTodoOf(label.label)
    }

    private fun runOnWorkerThread(f: () -> Unit) {
        executor.execute { f() }
    }

    private fun runOnUIThread(f: () -> Unit) {
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            f()
        } else {
            handler.post { f() }
        }
    }
}