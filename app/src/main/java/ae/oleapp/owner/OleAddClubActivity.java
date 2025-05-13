package ae.oleapp.owner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.adapters.OleClubDayAdapter;
import ae.oleapp.adapters.OleClubFacilityListAdapter;
import ae.oleapp.adapters.v5DayAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityAddClubBinding;
import ae.oleapp.dialogs.OleCustomAlertDialog;
import ae.oleapp.dialogs.SelectionListDialog;
import ae.oleapp.models.Club;
import ae.oleapp.models.Country;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.Day;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.OleShiftTime;
import ae.oleapp.models.SelectionList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleKeyboardUtils;
import io.github.vejei.cupertinoswitch.CupertinoSwitch;
import mumayank.com.airlocationlibrary.AirLocation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;


import java.net.UnknownHostException;
import java.util.Set;

import ae.oleapp.util.Constants;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OleAddClubActivity extends BaseActivity implements View.OnClickListener { //, CompoundButton.OnCheckedChangeListener

    private OleactivityAddClubBinding binding;
    private EasyImage easyImage;
    private ImageType imageType;
    private String logoImage = "", coverImage = "";
    private  List<OleKeyValuePair> daysList = new ArrayList<>();
    private  String countryId = "";
    private  String cityId = "";
    private final String currentDayId = "";
    private final String slotOneHour = "1";
    private final String slotOneHalfHour = "1";
    private final String slotTwoHour = "1";
    private final String clubType = "";
    private v5DayAdapter dayAdapter;
    private  double latitude = 0;
    private  double longitude = 0;
    private OleClubFacilityListAdapter facilityAdapter;
    private boolean isForUpdate = false;
    private Club club;
    private final List<Country> countryList = new ArrayList<>();
    private String timingJson = "", facilityJson = "", finalSlotPattern = "";
    private File logoFile, coverFile = new File("");
    Set<String> slotPattern = new LinkedHashSet<>();
    private boolean clubAdded = false;


    public enum ImageType {
        logo,
        cover
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityAddClubBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isForUpdate = bundle.getBoolean("is_edit", false);
            Gson gson = new Gson();
            club = gson.fromJson(bundle.getString("club", ""), Club.class);
        }

        daysList = Functions.getDays(getContext());
        if (AppManager.getInstance().clubFacilities.size() == 0) {
            KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
            getFacilityAPI(new FacilityCallback() {
                @Override
                public void facilities(List<OleClubFacility> facilities) {
                    Functions.hideLoader(hud);
                    AppManager.getInstance().clubFacilities.clear();
                    AppManager.getInstance().clubFacilities.addAll(facilities);
                    facilityAdapter.notifyDataSetChanged();
                }
            });
        }

        LinearLayoutManager dayLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.daysRecyclerVu.setLayoutManager(dayLayoutManager);
        dayAdapter = new v5DayAdapter(getContext(), daysList);
        dayAdapter.setOnItemClickListener(dayClickListener);
        binding.daysRecyclerVu.setAdapter(dayAdapter);

        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.facRecyclerVu.setLayoutManager(facLayoutManager);
        facilityAdapter = new OleClubFacilityListAdapter(getContext(), AppManager.getInstance().clubFacilities);
        facilityAdapter.setOnItemClickListener(facClickListener);
        binding.facRecyclerVu.setAdapter(facilityAdapter);

        new AirLocation(getContext(), true, true, new AirLocation.Callbacks() {
                @Override
                public void onSuccess(@NotNull Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Functions.getAddressFromLocation(location, getContext(), new GeocoderHandler());
                    String url = "http://maps.google.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=15&size=300x300&sensor=false&key="+getString(R.string.maps_api_key);
                    Glide.with(getApplicationContext()).load(url).into(binding.mapImgVu);

                }

                @Override
                public void onFailed(@NotNull AirLocation.LocationFailedEnum locationFailedEnum) {

                }
            });
        checkKeyboardListener();
        binding.btnBack.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);
        binding.imgVuLogo.setOnClickListener(this);
        binding.imgVuBanner.setOnClickListener(this);
        binding.relMain.setOnClickListener(this);
        binding.etCity.setOnClickListener(this);
        binding.etCountry.setOnClickListener(this);
        binding.hourSwitch.setOnStateChangeListener(stateChangeListener);
        binding.btnAddField.setOnClickListener(this);
        binding.btnHome.setOnClickListener(this);
        binding.slot60Vu.setOnClickListener(this);
        binding.slot90Vu.setOnClickListener(this);
        binding.slot120Vu.setOnClickListener(this);

    }

    CupertinoSwitch.OnStateChangeListener stateChangeListener = new CupertinoSwitch.OnStateChangeListener() {
        @Override
        public void onChanged(CupertinoSwitch view, boolean checked) {

        }

        @Override
        public void onSwitchOn(CupertinoSwitch view) {
            binding.weekLay.setVisibility(View.GONE);
        }

        @Override
        public void onSwitchOff(CupertinoSwitch view) {
            binding.weekLay.setVisibility(View.VISIBLE);
        }
    };

    OleClubFacilityListAdapter.OnItemClickListener facClickListener = new OleClubFacilityListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            OleClubFacility facility = AppManager.getInstance().clubFacilities.get(pos);
            int index = facilityAdapter.isExistInSelected(facility.getId());
            if (index == -1) {
                facilityAdapter.setCurrentSelectedPosition(pos);
            }
            else {
                facilityAdapter.selectedFacility.remove(index);
                facilityAdapter.notifyDataSetChanged();
            }
        }
    };

    v5DayAdapter.OnItemClickListener dayClickListener = new v5DayAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {

        }


    };

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack || v == binding.btnHome) {
            finish();
        }
        else if (v == binding.btnContinue) {
            if (!clubAdded){
                btnContinueClicked();
            }else{
                finish();
            }
        }

        else if (v == binding.imgVuLogo) {
            logoClicked();
        }
        else if (v == binding.imgVuBanner) {
            bannerClicked();
        }
        else if (v == binding.etCity) {
            cityClicked();
        }
        else if (v == binding.etCountry) {
            countryClicked();
        }
        else if (v == binding.btnAddField) {
            addFieldClicked();
        }
        else if (v == binding.slot60Vu) {
            toggleSlot("1",  binding.slot60Vu, binding.clockImg60, binding.tvSlot60);
        }
        else if (v == binding.slot90Vu) {
            toggleSlot("1.5", binding.slot90Vu, binding.clockImg90, binding.tvSlot90);
        }
        else if (v == binding.slot120Vu) {
            toggleSlot("2", binding.slot120Vu, binding.clockImg120, binding.tvSlot120);
        }
       
