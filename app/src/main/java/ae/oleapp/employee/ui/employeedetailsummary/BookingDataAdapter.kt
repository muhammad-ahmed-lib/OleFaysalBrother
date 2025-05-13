package ae.oleapp.employee.ui.employeedetailsummary

import ae.oleapp.databinding.ItemBookingDataBinding
import ae.oleapp.employee.data.model.response.GetEmployeeDetails.BookingsData
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BookingDataAdapter(private val onBookingDataClick: (BookingsData) -> Unit) :
    RecyclerView.Adapter<BookingDataAdapter.ViewHolder>() {
    private var bookingList: List<BookingsData> = dummyBookingsList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookingDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bookingList[position])

        holder.itemView.setOnClickListener {
            onBookingDataClick(bookingList[position])
        }

    }


    override fun getItemCount(): Int {
        return bookingList.size
    }

    fun submitList(newList: List<BookingsData>){
        bookingList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemBookingDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bookingsData: BookingsData) {
            binding.bookingData = bookingsData
        }
    }
}
val dummyBookingsList = listOf(
    BookingsData(detail = "General Checkup", count = 2, status = "Active"),
    BookingsData(detail = "Dental Cleaning", count = 1, status = "Completed"),
    BookingsData(detail = "Eye Checkup", count = 3, status = "Cancelled"),
    BookingsData(detail = "Orthopedic Consultation", count = 1, status = "Rejected"),
    BookingsData(detail = "ENT Appointment", count = 4, status = "Active")
)