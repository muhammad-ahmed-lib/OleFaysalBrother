package ae.oleapp.owner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.FieldSizeAdapter;
import ae.oleapp.adapters.OleBookingFieldAdapter;
import ae.oleapp.adapters.OleClubDayAdapter;
import ae.oleapp.adapters.OleFieldColorListAdapter;
import ae.oleapp.adapters.OleFieldSizeAdapter;
import ae.oleapp.adapters.OleFieldTypeAdapter;
import ae.oleapp.adapters.OleGrassTypeAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.adapters.v5DayAdapter;
import ae.oleapp.adapters.v5FieldPriceAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityAddFieldBinding;
import ae.oleapp.dialogs.OleColorPreviewDialogFragment;
import ae.oleapp.models.Club;
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.FieldSizeList;
import ae.oleapp.models.OleColorModel;
import ae.oleapp.models.Field;
import ae.oleapp.models.OleFieldData;
import ae.oleapp.models.OleFieldDataChild;
import ae.oleapp.models.OleFieldPrice;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.UserInfo;
import ae.oleapp.signup.OwnerSignupConfirmationActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleKeyboardUtils;
import io.github.vejei.cupertinoswitch.CupertinoSwitch;
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

public class OleAddFieldActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityAddFieldBinding binding;
    private Club club;
    private int sizeId;
    private final String fieldTypeId = "";
    private String fieldType = "";
    private String environment = "";
    private String grassType = "";
    private final String grassTypeId = "";
    private final String color = "";
    private final String currentDayId = "";
    private final String isMerge = "0";
    private OleClubDayAdapter dayAdapter;
    private List<OleKeyValuePair> daysList = new ArrayList<>();
    private final List<OleFieldPrice> priceList = new ArrayList<>();
