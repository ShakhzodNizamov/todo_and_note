package uz.jagito.todoandnote.ui.dialogs

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager

import kotlinx.android.synthetic.main.label_list_dialog.view.*
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.ui.adapters.LabelDialogAdapter
import uz.jagito.todoandnote.utils.extensions.SingleBlock


class LabelListDialog(
    context: Context,
    private val adapter: LabelDialogAdapter
) :
    AlertDialog(context) {

    private val view = layoutInflater.inflate(R.layout.label_list_dialog, null, false)

    private var listener: SingleBlock<LabelData>? = null

    init {
        view.labels_recycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        view.labels_recycler.adapter = adapter
        adapter.setOnClickItemListener {
            listener?.invoke(it)
        }

        setView(view)
        setTitle("Choose label for todo")
        view.cancel_label_dialog_btn.setOnClickListener { dismiss() }
    }

    fun setonItemClickListener(f: SingleBlock<LabelData>) {
        listener = f
    }
}