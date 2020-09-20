package uz.jagito.todoandnote.ui.adapters

import android.os.Build
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.jagito.todoandnote.data.ListTodoAndLabelData
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import uz.jagito.todoandnote.ui.fragments.TodoFragment
import uz.jagito.todoandnote.utils.extensions.SingleBlock
import uz.jagito.todoandnote.utils.extensions.putArguments
import uz.jagito.todoandnote.utils.extensions.toJson
import java.util.*
import kotlin.collections.ArrayList

class TodoViewPagerAdapter(
    val list: ArrayList<ListTodoAndLabelData>,
    activity: FragmentActivity
) :
    FragmentStateAdapter(activity) {
    private var listenerItemClick: SingleBlock<TodoData>? = null
    private var listenerEdit: SingleBlock<TodoData>? = null
    private var listenerDelete: SingleBlock<TodoData>? = null
    private var listenerDone: SingleBlock<TodoData>? = null
    private var listenerCancel: SingleBlock<TodoData>? = null

    var index = 0
    private val fragments = ArrayList<TodoFragment>().apply {
        list.forEachIndexed { _, _ ->
            add(TodoFragment())
        }
    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        fragments[position].apply {
            setOnItemClickListener {
                listenerItemClick?.invoke(it)
            }
            adapter.setOnDeleteListener { listenerDelete?.invoke(it) }
            adapter.setOnDoneListener { listenerDone?.invoke(it) }
            adapter.setOnCancelListener { listenerCancel?.invoke(it) }
            adapter.setOnEditListener { listenerEdit?.invoke(it) }
        }
        return fragments[position].putArguments {
            index = position
            putString("key", list[position].listNote.toJson())
            putInt("pos", position)
            putLong("labelId", list[position].labelData.id)
        }
    }

    fun add(todo: TodoData) {

        Log.d("ooop","list size =${list.size}  fragment size = ${fragments.size}")
        list.forEachIndexed { i, listNoteData ->
            if (todo.labelID == listNoteData.labelData.id) {
                val l = ArrayList<TodoData>()
                l.add(todo)
                listNoteData.listNote = l
                fragments[i].adapter.add(todo)
            }
        }
        list[0].listNote.add(todo)
        fragments[0].adapter.add(todo)

        val calendar = Calendar.getInstance()
        val today = calendar.time.time
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = calendar.time.time

        if (todo.deadline in today..tomorrow) {
            fragments[1].adapter.add(todo)
        }
    }

    fun remove(todo: TodoData) {
        list.forEachIndexed { i, listNoteData ->
            var pos = -1
            listNoteData.listNote.forEachIndexed { j, todoData ->
                if (todo.id == todoData.id) {
                    pos = j
                    fragments[i].adapter.delete(todo)
                }
            }

        }
    }

    fun removeLabel(labelData: LabelData) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fragments.removeIf {
               val t =it.arguments
                t != null && t.getLong("labelId") == labelData.id
            }
        }else{
            var i = -1
            list.forEachIndexed { index, listNoteData ->
                if (listNoteData.labelData.id == labelData.id) {
                    i = index
                    fragments.removeAt(index)
                }
            }
            if (i != -1){
                list.removeAt(i)

            }
        }
       notifyDataSetChanged()
    }

    fun update(todo: TodoData) {
        list.forEachIndexed { i, listNoteData ->
            listNoteData.listNote.forEachIndexed { j, todoData ->
                if (todo.id == todoData.id) {
                    fragments[i].adapter.update(todo)
                }
            }
        }
    }

    fun addPage(labelData: LabelData) {
        fragments.add(TodoFragment())
        val newList = ArrayList<TodoData>()
        list.add(ListTodoAndLabelData(newList, labelData))
        notifyItemInserted(list.size - 1)
        Log.d("ooop","list size =${list.size}  fragment size = ${fragments.size}")
       // Log.d("0pp0", "Pages = ${list[list.size - 1].labelData.label}  === ${fragments.size}")
    }

    fun setOnItemClickListener(f: SingleBlock<TodoData>) {
        listenerItemClick = f
    }

    fun setOnEditListener(f: SingleBlock<TodoData>) {
        listenerEdit = f
    }

    fun setOnDeleteListener(f: SingleBlock<TodoData>) {
        listenerDelete = f
    }

    fun setOnDoneListener(f: SingleBlock<TodoData>) {
        listenerDone = f
    }

    fun setOnCancelListener(f: SingleBlock<TodoData>) {
        listenerCancel = f
    }
}