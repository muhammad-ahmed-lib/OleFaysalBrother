package ae.oleapp.employee.ui.bookingHistory

import ae.oleapp.databinding.ItemBookingHistoryRvBinding
import ae.oleapp.databinding.ItemChangeClubListBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BookingHistoryAdapter : RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookingHistoryRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return  5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    class ViewHolder(private val binding: ItemBookingHistoryRvBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}