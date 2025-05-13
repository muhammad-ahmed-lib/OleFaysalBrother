package ae.oleapp.booking;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityCompleteBookingBinding;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteBookingActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCompleteBookingBinding binding;
    private final String extraTime = "";
    private String price = "";
    private String currency = "";
    private String duration = "";
    private String filePath = "";
    private int bookingId;

    private EasyImage easyImage;
    private File file = new File("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompleteBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            price = String.valueOf(bundle.getInt("price"));
            currency = bundle.getString("currency", "");
            duration = bundle.getString("duration", "");
            bookingId = bundle.getInt("id");
        }

        switch (duration.toLowerCase()) {
            case "60 min":
                binding.tvDuration.setText("1 Hour");
                break;
            case "90 min":
                binding.tvDuration.setText("1.5 Hour");
                break;
            default:
                binding.tvDuration.setText("2 Hour");
                break;
        }


        binding.tvPrice.setText(price);
        binding.tvPrice2.setText(price);
        binding.tvCurrency.setText(currency);
        binding.tvCurrency2.setText(currency);


        binding.btnComplete.setOnClickListener(this);
        binding.imgVuReceipt.setOnClickListener(this);
        binding.etDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                discountTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack) {
            finish();
        }
        else if (v == binding.btnComplete) {
            completeClicked();
        }
        else if (v == binding.imgVuReceipt) {
            receiptClicked();
        }
    }

    private void discountTextChanged() {
        if (binding.etDiscount.getText().toString().isEmpty()) {
            binding.tvPrice.setText(price);
            binding.tvCurrency.setText(currency);
            binding.tvPrice2.setText(price);
            binding.tvCurrency2.setText(currency);
        }
        else {
            double val = Double.parseDouble(binding.etDiscount.getText().toString());
            double total = Double.parseDouble(price) - val;
            if (val > Double.parseDouble(price)) {
                binding.etDiscount.setText(price);
                binding.tvPrice.setText(String.format("%s %s", "0", currency));
                binding.tvPrice.setText(0);
                binding.tvCurrency.setText(currency);
                binding.tvPrice2.setText(0);
                binding.tvCurrency2.setText(currency);
            }
            else {
                binding.tvPrice.setText(String.format("%s %s", total, currency));
                binding.tvPrice.setText(String.valueOf(total));
                binding.tvCurrency.setText(currency);
                binding.tvPrice2.setText(String.valueOf(total));
                binding.tvCurrency2.setText(currency);
            }
        }
    }

    private void receiptClicked() {
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        }else {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        }
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                easyImage = new EasyImage.Builder(getContext())
                        .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                        .setCopyImagesToPublicGalleryFolder(false)
                        .allowMultiple(false).build();
                easyImage.openChooser(getContext());
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                filePath = resultUri.getPath();
                file = new File(filePath);
                Glide.with(getApplicationContext()).load(file).into(binding.imgVuReceipt);
                //updatePhotoAPI(true, file);
                addCompleteBooking(true, String.valueOf(bookingId), filePath);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else {
            if (easyImage != null){
                easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                    @Override
                    public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                        if (mediaFiles.length > 0) {
//                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
//                                .setAspectRatio(1, 1)
//                                .start(getContext());

                            CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                                    .setFixAspectRatio(false)
                                    .setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                    .start(getContext());
                        }
                    }

                    @Override
                    public void onImagePickerError(Throwable error, MediaSource source) {
                        Functions.showToast(getContext(), error.getLocalizedMessage(), FancyToast.ERROR);
                    }

                    @Override
                    public void onCanceled(@NonNull MediaSource mediaSource) {
                        //canceled
                    }
                });
            }
        }
    }


    private void completeClicked() {
        Intent intent = new Intent();
        intent.putExtra("note", binding.etNote.getText().toString());
        intent.putExtra("discount", binding.etDiscount.getText().toString());
        intent.putExtra("balance", binding.etBalance.getText().toString());
        intent.putExtra("extraTime", extraTime);
        intent.putExtra("price", binding.etPrice.getText().toString());
        intent.putExtra("posPayment", binding.etPosPayment.getText().toString());
//        intent.putExtra("filePath", filePath);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void addCompleteBooking(boolean isLoader, String id, String filePath) { //if invoice uploaded, pos cannot be empty, if pos is added incvoice cannnot e empty
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        MultipartBody.Part filePart = null;
        if (filePath != null) {
            File file = new File(filePath);
            String mimeTypeCover = getMimeType(file);
            RequestBody requestBody = RequestBody.create(file, MediaType.parse(mimeTypeCover));
            filePart = MultipartBody.Part.createFormData("receipt", file.getName(), requestBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.addCompleteBooking(filePart,
                RequestBody.create(id, MediaType.parse("multipart/form-data")));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);

                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }



}