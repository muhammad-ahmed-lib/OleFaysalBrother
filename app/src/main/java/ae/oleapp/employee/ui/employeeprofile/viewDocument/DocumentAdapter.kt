package ae.oleapp.employee.ui.employeeprofile.viewDocument

import ae.oleapp.databinding.ItemDocumentRvBinding
import ae.oleapp.employee.data.model.common.DocumentType
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DocumentAdapter(private val onItemClick: (DocumentType) -> Unit) :
    RecyclerView.Adapter<DocumentAdapter.ViewHolder>() {
    private var documentTypeList: List<DocumentType> = DocumentType.entries


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDocumentRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(documentTypeList[position])

        holder.itemView.setOnClickListener {
            onItemClick(documentTypeList[position])
        }

    }

    override fun getItemCount(): Int {
        Log.d("jkasdhfa", "getItemCount: ${documentTypeList.size}")
        return documentTypeList.size
    }

    inner class ViewHolder(private val binding: ItemDocumentRvBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(documentType: DocumentType) {
            binding.tvId.text = documentType.typeName
            binding.iv.setImageResource(documentType.imageResId)
        }
    }

}