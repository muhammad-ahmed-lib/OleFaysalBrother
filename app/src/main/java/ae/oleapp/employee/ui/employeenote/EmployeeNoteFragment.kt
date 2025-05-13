package ae.oleapp.employee.ui.employeenote

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.R
import ae.oleapp.databinding.FragmentEmployeeNoteBinding
import ae.oleapp.employee.ui.employeedetail.EmployeeDetailVM
import ae.oleapp.employee.utils.CustomToast
import ae.oleapp.employee.utils.findNavController
import ae.oleapp.employee.utils.viewBinding
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class EmployeeNoteFragment : Fragment() {

    private val binding by viewBinding { FragmentEmployeeNoteBinding.inflate(it) }

    private val viewModel : EmployeeDetailVM by viewModel<EmployeeDetailVM>(
        ownerProducer = {requireParentFragment()}
    )
    private lateinit var adapter : EmployeeNotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        init()
        setupObserver()
        setupClicks()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setupClicks() {
        binding.btnAddNote.setOnClickListener {
            AddNoteFragment.newInstance(childFragmentManager)
        }
    }

    private fun init(){
        adapter = EmployeeNotesAdapter(onNoteClick = {
            viewModel.noteId = it.id ?: 0
            viewModel.setNote(it.description?:"")
            viewModel.isEmployeeNoteUpdate = true
//           navigateToAddNoteFragment()
            AddNoteFragment.newInstance(childFragmentManager )

        },
            onDeleteClick = {
                viewModel.noteId = it
                viewModel.deleteEmployeeNote()
            }).apply {
            binding.noteRV.adapter = this
        }
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.getEmployeeDetail.collectLatest { employeeDetail ->
                       adapter.submitList(employeeDetail?.tasks ?: emptyList())
                    }
                }


            }
        }

    }

//    private fun navigateToAddNoteFragment(){
//        val action = EmployeeNoteFragmentDirections.actionEmployeeNoteFragmentToAddEmployeeNoteFragment()
//        val navController = findNavController(R.id.nav_host_fragment)
//        navController.setGraph(R.navigation.app_navigation)
//        navController.navigate(action)
//    }


}