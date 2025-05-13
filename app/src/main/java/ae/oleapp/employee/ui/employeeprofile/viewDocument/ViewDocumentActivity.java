//package ae.oleapp.employee.ui.employeeprofile.viewDocument;
//
//import android.app.DownloadManager;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Looper;
//import android.text.Html;
//import android.text.SpannableString;
//import android.text.style.ForegroundColorSpan;
//import android.view.View;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.core.text.HtmlCompat;
//
//import com.bumptech.glide.Glide;
//import com.github.barteksc.pdfviewer.PDFView;
//import com.google.gson.Gson;
//import com.kaopiz.kprogresshud.KProgressHUD;
//import com.shashank.sony.fancytoastlib.FancyToast;
//
//import org.json.JSONObject;
//
//import java.io.BufferedInputStream;
//import java.io.InputStream;
//import java.net.URL;
//import java.net.UnknownHostException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import javax.net.ssl.HttpsURLConnection;
//
//import ae.oleapp.R;
//import ae.oleapp.base.BaseActivity;
//import ae.oleapp.databinding.ActivityViewDocumentBinding;
//import ae.oleapp.models.Document;
//import ae.oleapp.util.AppManager;
//import ae.oleapp.util.Constants;
//import ae.oleapp.util.Functions;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class ViewDocumentActivity extends BaseActivity implements View.OnClickListener{
//    private ActivityViewDocumentBinding binding;
//    private Document document;
//    private boolean isImage = false;
//    private final boolean pdf = false;
//    private final boolean isFrontImage = true;
//    private String pdfUrl = "";
//    private final String folderName= "";
//    private final String fileName= "";
//    private String docType = "";
//    private int partnerId = 0;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityViewDocumentBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null){
//            docType = bundle.getString("doc_type");
//            partnerId = bundle.getInt("partner_id");
//            isImage = bundle.getBoolean("is_image");
//            document = new Gson().fromJson(bundle.getString("document"), Document.class);
//        }
//
//        if (isImage){showImageDocData();}
//        else{showPdfDocData();}
//        binding.btnDownload.setOnClickListener(this);
//        binding.btnEdit.setOnClickListener(this);
//        binding.btnClose.setOnClickListener(this);
//        binding.btnDelete.setOnClickListener(this);
//        binding.btnShare.setOnClickListener(this);
//    }
//
//    private void showPdfDocData() {
//        if (document == null){
//            Functions.showToast(getContext(),"Document is empty", FancyToast.ERROR);
//            return;
//        }
//
//        binding.relImg.setVisibility(View.GONE);
//        binding.relPdf.setVisibility(View.VISIBLE);
//        pdfUrl = document.getDocument();
//
//        PDFLoader pdfLoader = new PDFLoader();
//        pdfLoader.loadPdfFromUrl(pdfUrl, binding.pdf, binding.progressBar);
//
//        binding.tvName.setText(document.getName());
//
//        binding.tvExpiry.setText(HtmlCompat.fromHtml(getString(R.string.document_expiry_message, document.getExpiresAt()), HtmlCompat.FROM_HTML_MODE_LEGACY));
//
//    }
//
//    private void showImageDocData() {
//        if (document == null){
//            Functions.showToast(getContext(),"Document is empty", FancyToast.ERROR);
//            return;
//        }
//
//        binding.relImg.setVisibility(View.VISIBLE);
//        binding.relPdf.setVisibility(View.GONE);
//
//        binding.tvName.setText(document.getName());
//        Glide.with(getApplicationContext()).load(document.getFront()).into(binding.frontImgVu);
//        Glide.with(getApplicationContext()).load(document.getBack()).into(binding.backImgVu);
//
//        binding.tvExpiry.setText(HtmlCompat.fromHtml(getString(R.string.document_expiry_message, document.getExpiresAt()), HtmlCompat.FROM_HTML_MODE_LEGACY));
//    }
//
//    private void downloadPdf(String fileUrl) {
//        String fileName = (document.getName() == null || document.getName().trim().isEmpty()) ? "oleDocument.pdf" : document.getName().trim() + ".pdf";
//
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//
//        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
//        if (downloadManager != null) {
//            downloadManager.enqueue(request);
//            Functions.showToast(getContext(), "File is Downloading...", FancyToast.SUCCESS);
//        }
//        else {
//            Functions.showToast(getContext(), "Download Manager not available", FancyToast.ERROR);
//        }
//
//        finish();
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v == binding.btnClose){
//            finish();
//        }
//        else if (v == binding.btnDelete){
//            deleteDocument(document.getId(), partnerId);
//        }
//        else if (v == binding.btnShare){
//
//        }
//        else if (v == binding.btnEdit){
//            Intent intent = new Intent(getContext(), AddUpdateDocumentActivity.class);
//            intent.putExtra("doc_type", docType);
//            intent.putExtra("partner_id", partnerId);
//            intent.putExtra("is_image", isImage);
//            intent.putExtra("is_update", true);
//            intent.putExtra("document", new Gson().toJson(document));
//            startActivity(intent);
//        }
//        else if (v == binding.btnDownload){
//            downloadPdf(pdfUrl);
//        }
//    }
//
//    public class PDFLoader {
//
//        private final ExecutorService executorService = Executors.newSingleThreadExecutor();
//        private final Handler mainHandler = new Handler(Looper.getMainLooper());
//
//        public void loadPdfFromUrl(String pdfUrl, PDFView pdfView, ProgressBar progressBar) {
//            // Show the loader before starting the download
//            mainHandler.post(() -> progressBar.setVisibility(View.VISIBLE));
//
//            executorService.execute(() -> {
//                InputStream inputStream = null;
//                try {
//                    URL url = new URL(pdfUrl);
//                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
//                    if (urlConnection.getResponseCode() == 200) {
//                        inputStream = new BufferedInputStream(urlConnection.getInputStream());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                InputStream finalInputStream = inputStream;
//                mainHandler.post(() -> {
//                    // Hide the loader when the file is ready or failed to load
//                    progressBar.setVisibility(View.GONE);
//
//                    if (finalInputStream != null) {
//                        pdfView.fromStream(finalInputStream).load();
//                    } else {
//                        Toast.makeText(pdfView.getContext(), "Failed to load PDF", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            });
//        }
//    }
//
//    private void deleteDocument(int docId, int partnerId) {
//        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
//        Call<ResponseBody> call = AppManager.getInstance().apiInterfacePartner.deleteDocument(docId, partnerId);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(getContext(), getString(R.string.success), FancyToast.SUCCESS);
//                            finish();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                }
//                else {
//                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
//                }
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                }
//                else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
//    }
//
//}