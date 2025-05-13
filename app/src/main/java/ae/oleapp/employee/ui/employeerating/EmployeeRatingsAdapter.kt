package ae.oleapp.employee.ui.employeerating

import ae.oleapp.databinding.ItemEmployeeNoteRvBinding
import ae.oleapp.databinding.ItemEmployeeRatingBinding
import ae.oleapp.employee.data.model.response.GetEmployeeDetails.EmployeeNotes
import ae.oleapp.employee.data.model.response.ReviewData
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class EmployeeRatingsAdapter(private val onRatingClick: (ReviewData) -> Unit) :
RecyclerView.Adapter<EmployeeRatingsAdapter.ViewHolder>() {
    private var ratingList: List<ReviewData> = emptyList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEmployeeRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(ratingList[position])

        holder.itemView.setOnClickListener {
            onRatingClick(ratingList[position])
        }

    }

    override fun getItemCount(): Int {
        return ratingList.size
    }

    fun submitList(newList: List<ReviewData>){
        ratingList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemEmployeeRatingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ratingsData: ReviewData) {
            binding.rating = ratingsData
        }
    }

}