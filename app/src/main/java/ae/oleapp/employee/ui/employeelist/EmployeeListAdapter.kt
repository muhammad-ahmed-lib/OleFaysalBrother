package ae.oleapp.employee.ui.employeelist

import ae.oleapp.databinding.ItemEmployeeListBinding
import ae.oleapp.employee.data.model.response.GetAllEmployeeResponse
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class EmployeeListAdapter (private val onItemClick: (GetAllEmployeeResponse) -> Unit,
    private val onCallClicked: (String) -> Unit): RecyclerView.Adapter<EmployeeListAdapter.MyViewHolder>() {
    private var employeeList: List<GetAllEmployeeResponse> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemEmployeeListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding , onItemClick , onCallClicked)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(employeeList[position])
    }

    override fun getItemCount(): Int = employeeList.size

    fun submitList(newList: List<GetAllEmployeeResponse>) {
        employeeList = newList
        notifyDataSetChanged()
    }

    class MyViewHolder(private val binding: ItemEmployeeListBinding , private val onItemClick: (GetAllEmployeeResponse) -> Unit , private val onCallClicked: (String) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(employee: GetAllEmployeeResponse) {
           binding.employee = employee
            binding.mainCV.setOnClickListener { onItemClick(employee) }
            binding.btnCall.setOnClickListener { onCallClicked(employee.phone ?: "")  }
        }
    }
}