//    private final List<Field> fieldList = new ArrayList<>();
//    private OleBookingFieldAdapter fieldAdapter;
    private String fieldId = "";
    private String clubId = "";
    private String priceJson = "";
    private boolean isFootballUpdate = false;
    private boolean isPadelUpdate = false;
    private Field field;
    private File coverImage;
    private EasyImage easyImage;
    private final boolean isPadel = false;
    private OleFieldSizeAdapter oleFieldSizeAdapter;
    private OleFieldTypeAdapter oleFieldTypeAdapter;
    private OleGrassTypeAdapter oleGrassTypeAdapter;
    private OleFieldColorListAdapter colorListAdapter;
    private List<OleColorModel> colorList;
    private final int stepCount = 1;
    private OleRankClubAdapter oleRankClubAdapter;
    private FieldSizeAdapter fieldSizeAdapter;
    private final List<FieldSizeList> sizeList = new ArrayList<>();
    private v5FieldPriceAdapter priceAdapter;
    private String matchAllowed = "FALSE";
    private String friendlyGame = "FALSE";
    private int selectedColorIndex = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityAddFieldBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fieldId = bundle.getString("field_id", "");
            clubId = bundle.getString("club_id", "");
            isFootballUpdate = bundle.getBoolean("is_football_update", false);
            isPadelUpdate = bundle.getBoolean("is_padel_update", false);
        }

        colorList = Arrays.asList(
                new OleColorModel(getString(R.string.black), "#000000"),
                new OleColorModel(getString(R.string.gray), "#C4C4C4"),
                new OleColorModel(getString(R.string.blue), "#3A73C3"),
                new OleColorModel(getString(R.string.yellow), "#FFBA00"),
                new OleColorModel(getString(R.string.pink), "#FD6C9E"),
                new OleColorModel(getString(R.string.red), "#FE2717"),
                new OleColorModel(getString(R.string.purple), "#800080"),
                new OleColorModel(getString(R.string.aqua), "#76D4FB"));
        daysList = Functions.getDays(getContext());

        checkKeyboardListener();
        getFieldSize();

        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.sizeRecyclerVu.setLayoutManager(facLayoutManager);
        fieldSizeAdapter = new FieldSizeAdapter(getContext(), sizeList, -1);
        fieldSizeAdapter.setItemClickListener(itemClickListener);
        binding.sizeRecyclerVu.setAdapter(fieldSizeAdapter);

        LinearLayoutManager dayLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.priceRecyclerVu.setLayoutManager(dayLayoutManager);
        priceAdapter = new v5FieldPriceAdapter(getContext(), daysList);
        binding.priceRecyclerVu.setAdapter(priceAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.colorRecyclerVu.setLayoutManager(layoutManager);
        colorListAdapter = new OleFieldColorListAdapter(getContext(), colorList);
        colorListAdapter.setOnItemClickListener(colorClickListener);
        binding.colorRecyclerVu.setAdapter(colorListAdapter);
        colorListAdapter.setSelectedIndex(selectedColorIndex);


        binding.btnBack.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);
        binding.football.setOnClickListener(this);
        binding.padel.setOnClickListener(this);
        binding.indoorVu.setOnClickListener(this);
        binding.outdoorVu.setOnClickListener(this);
        binding.naturalVu.setOnClickListener(this);
        binding.artificialVu.setOnClickListener(this);
        binding.futsalVu.setOnClickListener(this);
        binding.priceSwitch.setOnStateChangeListener(stateChangeListener);
        binding.matchSwitch.setOnStateChangeListener(matchStateChangeListener);
        binding.friendlyGameSwitch.setOnStateChangeListener(gameStateChangeListener);
    }

    CupertinoSwitch.OnStateChangeListener stateChangeListener = new CupertinoSwitch.OnStateChangeListener() {
        @Override
        public void onChanged(CupertinoSwitch view, boolean checked) {

        }

        @Override
        public void onSwitchOn(CupertinoSwitch view) {
            binding.priceVu.setVisibility(View.VISIBLE);
            binding.priceLay.setVisibility(View.GONE);
        }

        @Override
        public void onSwitchOff(CupertinoSwitch view) {
            resetFixedPrice();
            binding.priceVu.setVisibility(View.GONE);
            binding.priceLay.setVisibility(View.VISIBLE);

        }
    };
    CupertinoSwitch.OnStateChangeListener matchStateChangeListener = new CupertinoSwitch.OnStateChangeListener() {
        @Override
        public void onChanged(CupertinoSwitch view, boolean checked) {

        }

        @Override
        public void onSwitchOn(CupertinoSwitch view) {
            matchAllowed = "TRUE";
        }

        @Override
        public void onSwitchOff(CupertinoSwitch view) {
            matchAllowed = "FALSE";
        }
    };
    CupertinoSwitch.OnStateChangeListener gameStateChangeListener = new CupertinoSwitch.OnStateChangeListener() {
        @Override
        public void onChanged(CupertinoSwitch view, boolean checked) {

        }

        @Override
        public void onSwitchOn(CupertinoSwitch view) {
            friendlyGame = "TRUE";

        }

        @Override
        public void onSwitchOff(CupertinoSwitch view) {
            friendlyGame = "FALSE";
        }
    };


    private void resetFixedPrice() {
        binding.tvOneHour.setText("");
        binding.tvOneHalfHour.setText("");
        binding.tvTwoHour.setText("");
        priceJson = "";
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


    @Override
    protected void onResume() {
        super.onResume();
//        if (!isPadelUpdate && !isFootballUpdate) {
//            getClubList(true);
//        }
    }


    @Override
    public void onClick(View v) {
        if (v == binding.btnBack || v == binding.btnHome) {
            finish();
        }
        else if (v == binding.btnContinue) {

            btnContinueClicked();
        }
        else if (v == binding.football) {
            stepSetup("football");
        }
        else if (v == binding.padel) {
            stepSetup("padel");
        }
        else if (v == binding.indoorVu) {
            stepSetup("indoor");
        }
        else if (v == binding.outdoorVu) {
            stepSetup("outdoor");
        }
        else if (v == binding.naturalVu) {
            stepSetup("natural");
        }
        else if (v == binding.artificialVu) {
            stepSetup("artificial");
        }
        else if (v == binding.futsalVu) {
            stepSetup("futsal");
        }

    }

    FieldSizeAdapter.ItemClickListener itemClickListener = new FieldSizeAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            fieldSizeAdapter.setSelectedIndex(pos);
            sizeId = sizeList.get(pos).getId();

        }
    };

    OleFieldColorListAdapter.OnItemClickListener colorClickListener = new OleFieldColorListAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            colorListAdapter.setSelectedIndex(pos);
            selectedColorIndex = pos;
            binding.fieldSizeTvSample.setCardBackgroundColor(Color.parseColor(colorList.get(selectedColorIndex).getColor()));

        }
    };


    private void btnContinueClicked(){
        int currentStep = binding.stepView.getCurrentStep();

        // Check if the current step is less than the total step count
        if (currentStep < binding.stepView.getStepCount()) {

            // Mark the current step as done (without moving)
            binding.stepView.done(false);

            // Only proceed if conditions for the current step are met
            switch(currentStep) {
                case 0:
                    if (binding.fieldName.getText().toString().isEmpty()){
                        Functions.showToast(getContext(), getString(R.string.enter_field_name), FancyToast.ERROR);
                        return;
                    }
                    if (fieldType.isEmpty()){
                        Functions.showToast(getContext(), "Please select field type", FancyToast.ERROR);
                        return;
                    }
                    if (environment.isEmpty()){
                        Functions.showToast(getContext(), "Please select environment", FancyToast.ERROR);
                        return;
                    }
                    if (grassType.isEmpty()){
                        Functions.showToast(getContext(), "Please select grass type", FancyToast.ERROR);
                        return;
                    }
                    if (sizeId == 0 ){
                        Functions.showToast(getContext(), "Please select field size", FancyToast.ERROR);
                        return;
                    }

                    binding.step1Vu.setVisibility(View.GONE);
                    binding.step2Vu.setVisibility(View.VISIBLE);
                    break;

                case 1:
                    // Check for hourly prices if the switch is checked
                    if (binding.priceSwitch.isChecked()) {
                        if (binding.tvOneHour.getText().toString().isEmpty() ||
                                binding.tvOneHalfHour.getText().toString().isEmpty() ||
                                binding.tvTwoHour.getText().toString().isEmpty()) {
                            Functions.showToast(getContext(), "Please add hourly prices.", FancyToast.ERROR);
                            return;
                        }
                        priceJson = getFixedPriceJson();
                    } else {
                        // Use getPricesAsJson only if priceJson is empty or unset
                        priceJson = v5FieldPriceAdapter.getPricesAsJson();
                        if (priceJson.isEmpty() || priceJson.equals("[]")) {
                            Functions.showToast(getContext(), "Please add prices.", FancyToast.ERROR);
                            return;
                        }
                    }

                    // Only go to the next step if priceJson is valid
                    binding.step2Vu.setVisibility(View.GONE);
                    binding.step3Vu.setVisibility(View.VISIBLE);
                    break;

                case 2:

                    addOwnerField(binding.fieldName.getText().toString(), colorList.get(selectedColorIndex).getColor());
                    break;

                default:
                    // Do nothing for undefined steps
            }

            // Move to the next step if all conditions for currentStep are met
            if (currentStep < binding.stepView.getStepCount() - 1) {
                binding.stepView.go(currentStep + 1, true);
            } else {
                // Mark as done if it's the last step
                binding.stepView.done(true);
            }
        }
    }



    public  String getFixedPriceJson() {
        try {
            JSONArray array = new JSONArray();

            // Fetch fixed prices from TextViews
            int oneHourPrice = Integer.parseInt(binding.tvOneHour.getText().toString());
            int oneHalfHourPrice = Integer.parseInt(binding.tvOneHalfHour.getText().toString());
            int twoHourPrice = Integer.parseInt(binding.tvTwoHour.getText().toString());

            // Loop through each day of the week
            for (int dayId = 1; dayId <= 7; dayId++) {
                JSONObject priceObject = new JSONObject();
                priceObject.put("day_id", dayId);
                priceObject.put("one_hour", oneHourPrice);
                priceObject.put("one_half", oneHalfHourPrice);
                priceObject.put("two_hour", twoHourPrice);
                array.put(priceObject);
            }

            return array.toString(); // Return the JSON array as a string
        } catch (JSONException e) {
            e.printStackTrace();
            return ""; // Return an empty string in case of an error
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ""; // Handle potential NumberFormatException from parsing the prices
        }
    }


    private void stepSetup(String val){
        if (val.equalsIgnoreCase("football")){
            fieldType = "FOOTBALL";
            binding.football.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.v5_green_button_bg));
            binding.tvFootball.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.footballIc.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_football_ic_active));

            binding.padel.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.v5_edit_text_bg_light_gray));
            binding.tvPadel.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            binding.padelIc.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_padel_ic));

        }
        else if (val.equalsIgnoreCase("padel")) {
            fieldType = "PADEL";
            binding.padel.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.v5_green_button_bg));
            binding.tvPadel.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.padelIc.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_padel_ic_active));

            binding.football.setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.v5_edit_text_bg_light_gray));
            binding.tvFootball.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            binding.footballIc.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_football_ic));

        }
        else if (val.equalsIgnoreCase("indoor")) {
            environment = "INDOOR";
            binding.indoorVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
            binding.tvIndoor.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

            binding.outdoorVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvOutdoor.setTextColor(ContextCompat.getColor(getContext(), R.color.black));


        }
        else if (val.equalsIgnoreCase("outdoor")) {
            environment = "OUTDOOR";
            binding.outdoorVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
            binding.tvOutdoor.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

            binding.indoorVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvIndoor.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }
        else if (val.equalsIgnoreCase("natural")) {
            grassType = "NATURAL";
            binding.naturalVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
            binding.tvNatural.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

            binding.artificialVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvArtificial.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

            binding.futsalVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvFutsal.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

        }
        else if (val.equalsIgnoreCase("artificial")) {
            grassType = "ARTIFICIAL";
            binding.artificialVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
            binding.tvArtificial.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

            binding.naturalVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvNatural.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

            binding.futsalVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvFutsal.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }
        else if (val.equalsIgnoreCase("futsal")) {
            grassType = "FUTSAL";
            binding.futsalVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
            binding.tvFutsal.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

            binding.naturalVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvNatural.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

            binding.artificialVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvArtificial.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }

    }
    private void getFieldSize() {
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getFieldSize();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray data = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            for (int i = 0; i < data.length(); i++) {
                                sizeList.add(gson.fromJson(data.get(i).toString(), FieldSizeList.class));
                            }
                            fieldSizeAdapter.notifyDataSetChanged();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });

    }






