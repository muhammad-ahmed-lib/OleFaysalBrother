package ae.oleapp.employee.ui.changeClub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.R
import ae.oleapp.databinding.FragmentChangeClubBinding
import ae.oleapp.employee.data.model.response.GetClub.Companion.getList
import ae.oleapp.employee.ui.employeedetail.EmployeeDetailVM
import ae.oleapp.employee.ui.employeenote.AddNoteFragment
import ae.oleapp.employee.utils.viewBinding
import android.app.Dialog
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangeClubFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding { FragmentChangeClubBinding.inflate(it) }

    private val viewModel : EmployeeDetailVM by viewModel<EmployeeDetailVM>()
    var clubId = 0
    var employeeId = ""


    companion object {
        fun newInstance(clubId :Int , employeeId :String,  fragmentManager: FragmentManager) {
            val args = Bundle().apply {
                putInt("clubId", clubId)
                putString("employeeId", employeeId)
            }
            ChangeClubFragment().apply {
                arguments = args
            }.show(fragmentManager, "ChangeClubFragment")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClicks()
        setupRV()
        setupObservers()
    }

    private fun setupObservers() {

    }

    private fun setupRV() {
        clubId = arguments?.getInt("clubId") ?: 0
        employeeId = arguments?.getString("employeeId") ?: ""
        val filterList = getList().filterNot { it.id?.toInt() == clubId }


        ChangeClubAdapter(onClubClick = {clubId1 ->
            clubId = clubId1.id?.toInt() ?: 0
        }).apply {
            binding.clubRV.adapter = this
            this.submitList( filterList ?: emptyList())
        }
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener { dismiss() }
        binding.btnChange.setOnClickListener { viewModel.changeEmployeeClub( employeeId ?: "",  clubId) }
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
                dialog.setCancelable(false)
            }
        }
    }

}