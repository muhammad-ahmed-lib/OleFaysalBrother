package ae.oleapp.employee.ui.employeeDesignation

import ae.oleapp.databinding.ItemDesignationRvBinding
import ae.oleapp.employee.data.model.response.GetDesignationDataResponse
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DesignationsAdapter (private val onItemClicked: (GetDesignationDataResponse) -> Unit): RecyclerView.Adapter<DesignationsAdapter.MyViewHolder>() {
    private var employeeList: List<GetDesignationDataResponse> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDesignationRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding , onItemClicked)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(employeeList[position])
    }

    override fun getItemCount(): Int = employeeList.size

    fun submitList(newList: List<GetDesignationDataResponse>) {
        employeeList = newList
        notifyDataSetChanged()
    }

    class MyViewHolder(private val binding: ItemDesignationRvBinding ,  private val onItemClicked: (GetDesignationDataResponse) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(employee: GetDesignationDataResponse) {
            binding.designation = employee
            binding.destinationCV.setOnClickListener { onItemClicked(employee)  }
        }
    }
}