package uz.jagito.todoandnote.ui.adapters

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import kotlinx.android.synthetic.main.todo_item.view.*
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import uz.jagito.todoandnote.utils.extensions.*

class HistoryRecyclerAdapter :
    RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder>(), BaseAdapter {
    private var pos: Int = 0
    private var itemClickListener: SingleBlock<TodoData>? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {

            itemView.apply {
                setOnClickListener {
                    itemClickListener?.invoke(sortedList[adapterPosition])
                }
                btnMore.visibility = View.INVISIBLE
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(position: Int) = bindItem {

            val d = sortedList[adapterPosition]

            todoTitle.text = d.title
            todoDescription.text = d.description
            tags.text = if (d.hashTag.isEmpty()) "" else "#${d.hashTag}"

            if (d.deadline != 0L) {
                deadlineTimeDialog.text = convertLongToTime(d.deadline)
            } else {
                timeIcon.visibility = View.INVISIBLE
                deadlineTimeDialog.visibility = View.INVISIBLE
            }
            status.text = "Status: " + when (d.priority) {
                0.toByte() -> "Free"

                1.toByte() -> "Important"

                2.toByte() -> "Crucial"
                else -> "Free"
            }
            val tomorrow = getTimeByDay(1)
            val threeDay = getTimeByDay(3)
            statusColor.setBackgroundResource(
                when (d.deadline) {
                    in 10..tomorrow -> R.drawable.status_bg_crucial
                    in tomorrow..threeDay -> R.drawable.status_bg_important
                    else -> R.drawable.status_bg_free
                }
            )
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.todo_item))
    }

    override fun getItemCount(): Int = sortedList.size()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(pos)
    }

    private val callback = object : SortedListAdapterCallback<TodoData>(this) {
        override fun areItemsTheSame(item1: TodoData, item2: TodoData): Boolean {
            return item1.id == item2.id
        }

        override fun compare(o1: TodoData, o2: TodoData): Int {
            return if (o1.deadline > o2.deadline) 1 else if (o1.deadline == o2.deadline) 0 else -1
        }

        override fun areContentsTheSame(oldItem: TodoData, newItem: TodoData): Boolean {
            return oldItem == newItem
        }
    }
    private val sortedList = SortedList(TodoData::class.java, callback)

    private fun replaceAll(list: List<TodoData>) {
        sortedList.beginBatchedUpdates()
        sortedList.replaceAll(list)
        sortedList.endBatchedUpdates()
    }

    fun submitList(list: List<TodoData>) {
        replaceAll(list)
    }

    fun setOnClickListener(f: SingleBlock<TodoData>) {
        itemClickListener = f
    }

    override fun update(it: TodoData) {
        var index = -1
        for (i in 0 until itemCount) {
            if (sortedList[i].id == it.id) {
                index = i
                break
            }
        }
        if (index != -1)
            sortedList.updateItemAt(index, it)
    }

    override fun delete(it: TodoData) {
        sortedList.remove(it)
    }

    override fun add(it: TodoData) {
        sortedList.add(it)
    }

    fun submitPosition(pos: Int) {
        this.pos = pos
    }
}
