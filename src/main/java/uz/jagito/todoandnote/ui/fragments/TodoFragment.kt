package uz.jagito.todoandnote.ui.fragments


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.todo_page.*
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.data.local.room.entities.TodoData
import uz.jagito.todoandnote.ui.adapters.MainRecyclerAdapter
import uz.jagito.todoandnote.utils.extensions.SingleBlock
import uz.jagito.todoandnote.utils.extensions.toList

class TodoFragment : Fragment(R.layout.todo_page) {
    private var listenerItemClick: SingleBlock<TodoData>? = null
    val list = ArrayList<TodoData>()
     val adapter = MainRecyclerAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = requireArguments()
        list.addAll(bundle.getString("key")!!.toList())
        val pos = bundle.getInt("pos")
        adapter.submitPosition(pos)
        adapter.submitList(list)
        list_main.layoutManager = GridLayoutManager(activity, 2)
        list_main.adapter = adapter
        Log.d("7uu7","TodoFragment - onViewCreated")
        adapter.setOnClickListener {
            listenerItemClick?.invoke(it)
        }
    }

    fun setOnItemClickListener(f: SingleBlock<TodoData>){
        listenerItemClick = f
    }
}