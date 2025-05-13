package ae.oleapp.employee.ui.employeeDesignation

import ae.oleapp.databinding.ItemDesignationPermissionRvBinding
import ae.oleapp.databinding.ItemDesignationRvBinding
import ae.oleapp.employee.data.model.response.GetDesignationDataResponse
import ae.oleapp.employee.data.model.response.Permission
import ae.oleapp.employee.utils.toggleSwitch
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class PermissionAdapter : RecyclerView.Adapter<PermissionAdapter.MyViewHolder>() {
    private var permissionList: List<Permission> = emptyList()

     var toggledIds = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemDesignationPermissionRvBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val permission = permissionList[position]
        holder.bind(permission) { id, isChecked ->

        }
    }

    override fun getItemCount(): Int = permissionList.size

    fun submitList(newList: List<Permission>) {
        permissionList = newList
        notifyDataSetChanged()
    }

    fun preSelectedPermission(selectedPermission: List<Permission>) {
        toggledIds =  selectedPermission.mapNotNull { it.id }.toMutableSet()
    }

    fun getSelectedPermissionIds():String{
        return toggledIds.joinToString(",")
    }

    inner class MyViewHolder(private val binding: ItemDesignationPermissionRvBinding) : RecyclerView.ViewHolder(binding.root) {

        private var isToggled = false

//        fun bind(permission: Permission, onToggle: (id: Int, isChecked: Boolean) -> Unit) {
//            binding.permission = permission
//
//            binding.switchLayout.setOnClickListener {
//                isToggled = !isToggled
//                toggleSwitch(
//                    isToggled,
//                    binding.switchLayout,
//                    binding.toggleCircle
//                )
//                permission.id?.let {  onToggle(it, isToggled) }
//            }
//        }
fun bind(permission: Permission, onToggle: (id: Int, isChecked: Boolean) -> Unit) {
    binding.permission = permission
    val isToggled = toggledIds.contains(permission.id)

    // Set initial position of the toggle UI based on isToggled
    if (isToggled) {
        binding.switchLayout.post {
            toggleSwitch(true, binding.switchLayout, binding.toggleCircle)
        }
    } else {
        binding.switchLayout.post {
            toggleSwitch(false, binding.switchLayout, binding.toggleCircle)
        }
    }

    binding.switchLayout.setOnClickListener {
        val toggled = !toggledIds.contains(permission.id)

        if (toggled) {
            permission.id?.let { it1 -> toggledIds.add(it1) }
        } else {
            toggledIds.remove(permission.id)
        }

        toggleSwitch(toggled, binding.switchLayout, binding.toggleCircle)
        permission.id?.let { it1 -> onToggle(it1, toggled) }
    }
}

    }
}