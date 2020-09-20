package uz.jagito.todoandnote.ui.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.page_into.view.*
import uz.jagito.todoandnote.R
import uz.jagito.todoandnote.data.IntroData
import uz.jagito.todoandnote.utils.extensions.bindItem
import uz.jagito.todoandnote.utils.extensions.inflate

class IntroPageAdapter (private val data: List<IntroData>) : RecyclerView.Adapter<IntroPageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.page_into))
    override fun getItemCount(): Int = data.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() = bindItem {
            val d = data[adapterPosition]
            imageIcon.setImageResource(d.imgID)
            textDescription.text = d.description
        }
    }
}