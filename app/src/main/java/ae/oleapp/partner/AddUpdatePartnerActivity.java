package ae.oleapp.partner;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddUpdatePartnerBinding;
import ae.oleapp.models.CountryPhoneList;
import ae.oleapp.models.Partner;
import ae.oleapp.models.PartnerData;
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

public class AddUpdatePartnerActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddUpdatePartnerBinding binding;
    private String clubId = "", photoFilePath = "";
    private EasyImage easyImage;
    private boolean update = false;
    private File file = new File("");
    private String countryId = "";
    private List<CountryPhoneList> countryList = new ArrayList<>();
    private Partner partner;
    private int partnerId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUpdatePartnerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            update =  bundle.getBoolean("is_update");
            clubId = bundle.getString("club_id", "");
            partnerId = bundle.getInt("partner_id");
            partner = new Gson().fromJson(bundle.getString("partner"), Partner.class);
            if (update){
                populateData(update);
            }
        }

        countryList = AppManager.getInstance().countryPhoneLists;

        CCPCountry.setDialogTitle(getString(R.string.select_country_region));
        CCPCountry.setSearchHintMessage(getString(R.string.search_hint));

        countryId =  FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());

        binding.ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryId =  FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());
            }
        });

        binding.btnAdd.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.imgVu.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
           if (v == binding.btnBack) {
               finish();
           } else if (v == binding.btnAdd) {
               if (
                            binding.etName.getText().toString().isEmpty() ||
                            binding.etEmail.getText().toString().isEmpty() ||
                            binding.etPhone.getText().toString().isEmpty() ||
                            binding.etShareValue.getText().toString().isEmpty()){
                Functions.showToast(getContext(), getString(R.string.fill_form_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                return;
               }

            if (update){
                updateNewPartner(true,
                        binding.etName.getText().toString(),
                        binding.etEmail.getText().toString(),
                        countryId,
                        binding.etPhone.getText().toString(),
                        binding.etShareValue.getText().toString(), file);
            }else{
                addNewPartner(true,
                        binding.etName.getText().toString(),
                        binding.etEmail.getText().toString(),
                        countryId,
                        binding.etPhone.getText().toString(),
                        binding.etShareValue.getText().toString(), file);
            }
        }
        else if (v == binding.imgVu) {
            uploadImage();
        }
    }

    private void addNewPartner(boolean isLoader, String name, String email, String countryId, String phone, String shareValue, File file) {
            KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
            MultipartBody.Part part = null;
            if (photoFilePath != null) {
                file = new File(photoFilePath);
                String mimeTypeCover = getMimeType(file);
                RequestBody requestBody = RequestBody.create(file, MediaType.parse(mimeTypeCover));
                part = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
            }
            Call<ResponseBody> call = AppManager.getInstance().apiInterfacePartner.addNewPartner(part,
                    RequestBody.create(MediaType.parse("multipart/form-data"), clubId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), name),
                    RequestBody.create(MediaType.parse("multipart/form-data"), email),
                    RequestBody.create(MediaType.parse("multipart/form-data"), countryId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), phone),
                    RequestBody.create(MediaType.parse("multipart/form-data"), shareValue));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                Functions.showToast(getContext(), getString(R.string.partner_added), FancyToast.SUCCESS);
                                finish();
                            } else {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Functions.hideLoader(hud);
                    if (t instanceof UnknownHostException) {
                        Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                    } else {
                        Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
            });
    }

    private void updateNewPartner(boolean isLoader, String name, String email, String countryId, String phone, String shareValue, File file) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        MultipartBody.Part part = null;
        if (photoFilePath != null && !photoFilePath.isEmpty()) {
            file = new File(photoFilePath);
            if (file.exists()) {
                String mimeTypeCover = getMimeType(file);
                RequestBody requestBody = RequestBody.create(file, MediaType.parse(mimeTypeCover));
                part = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
            }
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterfacePartner.updateNewPartner(part,
                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(partnerId)),
                RequestBody.create(MediaType.parse("multipart/form-data"), clubId),
                RequestBody.create(MediaType.parse("multipart/form-data"), name),
                RequestBody.create(MediaType.parse("multipart/form-data"), email),
                RequestBody.create(MediaType.parse("multipart/form-data"), countryId),
                RequestBody.create(MediaType.parse("multipart/form-data"), phone),
                RequestBody.create(MediaType.parse("multipart/form-data"), shareValue));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), getString(R.string.partner_updated), FancyToast.SUCCESS);
                            finish();
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void uploadImage() {
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        } else {
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
                //file = new File(resultUri.getPath());
                photoFilePath = resultUri.getPath();
                file = new File(photoFilePath);
                Glide.with(getContext()).load(file).apply(RequestOptions.circleCropTransform()).into(binding.imgVu);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else {
            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                .setAspectRatio(1, 1)
                                .start(getContext());
                    }
                }

                @Override
                public void onImagePickerError(Throwable error, MediaSource source) {
                    Functions.showToast(getContext(), error.getLocalizedMessage(), FancyToast.ERROR);
                }

                @Override
                public void onCanceled(@NonNull MediaSource mediaSource) {

                }
            });
        }
    }

    private void populateData(Boolean isUpdate){
        if (isUpdate){
            binding.title.setText(R.string.update);
            binding.tvBtn.setText(R.string.update);
            binding.etName.setText(partner.getName());
            binding.etEmail.setText(partner.getEmail());
            binding.etPhone.setText(partner.getPhoneNumber());
            binding.etShareValue.setText(String.valueOf(partner.getShares()));

            if (!partner.getProfilePhoto().isEmpty()){
                Glide.with(getApplicationContext()).load(partner.getProfilePhoto()).apply(RequestOptions.circleCropTransform()).into(binding.imgVu);
            }
        }
        else{
            binding.title.setText(getString(R.string.add_new_partner));
            binding.tvBtn.setText(R.string.add);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}