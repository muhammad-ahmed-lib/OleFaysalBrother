package ae.oleapp.employee.ui.employeeprofile.viewDocument

import ae.oleapp.R
import ae.oleapp.databinding.FragmentViewDocumentBinding
import ae.oleapp.employee.utils.CustomToast
import ae.oleapp.employee.utils.DateTimeHelper.DATE_FORMAT_YYYY_MM_DD
import ae.oleapp.employee.utils.DateTimeHelper.formatMillisecondsToDateString
import ae.oleapp.employee.utils.shareContent
import ae.oleapp.employee.utils.viewBinding
import android.app.Activity
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ViewDocumentFragment : Fragment() {

    private val binding by viewBinding{ FragmentViewDocumentBinding.inflate(it) }
    private val viewModel : DocumentVM by viewModel<DocumentVM>()

    private val args : ViewDocumentFragmentArgs by navArgs()

    private var isFront = true

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    val fileUri = data?.data
                    if (isFront) {
                        viewModel.setFrontImageUri(fileUri ?: Uri.EMPTY)
                    } else {
                        viewModel.setBackImageUri(fileUri ?: Uri.EMPTY)
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    CustomToast.makeToast(ImagePicker.getError(data))
                }
                else -> {
                    CustomToast.makeToast("Task Cancelled")
                }
            }
        }

    private val startForPdfResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                viewModel.setPdfImageUri(uri)
            } else {
                CustomToast.makeToast("No PDF Selected")
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        init()
        setupClicks()
        setupObserver()
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel._isDocumentDelete.collect {
                        if (it) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun init(){
        args?.let {
            viewModel.employeeId = (it.employeeDocumentUpdateModel?.employeeId ?: 0).toString()
            viewModel.documentId = (it.employeeDocumentUpdateModel?.documenId ?: 0)
            viewModel.documentName = it.employeeDocumentUpdateModel?.DocumentType?.typeName ?: ""
            viewModel.documentType = it.employeeDocumentUpdateModel?.DocumentType?.name ?: ""
            viewModel.setIsPdf(it.employeeDocumentUpdateModel?.isPdfCLick ?: false)
            viewModel.setIsEditMode(it.employeeDocumentUpdateModel?.isEditMode ?: false)
            viewModel.setFrontImageUrl(it.employeeDocumentUpdateModel?.front ?: "")
            viewModel.setBackImageUrl(it.employeeDocumentUpdateModel?.back ?: "")
            it.employeeDocumentUpdateModel?.document?.let {uri -> viewModel.setPdfImageUri(Uri.parse(uri)) }
            viewModel.setPdfUrl(it.employeeDocumentUpdateModel?.document ?: "")
            viewModel.setExpiryDate(it.employeeDocumentUpdateModel?.expiry ?: "")
        }
    }

    private fun setupClicks() {
        binding.frontCV.setOnClickListener {
            isFront = true
            launchImagePicker()
        }

        binding.backCV.setOnClickListener {
            isFront = false
            launchImagePicker()
        }
        binding.btnExpiryDate.setOnClickListener { showDatePicker() }
        binding.pdfCV.setOnClickListener {    startForPdfResult.launch("application/pdf") }
//        binding.btnDownload.setOnClickListener {  }
        binding.btnEdit.setOnClickListener { viewModel.validateDocuments() }
        binding.btnShare.setOnClickListener { shareContent(requireContext() , viewModel.employeeFrontImageUri.value ?: Uri.EMPTY , viewModel.frontImageUrl.value?.toString() , "text/plain" )  }
        binding.btnDelete.setOnClickListener { viewModel.deleteDocuments() }
        binding.btnClose.setOnClickListener { findNavController().popBackStack() }

    }

    private fun launchImagePicker() {
        ImagePicker.with(this)
            .crop()
            .galleryOnly()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    private fun showDatePicker() {
        val now = Calendar.getInstance()
        val pickerDialog = DatePickerDialog(
            requireContext(), R.style.datepicker,
            { view, year, month, dayOfMonth ->
                now.set(year, month, dayOfMonth)
                val formattedDate = formatMillisecondsToDateString(now.timeInMillis , DATE_FORMAT_YYYY_MM_DD)
                viewModel.setExpiryDate(formattedDate)
            },
            now[Calendar.YEAR],
            now[Calendar.MONTH],
            now[Calendar.DAY_OF_MONTH]
        )
        pickerDialog.show()
    }

}