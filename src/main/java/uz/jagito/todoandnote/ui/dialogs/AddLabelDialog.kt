package uz.jagito.todoandnote.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.add_label_dialog.view.*

import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.ui.adapters.AddLabelDialogAdapter
import uz.jagito.todoandnote.utils.extensions.SingleBlock


class AddLabelDialog(
    context: Context,
    private val adapter: AddLabelDialogAdapter
) :
    AlertDialog(context) {

    private val view = LayoutInflater.from(context).inflate(R.layout.add_label_dialog, null, false)

    private var deleteListener: SingleBlock<LabelData>? = null
    private var addLabelListener: SingleBlock<LabelData>? = null

    init {
        view.apply {
            add_labels_recycler.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            add_labels_recycler.adapter = adapter
            adapter.setOnDeleteListener {
                deleteListener?.invoke(it)
            }

            add_label_btn.setOnClickListener {
                // val e = findViewById<TextInputEditText>(R.id.label_name)?.text.toString()
                val name = view.labelName.text.toString()
                // val name = e
                if (name.isEmpty()) {
                    Toast.makeText(context, "Enter label name ", Toast.LENGTH_SHORT).show()
                } else {
                    view.labelName.setText("")
                    addLabelListener?.invoke(LabelData(name))
                }
            }
        }

        setView(view)
        setTitle("Add label")
        view.cancel_label_dialog_btn.setOnClickListener { dismiss() }
    }

    fun setonAddLabelListener(f: SingleBlock<LabelData>) {
        addLabelListener = f
    }

    fun setonDeleteLabelListener(f: SingleBlock<LabelData>) {
        deleteListener = f
    }
}