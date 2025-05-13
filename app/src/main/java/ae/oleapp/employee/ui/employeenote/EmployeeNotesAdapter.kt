package ae.oleapp.employee.ui.employeenote


import ae.oleapp.databinding.ItemEmployeeNoteRvBinding
import ae.oleapp.employee.data.model.response.GetEmployeeDetails.EmployeeNotes
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class EmployeeNotesAdapter(private val onNoteClick: (EmployeeNotes) -> Unit,
    private val onDeleteClick: (Int) -> Unit) :
    RecyclerView.Adapter<EmployeeNotesAdapter.ViewHolder>() {
    private var notesList: List<EmployeeNotes> = emptyList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEmployeeNoteRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notesList[position])
    }


    override fun getItemCount(): Int {
        return notesList.size
    }

    fun submitList(newList: List<EmployeeNotes>){
        Log.d(",kajfkls;", "submitList: $newList")
        notesList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemEmployeeNoteRvBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notesData: EmployeeNotes) {
            binding.note = notesData

            binding.root.setOnClickListener {
                onNoteClick(notesData)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(notesData.id ?: 0)
            }
        }
    }

}