package ae.oleapp.employee.ui.employeedetail

import ae.oleapp.R
import ae.oleapp.databinding.ItemTabRvBinding
import ae.oleapp.employee.data.model.response.GetClub
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


class EmployeeDetailTabAdapter( private var tabList: List<EmployeeDetailTabsData> , private val onTabSelected: (EmployeeDetailTabsData) -> Unit) :
    RecyclerView.Adapter<EmployeeDetailTabAdapter.ViewHolder>() {
    private var selectedId = ""
    private var selectedIndex = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTabRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val club = tabList[position]
        holder.bind(club, position == selectedIndex)
        holder.itemView.setOnClickListener {
            val previousIndex = selectedIndex
            selectedIndex = position


            notifyItemChanged(previousIndex)
            notifyItemChanged(selectedIndex)
            onTabSelected(club)
        }

    }


    override fun getItemCount(): Int {
        return tabList.size
    }

    inner class ViewHolder(private val binding: ItemTabRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tab: EmployeeDetailTabsData, isSelected: Boolean) {
            binding.titleBtn.text = tab.tabName

            val context = binding.root.context
            val color =
                ContextCompat.getColor(context, if (isSelected) R.color.white else R.color.black)


            binding.mainCardView.strokeColor = color
//            binding.titleBtn.setTextColor(color)
        }
    }

}