//        else if (v == binding.monBtnAddMoreShift1) {
//            binding.monVuShift2.setVisibility(View.VISIBLE);
//        }
//        else if (v == binding.monBtnRemoveShift2) {
//            binding.tvMonOpenShift2.setText("");
//            binding.tvMonCloseShift2.setText("");
//            binding.monVuShift2.setVisibility(View.GONE);
//        }
//        else if (v == binding.tvMonOpenShift1) {
//            timeClicked(v);
//        }
//        else if (v == binding.tvMonCloseShift1) {
//            timeClicked(v);
//        }
//        else if (v == binding.tvMonOpenShift2) {
//            timeClicked(v);
//        }
//        else if (v == binding.tvMonCloseShift2) {
//            timeClicked(v);
//        }


        //        else if (v == binding.relOneHour) {
//            relOneHourClicked();
//        }
//        else if (v == binding.relOneHalfHour) {
//            relOneHalfHourClicked();
//        }
//        else if (v == binding.relTwoHour) {
//            relTwoClicked();
//        }
//        else if (v == binding.relApplyEveryday) {
//            appEverydayClicked();
//        }
//        else if (v == binding.btnAddMore) {
//            addMoreClicked();
//        }
//        else if (v == binding.btnRemove) {
//            removeClicked();
//        }
//        else if (v == binding.btnAdd) {
//            addClubClicked();
//        }
        else if (v == binding.relMain) {
            locationClicked();
        }
