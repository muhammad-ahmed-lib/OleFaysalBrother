package ae.oleapp.employee.ui.employeesalary

import ae.oleapp.databinding.ItemEmployeeListBinding
import ae.oleapp.databinding.ItemEmployeeSalaryListBinding
import ae.oleapp.employee.data.model.response.GetAllEmployeeResponse
import ae.oleapp.employee.data.model.response.GetEmployeeDetails.SalaryData
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class SalaryListAdapter (): RecyclerView.Adapter<SalaryListAdapter.MyViewHolder>() {
    private var employeeSalaryList: List<SalaryData> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemEmployeeSalaryListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(employeeSalaryList[position])
    }

    override fun getItemCount(): Int = employeeSalaryList.size

    fun submitList(newList: List<SalaryData>) {
        employeeSalaryList = newList
        notifyDataSetChanged()
    }

    class MyViewHolder(private val binding: ItemEmployeeSalaryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(employeeSalary: SalaryData) {
            binding.salary = employeeSalary
        }
    }
}