//
//    private void previewClicked() {
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        Fragment prev = getSupportFragmentManager().findFragmentByTag("ColorPreviewDialogFragment");
//        if (prev != null) {
//            fragmentTransaction.remove(prev);
//        }
//        fragmentTransaction.addToBackStack(null);
//        OleColorPreviewDialogFragment dialogFragment = new OleColorPreviewDialogFragment(colorList, colorListAdapter.getSelectedIndex());
//        dialogFragment.setDialogCallback(new OleColorPreviewDialogFragment.ColorPreviewDialogCallback() {
//            @Override
//            public void colorPicked(int colorPos) {
//                colorListAdapter.setSelectedIndex(colorPos);
//                OleColorModel oleColorModel = colorList.get(colorPos);
//                color = oleColorModel.getColor();
//            }
//        });
//        dialogFragment.show(fragmentTransaction, "ColorDialogFragment");
//    }
//
//    private void backClicked() {
//        if (stepCount == 2) {
//            moveToStep1();
//        }
//        else {
//            finish();
//        }
//    }
//
//    private void bannerClicked() {
//        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
//        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
//            @Override
//            public void onGranted() {
//                // do your task.
//                easyImage = new EasyImage.Builder(getContext())
//                        .setChooserType(ChooserType.CAMERA_AND_GALLERY)
//                        .setCopyImagesToPublicGalleryFolder(false)
//                        .allowMultiple(false).build();
//                easyImage.openChooser(OleAddFieldActivity.this);
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                Uri resultUri = result.getUri();
//                File file = new File(resultUri.getPath());
//                coverImage = file;
//                Glide.with(getContext()).load(file).into(binding.imgVuBanner);
//            }
//            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//                error.printStackTrace();
//            }
//        }
//        else {
//            if (easyImage == null) {
//                return;
//            }
//            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
//                @Override
//                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
//                    if (mediaFiles.length > 0) {
//                        CropImage.ActivityBuilder builder = CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
//                                .setGuidelines(CropImageView.Guidelines.ON)
//                                .setCropShape(CropImageView.CropShape.RECTANGLE)
//                                .setFixAspectRatio(false).setScaleType(CropImageView.ScaleType.CENTER_INSIDE);
//                        builder.setAspectRatio(4,2);
//                        builder.start(OleAddFieldActivity.this);
//                    }
//                }
//
//                @Override
//                public void onImagePickerError(Throwable error, MediaSource source) {
//                    Functions.showToast(getContext(), error.getLocalizedMessage(), FancyToast.ERROR);
//                }
//
//                @Override
//                public void onCanceled(@NonNull MediaSource mediaSource) {
//
//                }
//            });
//        }
//    }
//
//    private void addFootballClicked() {
//        if (stepCount == 1) {
//            if (!isFootballUpdate && club == null) {
//                Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR);
//                return;
//            }
//            if (binding.etFieldName.getText().toString().isEmpty()) {
//                Functions.showToast(getContext(), getString(R.string.enter_field_name), FancyToast.ERROR);
//                return;
//            }
//            if (sizeId.isEmpty()) {
//                Functions.showToast(getContext(), getString(R.string.select_field_size), FancyToast.ERROR);
//                return;
//            }
//            if (fieldTypeId.isEmpty()) {
//                Functions.showToast(getContext(), getString(R.string.select_field_type), FancyToast.ERROR);
//                return;
//            }
//            if (grassTypeId.isEmpty()) {
//                Functions.showToast(getContext(), getString(R.string.select_grass_type), FancyToast.ERROR);
//                return;
//            }
//            moveToStep2();
//        }
//        else {
//            String field1Id = "", field2Id = "", field3Id = "";
//            if (!isFootballUpdate) {
//                if (isMerge.equalsIgnoreCase("1")) {
//                    if (fieldAdapter.selectedFields.size() < 2) {
//                        Functions.showToast(getContext(), getString(R.string.select_atleast_teo_fields), FancyToast.ERROR);
//                        return;
//                    }
//                    for (int i = 0; i < fieldAdapter.selectedFields.size(); i++) {
//                        if (i == 0) {
//                            field1Id = fieldAdapter.selectedFields.get(i).getId();
//                        } else if (i == 1) {
//                            field2Id = fieldAdapter.selectedFields.get(i).getId();
//                        } else if (i == 2) {
//                            field3Id = fieldAdapter.selectedFields.get(i).getId();
//                        }
//                    }
//                }
//            }
//            if (binding.priceSwitch.isChecked()) {
//                if (binding.etOneHour.getText().toString().isEmpty() || binding.etOneHalfHour.getText().toString().isEmpty() || binding.etTwoHour.getText().toString().isEmpty()) {
//                    Functions.showToast(getContext(), getString(R.string.enter_price), FancyToast.ERROR);
//                    return;
//                }
//                addSamePrice();
//            }
//            else {
//                if (priceList.size() < 7) {
//                    Functions.showToast(getContext(), getString(R.string.enter_price), FancyToast.ERROR);
//                    return;
//                }
//            }
//
//            String price = "";
//            try {
//                JSONArray array = new JSONArray();
//                for (OleFieldPrice oleFieldPrice : priceList) {
//                    JSONObject object = new JSONObject();
//                    object.put("one_half_hour", oleFieldPrice.getOneHalfHour());
//                    object.put("one_hour", oleFieldPrice.getOneHour());
//                    object.put("two_hour", oleFieldPrice.getTwoHour());
//                    object.put("day_id", oleFieldPrice.getDayId());
//                    array.put(object);
//                }
//                price = array.toString();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            if (isFootballUpdate) {
//                updateFieldAPI(true, binding.etFieldName.getText().toString(), price);
//            }
//            else {
//                addFieldAPI(true, binding.etFieldName.getText().toString(), price, field1Id, field2Id, field3Id);
//            }
//        }
//    }
//
//    private void moveToStep1() {
//        binding.step1Vu.setVisibility(View.VISIBLE);
//        binding.step2Vu.setVisibility(View.GONE);
//        stepCount = 1;
//        binding.btnTitle.setText(R.string.next);
//    }
//
//    private void moveToStep2() {
//        binding.step1Vu.setVisibility(View.GONE);
//        binding.step2Vu.setVisibility(View.VISIBLE);
//        stepCount = 2;
//        if (isFootballUpdate || isPadelUpdate) {
//            binding.btnTitle.setText(R.string.update);
//        }
//        else {
//            binding.btnTitle.setText(R.string.add_now);
//        }
//    }
//
//    private void addPadelClicked() {
//        if (stepCount == 1) {
//            if (!isPadelUpdate && coverImage == null) {
//                Functions.showToast(getContext(), getString(R.string.add_cover_image), FancyToast.ERROR);
//                return;
//            }
//            if (!isPadelUpdate && club == null) {
//                Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR);
//                return;
//            }
//            if (binding.etFieldName.getText().toString().isEmpty()) {
//                Functions.showToast(getContext(), getString(R.string.enter_field_name), FancyToast.ERROR);
//                return;
//            }
//            if (fieldTypeId.isEmpty()) {
//                Functions.showToast(getContext(), getString(R.string.select_field_type), FancyToast.ERROR);
//                return;
//            }
//            moveToStep2();
//        }
//        else {
//            if (binding.priceSwitch.isChecked()) {
//                if (binding.etOneHour.getText().toString().isEmpty() || binding.etOneHalfHour.getText().toString().isEmpty() || binding.etTwoHour.getText().toString().isEmpty()) {
//                    Functions.showToast(getContext(), getString(R.string.enter_price), FancyToast.ERROR);
//                    return;
//                }
//                addSamePrice();
//            }
//            else {
//                if (priceList.size() < 7) {
//                    Functions.showToast(getContext(), getString(R.string.enter_price), FancyToast.ERROR);
//                    return;
//                }
//            }
//
//            String price = "";
//            try {
//                JSONArray array = new JSONArray();
//                for (OleFieldPrice oleFieldPrice : priceList) {
//                    JSONObject object = new JSONObject();
//                    object.put("one_half_hour", oleFieldPrice.getOneHalfHour());
//                    object.put("one_hour", oleFieldPrice.getOneHour());
//                    object.put("two_hour", oleFieldPrice.getTwoHour());
//                    object.put("day_id", oleFieldPrice.getDayId());
//                    array.put(object);
//                }
//                price = array.toString();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            if (isPadelUpdate) {
//                updatePadelFieldApi(binding.etFieldName.getText().toString(), price);
//            }
//            else {
//                addPadelFieldApi(binding.etFieldName.getText().toString(), price);
//            }
//        }
//    }
//
//    private void mergeSwitchChanged(SwitchCompat aSwitch) {
//        if (aSwitch.isChecked()) {
//            if (club == null) {
//                aSwitch.setChecked(false);
//                Functions.showToast(getContext(), getString(R.string.select_club), FancyToast.ERROR);
//                return;
//            }
//            if (club.getFieldsCount() >= 2) {
//                isMerge = "1";
//                if (fieldList.size() == 0) {
//                    getAllFieldsAPI(true, club.getId());
//                }
//                binding.fieldRecyclerVu.setVisibility(View.VISIBLE);
//            } else {
//                aSwitch.setChecked(false);
//                Functions.showToast(getContext(), getString(R.string.min_two_fields_merge), FancyToast.ERROR);
//            }
//        }
//        else {
//            isMerge = "0";
//            binding.fieldRecyclerVu.setVisibility(View.GONE);
//        }
//    }
//
//    private void populateData() {
//        if (isFootballUpdate) {
//            sizeId = field.getFieldSize().getId();
//            for (int i = 0; i < AppManager.getInstance().oleFieldData.getFieldSizes().size(); i++) {
//                OleFieldDataChild dataChild = AppManager.getInstance().oleFieldData.getFieldSizes().get(i);
//                if (dataChild.getId().equalsIgnoreCase(sizeId)) {
//                    oleFieldSizeAdapter.setSelectedIndex(i);
//                    break;
//                }
//            }
//            grassTypeId = field.getGrassType().getId();
//            for (int i = 0; i < AppManager.getInstance().oleFieldData.getGrassType().size(); i++) {
//                OleFieldDataChild dataChild = AppManager.getInstance().oleFieldData.getGrassType().get(i);
//                if (dataChild.getId().equalsIgnoreCase(grassTypeId)) {
//                    oleGrassTypeAdapter.setSelectedIndex(i);
//                    break;
//                }
//            }
//            isMerge = field.getIsMerge();
//        }
//        if (isPadelUpdate && field.getImages() != null && field.getImages().size() > 0) {
//            Glide.with(getContext()).load(field.getImages().get(0).getPhotoPath()).into(binding.imgVuBanner);
//        }
//        binding.etFieldName.setText(field.getName());
//        fieldTypeId = field.getFieldType().getId();
//        for (int i = 0; i < AppManager.getInstance().oleFieldData.getFiledTypes().size(); i++) {
//            OleFieldDataChild dataChild = AppManager.getInstance().oleFieldData.getFiledTypes().get(i);
//            if (dataChild.getId().equalsIgnoreCase(fieldTypeId)) {
//                oleFieldTypeAdapter.setSelectedIndex(i);
//                break;
//            }
//        }
//        color = field.getFieldColor();
//        for (int i = 0; i < colorList.size(); i++) {
//            if (colorList.get(i).getColor().equalsIgnoreCase(color)) {
//                colorListAdapter.setSelectedIndex(i);
//                break;
//            }
//        }
//        priceList.clear();
//        priceList.addAll(field.getDaysPrice());
//        currentDayId = "1";
//        dayAdapter.setCurrentDayId(currentDayId);
//        dayAdapter.notifyDataSetChanged();
//        for (OleFieldPrice oleFieldPrice : priceList) {
//            if (oleFieldPrice.getDayId().equalsIgnoreCase(currentDayId)) {
//                binding.etOneHour.setText(oleFieldPrice.getOneHour());
//                binding.etOneHalfHour.setText(oleFieldPrice.getOneHalfHour());
//                binding.etTwoHour.setText(oleFieldPrice.getTwoHour());
//                break;
//            }
//        }
//    }
//
//    private void getAllFieldsAPI(boolean isLoader, String clubId) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getAllFields(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), clubId);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            JSONArray arr = object.getJSONArray(Constants.kData);
//                            fieldList.clear();
//                            Gson gson = new Gson();
//                            for (int i = 0; i < arr.length(); i++) {
//                                Field field = gson.fromJson(arr.get(i).toString(), Field.class);
//                                fieldList.add(field);
//                            }
//                            if (isFootballUpdate) {
//                                for (Field f: fieldList) {
//                                    if (f.getId().equalsIgnoreCase(field.getField1Id()) || f.getId().equalsIgnoreCase(field.getField2Id()) || f.getId().equalsIgnoreCase(field.getField3Id())) {
//                                        fieldAdapter.selectedFields.add(f);
//                                    }
//                                }
//                            }
//                            fieldAdapter.notifyDataSetChanged();
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
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
//    private void getOneFieldAPI(boolean isLoader) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.getOneField(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), fieldId);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            JSONObject obj = object.getJSONObject(Constants.kData);
//                            Gson gson = new Gson();
//                            field = gson.fromJson(obj.toString(), Field.class);
//                            populateData();
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
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
//    private void addFieldAPI(boolean isLoader, String name, String price, String field1Id, String field2Id, String field3Id) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addField(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID),
//                club.getId(), name, fieldTypeId, sizeId, grassTypeId, color, isMerge, field1Id, field2Id, field3Id, price);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
//                            finish();
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
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
//    private void addPadelFieldApi(String name, String price) {
//        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
//        MultipartBody.Part coverPart = null;
//        if (coverImage != null) {
//            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), coverImage);
//            coverPart = MultipartBody.Part.createFormData("cover", coverImage.getName(), fileReqBody);
//        }
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.addPadelField(coverPart,
//                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
//                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
//                RequestBody.create(MediaType.parse("text/plain"), club.getId()),
//                RequestBody.create(MediaType.parse("text/plain"), name),
//                RequestBody.create(MediaType.parse("text/plain"), fieldTypeId),
//                RequestBody.create(MediaType.parse("text/plain"), color),
//                RequestBody.create(MediaType.parse("text/plain"), price));
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
//                            finish();
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
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
//
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
//    private void updatePadelFieldApi(String name, String price) {
//        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
//        MultipartBody.Part coverPart = null;
//        if (coverImage != null) {
//            RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), coverImage);
//            coverPart = MultipartBody.Part.createFormData("cover", coverImage.getName(), fileReqBody);
//        }
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updatePadelField(coverPart,
//                RequestBody.create(MediaType.parse("text/plain"), Functions.getAppLang(getContext())),
//                RequestBody.create(MediaType.parse("text/plain"), Functions.getPrefValue(getContext(), Constants.kUserID)),
//                RequestBody.create(MediaType.parse("text/plain"), clubId),
//                RequestBody.create(MediaType.parse("text/plain"), fieldId),
//                RequestBody.create(MediaType.parse("text/plain"), name),
//                RequestBody.create(MediaType.parse("text/plain"), fieldTypeId),
//                RequestBody.create(MediaType.parse("text/plain"), color),
//                RequestBody.create(MediaType.parse("text/plain"), price));
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
//                            finish();
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
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
//
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
//    private void updateFieldAPI(boolean isLoader, String name, String price) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.updateField(Functions.getAppLang(getContext()),
//                Functions.getPrefValue(getContext(), Constants.kUserID),
//                clubId, fieldId, name, fieldTypeId, sizeId, grassTypeId, color, price);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
//                            finish();
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
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
//    private void getClubList(boolean isLoader) {
//        Call<ResponseBody> call;
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
//        call = AppManager.getInstance().apiInterface.getMyClubs(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID), "");
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            JSONArray arr = object.getJSONArray(Constants.kData);
//                            Gson gson = new Gson();
//                            AppManager.getInstance().clubs.clear();
//                            for (int i = 0; i < arr.length(); i++) {
//                                Club club = gson.fromJson(arr.get(i).toString(), Club.class);
//                                AppManager.getInstance().clubs.add(club);
//                            }
//                            populateClub();
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
//
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


    private void addOwnerField(String fieldName, String borderColor) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.addOwnerField(Integer.parseInt(clubId),sizeId,fieldName,borderColor,grassType,environment,fieldType,matchAllowed,friendlyGame,priceJson,"");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            binding.step3Vu.setVisibility(View.GONE);
                            binding.step4Vu.setVisibility(View.VISIBLE);
                            binding.tvBtnContinue.setText("Completed");
                            binding.tvBtnContinue.setTextColor(getContext().getColor(R.color.v5greenColor));
                            binding.btnContinueImg.setImageResource(R.drawable.v5_button_bg_green_op10);

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