//        else if (v == binding.etOpenTime1 || v == binding.etOpenTime2 || v == binding.etCloseTime1 || v == binding.etCloseTime2) {
//            timeClicked(v);
//        }
//        else if (v == binding.basicVu) {
//            if (binding.timeDetailVu.getVisibility() == View.VISIBLE || binding.facRecyclerVu.getVisibility() == View.VISIBLE) {
//                basicVuClicked();
//            }
//        }
//        else if (v == binding.timeVu) {
//            if (binding.facRecyclerVu.getVisibility() == View.VISIBLE) {
//                timeVuClicked();
//            }
//        }
//        else if (v == binding.footballVu) {
//            footballClicked();
//        }
//        else if (v == binding.padelVu) {
//            padelClicked();
//        }
    }

    public void toggleSlot(String slotValue, View view, View img, View txt) {
        if (slotPattern.contains(slotValue)) {
            slotPattern.remove(slotValue);
            if (view instanceof MaterialCardView && img instanceof ImageView && txt instanceof TextView) {
                MaterialCardView materialCardView = (MaterialCardView) view;
                ImageView clock = (ImageView) img;
                TextView info = (TextView) txt;

                materialCardView.setStrokeColor(ContextCompat.getColor(getContext(), R.color.transparent));
                clock.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_slot_clock_ic));
                info.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));
            }

        } else {
            slotPattern.add(slotValue);
            if (view instanceof MaterialCardView && img instanceof ImageView && txt instanceof TextView) {
                MaterialCardView materialCardView = (MaterialCardView) view;
                ImageView clock = (ImageView) img;
                TextView info = (TextView) txt;

                materialCardView.setStrokeColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
                clock.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_slot_clock_ic_green));
                info.setTextColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
            }

        }
        finalSlotPattern = TextUtils.join(",", slotPattern);
    }

    private void addFieldClicked() {
    }

    private void btnContinueClicked(){

        if (binding.etName.getText().toString().isEmpty()){
            Functions.showToast(getContext(), getString(R.string.enter_club_name), FancyToast.ERROR);
            return;
        }
        else if (countryId.isEmpty()){
            Functions.showToast(getContext(), getString(R.string.select_country), FancyToast.ERROR);
            return;
        }
        else if (binding.etCity.getText().toString().isEmpty()){
            Functions.showToast(getContext(), getString(R.string.enter_city), FancyToast.ERROR);
            return;
        }
        else if (binding.etPhone.getText().toString().isEmpty()){
            Functions.showToast(getContext(), getString(R.string.enter_phone), FancyToast.ERROR);
            return;
        } else if (binding.tvLoc.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_location), FancyToast.ERROR);
            return;
        }


        int currentStep = binding.stepView.getCurrentStep();


        // If current step is less than total step count
        if (currentStep < binding.stepView.getStepCount()) {

            // Mark the current step as done (without moving)
            binding.stepView.done(false);


        switch(currentStep){
            case 0:
                binding.step1Vu.setVisibility(View.GONE);
                binding.step2Vu.setVisibility(View.VISIBLE);
                break;
            case 1:
                if (binding.hourSwitch.isChecked()){
                    timingJson = get24hoursTimeJson();
                } else{
                    timingJson = v5DayAdapter.getTimeJson();
                }
                if (timingJson.isEmpty() ||  timingJson.equals("[]")){
                    Functions.showToast(getContext(), "Please go back and add company opening and closing time.", FancyToast.ERROR);
                    return;
                }
                if (finalSlotPattern.isEmpty()){
                    Functions.showToast(getContext(), "Please select slot pattern for users.", FancyToast.ERROR);
                    return;
                }
                binding.step2Vu.setVisibility(View.GONE);
                binding.step3Vu.setVisibility(View.VISIBLE);
                break;
            case 2:
                facilityJson = getFacilities();
                if (facilityJson.isEmpty()){
                    Functions.showToast(getContext(), "Please add facilities.", FancyToast.ERROR);
                    return;
                }
                addClubApi(binding.etName.getText().toString(), binding.etPhone.getText().toString(),binding.etCity.getText().toString(), timingJson,  facilityJson, finalSlotPattern);
                break;
            default:

        }

        // If not the last step, move to the next step
        if (currentStep < binding.stepView.getStepCount() - 1) {
            binding.stepView.go(currentStep + 1, true);
        } else {
            // If it's the last step, mark it as done
            binding.stepView.done(true);
        }

        }

    }

    private void logoClicked() {
        imageType = ImageType.logo;
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.pick_image), getResources().getString(R.string.delete_image))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            pickImage();
                        }
                        else if (index == 1) {
                            logoImage = "";
                            binding.imgVuLogo.setImageResource(R.drawable.add_image);
                        }
                    }
                }).show();
    }

    private void bannerClicked() {
        imageType = ImageType.cover;
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.pick_image), getResources().getString(R.string.delete_image))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            pickImage();
                        }
                        else if (index == 1) {
                            coverImage = "";
                            binding.coverImgVu.setImageDrawable(null);
                        }
                    }
                }).show();
    }

    private String getFacilities(){
        String facilities = "";
        try {
            JSONArray array = new JSONArray();
            for (OleClubFacility facility : facilityAdapter.selectedFacility) {
                JSONObject object = new JSONObject();
                object.put("facility_id", facility.getId());
                object.put("price", facility.getPrice());
                object.put("type", facility.getType());
                object.put("unit", facility.getUnit());
                object.put("max_quantity", facility.getMaxQuantity());
                array.put(object);
            }
            facilities = array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return facilities;
    }

    private void countryClicked() {
        List<SelectionList> oleSelectionList = new ArrayList<>();
        if (AppManager.getInstance().countries.size() == 0) {
            KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
            getCountriesAPI(new CountriesCallback() {
                @Override
                public void getCountries(List<Country> countries) {
                    hud.dismiss();
                    AppManager.getInstance().countries = countries;
                    countryClicked();
                }
            });
        }
        else {
            for (Country oleCountry : AppManager.getInstance().countries) {
                oleSelectionList.add(new SelectionList(String.valueOf(oleCountry.getId()), oleCountry.getName(), oleCountry.getFlag()));
            }
            SelectionListDialog dialog = new SelectionListDialog(getContext(), getString(R.string.select_country), false);
            dialog.setLists(oleSelectionList);
            dialog.setShowSearch(true);
            dialog.setOnItemSelected(new SelectionListDialog.OnItemSelected() {
                @Override
                public void selectedItem(List<SelectionList> selectedItems) {
                    SelectionList selectedItem = selectedItems.get(0);
                    countryId = selectedItem.getId();
                    binding.etCountry.setText(selectedItem.getValue());
                    cityId = "";
                    binding.etCity.setText("");
                }
            });
            dialog.show();
        }
    }

    private void cityClicked() {
//        if (countryId.isEmpty()) {
//            Functions.showToast(getContext(), getString(R.string.select_country), FancyToast.ERROR);
//            return;
//        }
//        List<OleSelectionList> oleSelectionList = new ArrayList<>();
//        for (Country oleCountry : AppManager.getInstance().countries) {
//            if (String.valueOf(oleCountry.getId()).equalsIgnoreCase(countryId)) {
//                for (Country city : oleCountry.getCities()) {
//                    oleSelectionList.add(new OleSelectionList(String.valueOf(city.getId()), city.getName()));
//                }
//                break;
//            }
//        }
//        OleSelectionListDialog dialog = new OleSelectionListDialog(getContext(), getString(R.string.select_city), false);
//        dialog.setLists(oleSelectionList);
//        dialog.setOnItemSelected(new OleSelectionListDialog.OnItemSelected() {
//            @Override
//            public void selectedItem(List<OleSelectionList> selectedItems) {
//                OleSelectionList selectedItem = selectedItems.get(0);
//                cityId = selectedItem.getId();
//                binding.etCity.setText(selectedItem.getValue());
//            }
//        });
//        dialog.show();
    }

    private void locationClicked() {
        Intent intent = new Intent(getContext(), OleMapActivity.class);
        startActivityForResult(intent, 112);
    }

    private void pickImage() {
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
                easyImage.openChooser(OleAddClubActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                if (imageType == ImageType.logo) {
                    logoImage = resultUri.getPath();
                    logoFile = new File(logoImage);
                    Glide.with(getApplicationContext()).load(logoFile).into(binding.imgVuLogo);
                }
                else {
                    coverImage = resultUri.getPath();
                    coverFile = new File(coverImage);
                    Glide.with(getApplicationContext()).load(coverFile).into(binding.coverImgVu);
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        else if (requestCode == 112 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            latitude = bundle.getDouble("lat");
            longitude = bundle.getDouble("lng");
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            Functions.getAddressFromLocation(location, getContext(), new GeocoderHandler());
        }
        else {
            if (easyImage == null) {
                return;
            }
            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
                        CropImage.ActivityBuilder builder = CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(false).setScaleType(CropImageView.ScaleType.CENTER_INSIDE);
                        if (imageType == ImageType.logo) {
                            builder.setAspectRatio(1,1);
                        }
                        else {
                            builder.setAspectRatio(4,2);
                        }
                        builder.start(OleAddClubActivity.this);
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

    private void addClubApi(String name, String number, String city, String timings, String facilities, String finalSlotPattern) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        MultipartBody.Part coverPart = null;
        if (!coverImage.isEmpty()) {
            coverFile = new File(coverImage);
            String mimeTypeCover = getMimeType(coverFile);
            RequestBody fileReqBody = RequestBody.create(coverFile, MediaType.parse(mimeTypeCover));
            coverPart = MultipartBody.Part.createFormData("banner", coverFile.getName(), fileReqBody);
        }
        MultipartBody.Part logoPart = null;
        if (!logoImage.isEmpty()) {
            logoFile = new File(logoImage);
            String mimeTypeLogo = getMimeType(logoFile);
            RequestBody requestBody = RequestBody.create(logoFile, MediaType.parse(mimeTypeLogo));
            logoPart = MultipartBody.Part.createFormData("logo", logoFile.getName(), requestBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.addClub(coverPart, logoPart,
                RequestBody.create(name, MediaType.parse("text/plain")),
                RequestBody.create(countryId, MediaType.parse("text/plain")),
                RequestBody.create(number, MediaType.parse("text/plain")),
                RequestBody.create(city, MediaType.parse("text/plain")),
                RequestBody.create(String.valueOf(longitude),MediaType.parse("text/plain")),
                RequestBody.create(String.valueOf(latitude),MediaType.parse("text/plain")),
                RequestBody.create(timings, MediaType.parse("text/plain")),
                RequestBody.create(facilities, MediaType.parse("text/plain")),
                RequestBody.create(finalSlotPattern, MediaType.parse("text/plain")));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            binding.step3Vu.setVisibility(View.GONE);
                            binding.step4Vu.setVisibility(View.VISIBLE);
                            binding.tvBtnContinue.setText(getString(R.string.completed));
                            binding.tvBtnContinue.setTextColor(getContext().getColor(R.color.v5greenColor));
                            binding.btnContinueImg.setImageResource(R.drawable.v5_button_bg_green_op10);
                            clubAdded = true;
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

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String result = "";
            if (message.what == 1) {
                Bundle bundle = message.getData();
                result = bundle.getString("address");
            } else {
                result = "";
            }
            // replace by what you need to do
            binding.tvLoc.setText(result);
        }
    }

    public static String get24hoursTimeJson() {
        try {
            JSONArray array = new JSONArray();
            for (int dayId = 1; dayId <= 7; dayId++) { // Assuming you have days 1 to 6
                JSONObject dayObject = new JSONObject();
                dayObject.put("day_id", dayId);

                JSONArray shiftsArray = new JSONArray();
                // Assuming you want to add two shifts per day
                for (int i = 0; i < 1; i++) {
                    JSONObject shiftObject = new JSONObject();
                    shiftObject.put("start_time", "00:00:00");
                    shiftObject.put("end_time", "00:00:00");
                    shiftsArray.put(shiftObject);
                }

                dayObject.put("shifts", shiftsArray);
                array.put(dayObject);
            }
            return array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private void checkKeyboardListener() {
        OleKeyboardUtils.addKeyboardToggleListener(this, new OleKeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
                    binding.bottomContainer.setVisibility(View.GONE);
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //your code here
                                    binding.bottomContainer.setVisibility(View.VISIBLE);
                                }
                            }, 50);
                        }
                    });
                }
            }
        });
    }
}
