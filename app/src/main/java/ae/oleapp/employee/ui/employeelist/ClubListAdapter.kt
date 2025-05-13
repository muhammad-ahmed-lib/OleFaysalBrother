package ae.oleapp.employee.ui.employeelist

import ae.oleapp.R
import ae.oleapp.databinding.ItemTabRvBinding
import ae.oleapp.employee.data.model.response.GetClub
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


class ClubListAdapter(private val onClubSelected: (GetClub) -> Unit) :
    RecyclerView.Adapter<ClubListAdapter.ViewHolder>() {
    private var selectedId = ""
    private var selectedIndex = 0
    private var clubList: List<GetClub> = emptyList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTabRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val club = clubList[position]
        holder.bind(club, position == selectedIndex)
        holder.itemView.setOnClickListener {
            val previousIndex = selectedIndex
            selectedIndex = position


            notifyItemChanged(previousIndex)
            notifyItemChanged(selectedIndex)
            onClubSelected(club)
        }

    }

    fun submitList(clubs: List<GetClub>) {
        clubList = clubs
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return clubList.size
    }

    inner class ViewHolder(private val binding: ItemTabRvBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(club: GetClub, isSelected: Boolean) {
            binding.titleBtn.text = club.name

            val context = binding.root.context
            val color =
                ContextCompat.getColor(context, if (isSelected) R.color.white else R.color.black)


            binding.mainCardView.setStrokeColor(color)
//            binding.titleBtn.setTextColor(color)
        }
    }

}