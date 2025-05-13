package ae.oleapp.employee.ui.employeenote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.R
import ae.oleapp.databinding.FragmentAddNoteBinding
import ae.oleapp.employee.ui.employeedetail.EmployeeDetailVM
import ae.oleapp.employee.utils.viewBinding
import android.app.Dialog
import android.view.WindowManager
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddNoteFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding { FragmentAddNoteBinding.inflate(it) }

//    private val viewModel : EmployeeDetailVM by viewModel<EmployeeDetailVM>()
    private val viewModel : EmployeeDetailVM by viewModel<EmployeeDetailVM>(
        ownerProducer = {requireParentFragment().requireParentFragment()}
    )

    companion object {
        fun newInstance(fragmentManager: FragmentManager) {
            AddNoteFragment().show(fragmentManager, "AddNoteFragment")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener { dialog ->
                val bottomSheet = (dialog as BottomSheetDialog)
                    .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

                bottomSheet?.let {
                    val behavior = BottomSheetBehavior.from(it)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    it.setBackgroundResource(R.drawable.rounded_top_sheet)
                    it.requestLayout()
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setCancelable(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        setupClicks()
    }

    fun init(){
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
//        arguments?.let {
//            viewModel.employeeId = it.getInt("employeeId")
//            viewModel.employeeClubId = it.getInt("clubId")
//        }
    }

    private fun setupClicks() {
        binding.btnAddNote.setOnClickListener {
            viewModel.updateNote()
        }
        binding.btnBack.setOnClickListener {
            dismiss()
        }
    }
}