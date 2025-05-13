package ae.oleapp.employee.ui.employeeprofile.viewDocument

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ae.oleapp.databinding.FragmentChooseDocumentTypeBinding
import ae.oleapp.employee.utils.viewBinding
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChooseDocumentTypeFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding { FragmentChooseDocumentTypeBinding.inflate(it) }

    private val args : ChooseDocumentTypeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClicks()
    }

    private fun setupClicks() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnImage.setOnClickListener {
            val action = ChooseDocumentTypeFragmentDirections
                .actionChooseDocumentTypeFragmentToViewDocumentFragment(args.employeeDocumentUpdateModel?.copy(isPdfCLick = false))
            findNavController().navigate(action)
        }
        binding.btnPdf.setOnClickListener {
            val action = ChooseDocumentTypeFragmentDirections
                .actionChooseDocumentTypeFragmentToViewDocumentFragment(args.employeeDocumentUpdateModel?.copy(isPdfCLick = true))
            findNavController().navigate(action)

        }
    }

}