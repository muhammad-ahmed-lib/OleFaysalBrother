//package ae.oleapp.employee.ui.employeeprofile.viewDocument;
//
//import android.Manifest;
//import android.app.DatePickerDialog;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.DatePicker;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.documentfile.provider.DocumentFile;
//import com.bumptech.glide.Glide;
//import com.google.gson.Gson;
//import com.kaopiz.kprogresshud.KProgressHUD;
//import com.nabinbhandari.android.permissions.PermissionHandler;
//import com.nabinbhandari.android.permissions.Permissions;
//import com.shashank.sony.fancytoastlib.FancyToast;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;
//import org.json.JSONObject;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.UnknownHostException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Locale;
//import ae.oleapp.R;
//import ae.oleapp.base.BaseActivity;
//import ae.oleapp.databinding.ActivityAddUpdateDocumentBinding;
//import ae.oleapp.models.Document;
//import ae.oleapp.util.AppManager;
//import ae.oleapp.util.Constants;
//import ae.oleapp.util.Functions;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import okhttp3.ResponseBody;
//import pl.aprilapps.easyphotopicker.ChooserType;
//import pl.aprilapps.easyphotopicker.EasyImage;
//import pl.aprilapps.easyphotopicker.MediaFile;
//import pl.aprilapps.easyphotopicker.MediaSource;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class AddUpdateDocumentActivity extends BaseActivity implements View.OnClickListener {
//
//    private ActivityAddUpdateDocumentBinding binding;
//    private int partnerId = 0;
//    private String docType = "", date = "";
//    private boolean isImage = false;
//    private boolean isUpdate = false;
//    private final boolean pdf = false;
//    private boolean isFrontImage = true;
//    private Document document;
//
//    private EasyImage easyImage;
//    private File fileFront, fileBack, fileDoc  = new File("");
//    private static final int REQUEST_PICK_PDF = 3;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityAddUpdateDocumentBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        makeStatusbarTransperant();
//
//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null){
//            docType = bundle.getString("doc_type");
//            partnerId = bundle.getInt("partner_id");
//            isImage = bundle.getBoolean("is_image");
//            isUpdate = bundle.getBoolean("is_update");
//            document = new Gson().fromJson(bundle.getString("document"), Document.class);
//        }
//
//        populateData();
//
//        binding.btnClose.setOnClickListener(this);
//        binding.etDate.setOnClickListener(this);
//        binding.btnFront.setOnClickListener(this);
//        binding.btnBack.setOnClickListener(this);
//        binding.docImgVu.setOnClickListener(this);
//        binding.btnAdd.setOnClickListener(this);
//
//    }
//
//    private void populateData() {
//        if (isUpdate){
//            binding.title.setText("Update Document");
//            binding.tvBtn.setText("Update");
//            binding.etName.setText(document.getName());
//            binding.etDate.setText(document.getExpiresAt());
//            if (!isImage){
//                Glide.with(getApplicationContext()).load(R.drawable.pdf_icon).into(binding.docImgVu);
//            }
//            else{
//                Glide.with(getApplicationContext()).load(document.getFront()).into(binding.frontImgVu);
//                Glide.with(getApplicationContext()).load(document.getBack()).into(binding.backImgVu);
//            }
//
//        }
//        if (!isImage){
//            binding.relDoc.setVisibility(View.VISIBLE);
//            binding.relImg.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (v == binding.btnClose){
//            finish();
//        }
//        else if (v == binding.etDate) {
//            dateClicked();
//        }
//        else if (v == binding.btnFront) {
//                isFrontImage = true;
//                uploadImage();
//        }
//        else if (v == binding.btnBack) {
//                isFrontImage = false;
//                uploadImage();
//        }
//        else if (v == binding.docImgVu) {
//            choosePdf();
//        }
//        else if (v == binding.btnAdd) {
//            if (isUpdate){
//                updateDocument(true, String.valueOf(document.getId()), String.valueOf(partnerId), binding.etName.getText().toString(), docType, date);
//            }
//            else{
//                uploadDocument(true, String.valueOf(partnerId), binding.etName.getText().toString(), docType, date);
//            }
//        }
//    }
//
//    private void dateClicked() {
//        Calendar now = Calendar.getInstance();
//        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Calendar calendar = Calendar.getInstance();
//                calendar.set(year, month, dayOfMonth);
//                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//                date = formatter.format(calendar.getTime());
//                formatter.applyPattern("dd/MM/yyyy");
//                binding.etDate.setText(formatter.format(calendar.getTime()));
//            }
//        },
//                now.get(Calendar.YEAR),
//                now.get(Calendar.MONTH),
//                now.get(Calendar.DAY_OF_MONTH));
//        pickerDialog.show();
//    }
//
//    private void uploadDocument(boolean isLoader, String partnerId, String name, String type, String expiryDate) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Uploading...") : null;
//        MultipartBody.Part front = null, back = null, document = null;
//        // Case 1: Sending Front & Back Images Together
//        if (fileFront != null && fileBack != null) {
//            if (fileFront.exists()) {
//                String mimeType = getMimeType(fileFront);
//                RequestBody requestBody = RequestBody.create(fileFront, MediaType.parse(mimeType));
//                front = MultipartBody.Part.createFormData("front", fileFront.getName(), requestBody);
//            }
//            if (fileBack.exists()) {
//                String mimeType = getMimeType(fileBack);
//                RequestBody requestBody = RequestBody.create(fileBack, MediaType.parse(mimeType));
//                back = MultipartBody.Part.createFormData("back", fileBack.getName(), requestBody);
//            }
//        }
//        // Case 2: Sending PDF Only
//        else if (fileDoc != null && fileDoc.exists()) {
//            RequestBody fileReqBody = RequestBody.create(MediaType.parse("application/pdf"), fileDoc);
//            document = MultipartBody.Part.createFormData("document", fileDoc.getName(), fileReqBody);
//        }
//
//        // Ensure valid request: Either (Front & Back) or (PDF) must be selected
//        if ((front == null && back == null) && document == null) {
//            Functions.showToast(getContext(), "Please select either front & back images OR a PDF", FancyToast.ERROR);
//            return;
//        }
//
//        // API Call
//        Call<ResponseBody> call = AppManager.getInstance().apiInterfacePartner.uploadDocument(
//                front, back, document,
//                RequestBody.create(partnerId, MediaType.parse("multipart/form-data")),
//                RequestBody.create(name, MediaType.parse("multipart/form-data")),
//                RequestBody.create(type, MediaType.parse("multipart/form-data")),
//                RequestBody.create(expiryDate, MediaType.parse("multipart/form-data"))
//        );
//
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
//                        } else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                } else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
//    }
//    private void updateDocument(boolean isLoader, String documentId,String partnerId, String name, String type, String expiryDate) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Uploading...") : null;
//        MultipartBody.Part front = null, back = null, document = null;
//        if (fileFront != null && fileBack != null) {
//            if (fileFront.exists()) {
//                String mimeType = getMimeType(fileFront);
//                RequestBody requestBody = RequestBody.create(fileFront, MediaType.parse(mimeType));
//                front = MultipartBody.Part.createFormData("front", fileFront.getName(), requestBody);
//            }
//            if (fileBack.exists()) {
//                String mimeType = getMimeType(fileBack);
//                RequestBody requestBody = RequestBody.create(fileBack, MediaType.parse(mimeType));
//                back = MultipartBody.Part.createFormData("back", fileBack.getName(), requestBody);
//            }
//        }
//        else if (fileDoc != null && fileDoc.exists()) {
//            RequestBody fileReqBody = RequestBody.create(MediaType.parse("application/pdf"), fileDoc);
//            document = MultipartBody.Part.createFormData("document", fileDoc.getName(), fileReqBody);
//        }
//        Call<ResponseBody> call = AppManager.getInstance().apiInterfacePartner.updateDocument(
//                front, back, document,
//                RequestBody.create(documentId, MediaType.parse("multipart/form-data")),
//                RequestBody.create(partnerId, MediaType.parse("multipart/form-data")),
//                RequestBody.create(name, MediaType.parse("multipart/form-data")),
//                RequestBody.create(type, MediaType.parse("multipart/form-data")),
//                RequestBody.create(expiryDate, MediaType.parse("multipart/form-data"))
//        );
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
//                        } else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                } else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
//    }
//
//    private void choosePdf() {
//        String[] permissions = new String[0];
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
//        }else {
//            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//
//        }
//        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
//            @Override
//            public void onGranted() {
//                openFileChooser();
//            }
//        });
//    }
//
//    private void uploadImage() {
//        String[] permissions = new String[0];
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
//        }else {
//            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//
//        }
//        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
//            @Override
//            public void onGranted() {
//                // do your task.
//                easyImage = new EasyImage.Builder(getContext())
//                        .setChooserType(ChooserType.CAMERA_AND_GALLERY)
//                        .setCopyImagesToPublicGalleryFolder(false)
//                        .allowMultiple(false).build();
//                easyImage.openChooser(getContext());
//            }
//        });
//    }
//
//    private void openFileChooser() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("application/pdf");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_pdf)), REQUEST_PICK_PDF);
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode != RESULT_OK || data == null) return;
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (result != null) {
//                Uri resultUri = result.getUri();
//                if (isFrontImage) {
//                    String photoFilePath = resultUri.getPath();
//                    fileFront = new File(photoFilePath);
//                    Glide.with(getApplicationContext()).load(fileFront).into(binding.frontImgVu);
//                } else {
//                    String photoFilePath = resultUri.getPath();
//                    fileBack = new File(photoFilePath);
//                    Glide.with(getApplicationContext()).load(fileBack).into(binding.backImgVu);
//                }
//            } else {
//                Exception error = result.getError();
//                Functions.showToast(getContext(), error.getLocalizedMessage(), FancyToast.ERROR);
//            }
//        }
//        else if (requestCode == REQUEST_PICK_PDF) {
//            Uri fileUri = data.getData();
//            String filePath = getFilePathFromUri(fileUri);
//            fileDoc = new File(filePath);
//
//            if (filePath != null && !filePath.isEmpty()) {
//                Glide.with(getApplicationContext()).load(R.drawable.pdf_icon).into(binding.docImgVu);
//            }
//        }
//        else {
//            // Handle camera/gallery image selection
//            if (!pdf) {
//                easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
//                    @Override
//                    public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
//                        if (mediaFiles.length > 0) {
//                            Uri fileUri = Uri.fromFile(mediaFiles[0].getFile());
//
//                            CropImage.activity(fileUri)
//                                    .setGuidelines(CropImageView.Guidelines.ON)
//                                    .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                    .setScaleType(CropImageView.ScaleType.CENTER_CROP)
//                                    .start(getContext());
//                        }
//                    }
//
//                    @Override
//                    public void onImagePickerError(Throwable error, MediaSource source) {
//                        Functions.showToast(getContext(), error.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//
//                    @Override
//                    public void onCanceled(@NonNull MediaSource mediaSource) {
//                        // Handle cancellation
//                    }
//                });
//            }
//        }
//    }
//
//
//    private String getFilePathFromUri(Uri uri) {
//        String filePath = null;
//        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
//
//        if (documentFile != null && documentFile.exists()) {
//            try {
//                InputStream inputStream = getContentResolver().openInputStream(uri);
//                File tempFile = createTempFile(".pdf"); // Specify the file extension as ".pdf"
//                if (tempFile != null) {
//                    OutputStream outputStream = new FileOutputStream(tempFile);
//                    byte[] buffer = new byte[4 * 1024];
//                    int read;
//                    while ((read = inputStream.read(buffer)) != -1) {
//                        outputStream.write(buffer, 0, read);
//                    }
//                    filePath = tempFile.getAbsolutePath();
//                    outputStream.close();
//                    inputStream.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return filePath;
//    }
//
//    private File createTempFile(String fileExtension) {
//        try {
//            File tempDir = getCacheDir();
//            String tempFileName = "temp_file_" + System.currentTimeMillis();
//            String tempFilePath = tempDir.getAbsolutePath() + "/" + tempFileName + fileExtension;
//            return new File(tempFilePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        binding = null;
//    }
//
//}