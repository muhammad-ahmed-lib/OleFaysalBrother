package ae.oleapp.employee.ui.addemployee.adapter

import ae.oleapp.R
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TabAdapter (
    private val context : Context,
    private val taskList : List<String>,
    private val onItemClicked : (Int) -> Unit
): RecyclerView.Adapter<TabAdapter.TaskHomeViewHolder>() {

    private var selectedPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHomeViewHolder {
        val view  = LayoutInflater.from(context).inflate(R.layout.item_tab_rv,parent,false)
        return TaskHomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskHomeViewHolder, position: Int) {

        val data = taskList[position]

        holder.timeTV.text = data


        if (position == selectedPosition) {
//            holder.mainCardView.backgroundTintList = context.getColorStateList(R.color.color_primary)
        } else {
//            holder.mainCardView.backgroundTintList = context.getColorStateList(R.color.white)
        }

        holder.mainCardView.setOnClickListener {
            onItemClicked(position)
            val previousSelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selectedPosition)
        }


    }


    class TaskHomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val mainCardView = itemView.findViewById<LinearLayout>(R.id.mainCardView)
        val timeTV = itemView.findViewById<TextView>(R.id.titleBtn)

    }
}