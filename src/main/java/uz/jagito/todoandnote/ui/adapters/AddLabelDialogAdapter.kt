package uz.jagito.todoandnote.ui.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.add_label_item.view.*
import kotlinx.android.synthetic.main.label_item.view.label_name
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.utils.extensions.SingleBlock
import uz.jagito.todoandnote.utils.extensions.bindItem
import uz.jagito.todoandnote.utils.extensions.inflate

class AddLabelDialogAdapter :
    RecyclerView.Adapter<AddLabelDialogAdapter.ViewHolder>() {
    private val list = ArrayList<LabelData>()
    private var itemDeleteListener: SingleBlock<LabelData>? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.delete_label_btn.setOnClickListener {
                itemDeleteListener?.invoke(list[adapterPosition])
            }
        }

        fun bind() = bindItem {
            label_name.text = list[adapterPosition].label
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.add_label_item))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    fun setOnDeleteListener(f: SingleBlock<LabelData>) {
        itemDeleteListener = f
    }

    fun addLabel(labelData: LabelData) {
        list.add(labelData)
        notifyItemChanged(list.size - 1)
    }

    fun deleteLabel(label: LabelData) {
        list.remove(label)
        notifyDataSetChanged()
    }

    fun submitList(labels: List<LabelData>) {
        list.clear()
        list.addAll(labels)
        notifyDataSetChanged()
    }
}