package ae.oleapp.employee.ui.changeClub

import ae.oleapp.R
import ae.oleapp.databinding.ItemChangeClubListBinding
import ae.oleapp.databinding.ItemEmployeeNoteRvBinding
import ae.oleapp.employee.data.model.response.GetClub
import ae.oleapp.employee.data.model.response.GetEmployeeDetails.EmployeeNotes
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ChangeClubAdapter(private val onClubClick: (GetClub) -> Unit) :
    RecyclerView.Adapter<ChangeClubAdapter.ViewHolder>() {
    private var clubSList: List<GetClub> = emptyList()
    private var selectedPosition: Int = RecyclerView.NO_POSITION


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChangeClubListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clubSList[position] , position , onClubClick)

    }


    override fun getItemCount(): Int {
        return clubSList.size
    }

    fun submitList(newList: List<GetClub>){
        clubSList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemChangeClubListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(clubData: GetClub , position: Int , onClubClick: (GetClub) -> Unit) {
            binding.club = clubData

            binding.selectionIV.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)


                onClubClick(clubData)
            }
            binding.selectionIV.setImageResource(if (selectedPosition == position) R.drawable.club_selected else R.drawable.club_deselect)

        }
    }

}

