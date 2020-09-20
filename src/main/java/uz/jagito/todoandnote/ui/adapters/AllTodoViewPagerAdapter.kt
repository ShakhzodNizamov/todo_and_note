package uz.jagito.todoandnote.ui.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.jagito.todoandnote.data.CANCELLED
import uz.jagito.todoandnote.data.DONE
import uz.jagito.todoandnote.data.ListTodoAndLabelData
import uz.jagito.todoandnote.data.local.room.entities.LabelData
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import uz.jagito.todoandnote.ui.fragments.AllTodoFragment
import uz.jagito.todoandnote.utils.extensions.SingleBlock
import uz.jagito.todoandnote.utils.extensions.putArguments
import uz.jagito.todoandnote.utils.extensions.toJson
import java.util.*
import java.util.function.Predicate
import kotlin.collections.ArrayList

class AllTodoViewPagerAdapter(
    val list: ArrayList<ListTodoAndLabelData>,
    activity: FragmentActivity
) :
    FragmentStateAdapter(activity) {
    private var listenerItemClick: SingleBlock<TodoData>? = null
    private var listenerDelete: SingleBlock<TodoData>? = null
    private var listenerClone: SingleBlock<TodoData>? = null

    private var listenerEdit: SingleBlock<TodoData>? = null
    private var listenerCancelled: SingleBlock<TodoData>? = null
    private var listenerDone: SingleBlock<TodoData>? = null

    var index = 0
    private val fragments = ArrayList<AllTodoFragment>().apply {
        list.forEachIndexed { _, _ ->
            add(AllTodoFragment())
        }
    }

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        fragments[position].apply {
            setOnItemClickListener { listenerItemClick?.invoke(it) }
            adapter.setOnDeleteListener { listenerDelete?.invoke(it) }
            adapter.setOnCloneListener { listenerClone?.invoke(it) }
            adapter.setOnEditListener { listenerEdit?.invoke(it) }
            adapter.setOnCancelListener { listenerCancelled?.invoke(it) }
            adapter.setOnDoneListener { listenerDone?.invoke(it) }
        }
        return fragments[position].putArguments {
            index = position
            putString("key", list[position].listNote.toJson())
            putInt("pos", position)

        }
    }


    fun add(todo: TodoData) {
        list.forEachIndexed { i, listNoteData ->
            if (todo.labelID == listNoteData.labelData.id)
                fragments[i].adapter.add(todo)
        }
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
            listNoteData.listNote.forEachIndexed { j, todoData ->
                if (todo.id == todoData.id) {
                    fragments[i].adapter.delete(todo)
                }
            }
        }
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

    fun addToCancelledList(todo: TodoData){
        list[CANCELLED].listNote.add(todo)
        notifyItemChanged(CANCELLED)
    }

    fun addToDoneList(todo: TodoData){
        list[DONE].listNote.add(todo)
        notifyItemChanged(DONE)
    }


    fun setOnItemClickListener(f: SingleBlock<TodoData>) {
        listenerItemClick = f
    }

    fun setOnDeleteListener(f: SingleBlock<TodoData>) {
        listenerDelete = f
    }

    fun setOnCloneListener(f: SingleBlock<TodoData>) {
        listenerClone = f
    }

    fun setOnDoneListener(f: SingleBlock<TodoData>) {
        listenerDone = f
    }

    fun setOnEditListener(f: SingleBlock<TodoData>) {
        listenerEdit = f
    }

    fun setOnCancelListener(f: SingleBlock<TodoData>) {
        listenerCancelled = f
    }
}