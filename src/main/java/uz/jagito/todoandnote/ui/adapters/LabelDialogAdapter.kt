package uz.jagito.todoandnote.ui.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.label_item.view.*
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.utils.extensions.SingleBlock
import uz.jagito.todoandnote.utils.extensions.bindItem
import uz.jagito.todoandnote.utils.extensions.inflate

class LabelDialogAdapter(val list: List<LabelData>):
        RecyclerView.Adapter<LabelDialogAdapter.ViewHolder>(){
    private var itemClickListener: SingleBlock<LabelData>? = null
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        init {
            itemView.setOnClickListener {
                itemClickListener?.invoke(list[adapterPosition])
            }
        }

        fun bind() = bindItem {
            label_name.text = list[adapterPosition].label
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.label_item))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    fun setOnClickItemListener(f:SingleBlock<LabelData>){
        itemClickListener = f
    }
}