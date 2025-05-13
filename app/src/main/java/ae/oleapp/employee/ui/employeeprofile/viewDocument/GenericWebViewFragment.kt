package ae.oleapp.employee.ui.employeeprofile.viewDocument

import ae.oleapp.R
import ae.oleapp.databinding.FragmentGenericWebviewBinding
import ae.oleapp.employee.ui.employeedetail.EmployeeDetailVM
import ae.oleapp.employee.utils.downloadMedia
import ae.oleapp.employee.utils.viewBinding
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.koin.androidx.viewmodel.ext.android.viewModel

class GenericWebViewFragment : Fragment() {

    private val binding by viewBinding { FragmentGenericWebviewBinding.inflate(it) }

    private val viewModel : DocumentVM by viewModel<DocumentVM>(
        ownerProducer = {requireParentFragment()}
    )

    private val args: GenericWebViewFragmentArgs by navArgs()

//    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        val encodedUrl = Uri.encode(uri.toString())
//
//        val googleDocsUrl = "https://docs.google.com/gview?embedded=true&url=$encodedUrl"
//        initWebClient(googleDocsUrl)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        initWebClient()

        init()
        setupClicks()
    }

    private fun setupClicks() {

//        binding.btnDelete.setOnClickListener { viewModel.deleteDocuments() }

        binding.btnDownload.setOnClickListener {
            requireContext().downloadMedia(args.employeeDocumentUpdateModel?.document?:"")
        }
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnEdit.setOnClickListener {
            val action = GenericWebViewFragmentDirections
                .actionGenericWebViewFragmentToViewDocumentFragment(args.employeeDocumentUpdateModel)
            findNavController().navigate(action)
        }
    }

    private fun init() {
        args?.let {
            viewModel.employeeId = (it.employeeDocumentUpdateModel?.employeeId ?: 0).toString()
            viewModel.documentId = (it.employeeDocumentUpdateModel?.documenId ?: 0)
            viewModel.documentName = it.employeeDocumentUpdateModel?.DocumentType?.typeName ?: ""
            viewModel.documentType = it.employeeDocumentUpdateModel?.DocumentType?.name ?: ""
            viewModel.setIsPdf(it.employeeDocumentUpdateModel?.isPdfCLick ?: false)
            viewModel.setIsEditMode(it.employeeDocumentUpdateModel?.isEditMode ?: false)
            viewModel.setPdfUrl(it.employeeDocumentUpdateModel?.document ?: "")

            // load the url
            val pdfUrl = it.employeeDocumentUpdateModel?.document?:""
            val url = "https://docs.google.com/gview?embedded=true&url=$pdfUrl"
            initWebClient(url ?: "")
        }
    }


    /**--------------------------------Init WebView--------------------------------*/
    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebClient(url: String) {

        binding.webView.invalidate()
        binding.webView.setLayerType(
            View.LAYER_TYPE_NONE, null
        )
        binding.webView.settings.supportZoom()
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.allowContentAccess = true
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.displayZoomControls = true
        binding.webView.settings.loadWithOverviewMode = true
        // Disable zoom controls
        binding.webView.settings.setSupportZoom(false)
        binding.webView.settings.builtInZoomControls = false
        binding.webView.settings.displayZoomControls = false

        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true

        binding.webView.loadUrl(url)

        binding.webView.webViewClient = object : WebViewClient() {

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)


            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)

            }


            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
    }

}