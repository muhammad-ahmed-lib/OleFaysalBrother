package ae.oleapp.employee.ui.bookingHistory

import ae.oleapp.R
import ae.oleapp.databinding.ItemBookingFilterRvBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class BookingFilterAdapter(private val onFilterClick: (FilterItem) -> Unit) :
    RecyclerView.Adapter<BookingFilterAdapter.ViewHolder>() {
    private var clubSList: List<FilterItem> = filters
    private var selectedPosition: Int = RecyclerView.NO_POSITION


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookingFilterRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clubSList[position] , position , onFilterClick)

    }


    override fun getItemCount(): Int {
        return clubSList.size
    }

    fun submitList(newList: List<FilterItem>){
        clubSList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemBookingFilterRvBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(filterData: FilterItem, position: Int, onClubClick: (FilterItem) -> Unit) {

            binding.titleTV.text = filterData.title.title

            binding.selectionIV.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)


                onClubClick(filterData)
            }
            binding.selectionIV.setImageResource(if (selectedPosition == position) R.drawable.club_selected else R.drawable.club_deselect)

        }
    }

}
enum class FilterType(val title: String) {
    LAST_6_MONTHS("Last 6 Months"),
    LAST_3_MONTHS("Last 3 Months"),
    LAST_MONTH("Last Month"),
    JANUARY("January"),
    FEBRUARY("February"),
    MARCH("March"),
}
data class FilterItem(
    val title: FilterType,
    var isSelected: Boolean = false
)
val filters = listOf(
    FilterItem(FilterType.LAST_6_MONTHS),
    FilterItem(FilterType.LAST_3_MONTHS),
    FilterItem(FilterType.LAST_MONTH),
    FilterItem(FilterType.JANUARY),
    FilterItem(FilterType.FEBRUARY),
    FilterItem(FilterType.MARCH)
)

