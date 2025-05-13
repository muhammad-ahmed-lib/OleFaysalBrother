package ae.oleapp.promotions.gifts;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import ae.oleapp.R;
import ae.oleapp.adapters.GiftTargetAdapter;
import ae.oleapp.adapters.UniversalAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddUpdateGiftBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.Gift;
import ae.oleapp.models.GiftTarget;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
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

public class AddUpdateGiftActivity extends BaseActivity implements View.OnClickListener {
    private ActivityAddUpdateGiftBinding binding;
    public String selectedClubId = "";
    private int selectedIndex = 0;
    private final List<GiftTarget> giftTargetList = new ArrayList<>();
    private  List<Club> clubList = new ArrayList<>();
    private UniversalAdapter oleClubNameAdapter;
    private GiftTargetAdapter giftTargetAdapter;
    private boolean update = false;
    private int giftId;
    private String selectedClubIds = "";
    private String startDate = "", endDate = "";
    private String dateRange = "";
    private String targetId = "";
    private String photoFilePath;
    private EasyImage easyImage;
    private File file = new File("");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUpdateGiftBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            update = bundle.getBoolean("update");
            giftId = bundle.getInt("id");
        }

        getGiftTargetList(true);


        clubList.clear();
        clubList = AppManager.getInstance().clubs;
        LinearLayoutManager oleClubNameLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubNameRecyclerVu.setLayoutManager(oleClubNameLayoutManager);
        oleClubNameAdapter = new UniversalAdapter(getContext(), clubList);
        oleClubNameAdapter.setItemClickListener(clubNameClickListener);
        binding.clubNameRecyclerVu.setAdapter(oleClubNameAdapter);

        LinearLayoutManager giftsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.giftTargetRecyclerVu.setLayoutManager(giftsLayoutManager);
        giftTargetAdapter = new GiftTargetAdapter(getContext(), giftTargetList);
        giftTargetAdapter.setItemClickListener(itemClickListener);
        binding.giftTargetRecyclerVu.setAdapter(giftTargetAdapter);

        if (update){
            getGiftDetails(false, giftId);
        }

        binding.btnBack.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.imgVu.setOnClickListener(this);
        binding.etDate.setOnClickListener(this);

        binding.switchAllClub.setOnStateChangeListener(new CupertinoSwitch.OnStateChangeListener() {
            @Override
            public void onChanged(CupertinoSwitch view, boolean checked) {
                for (Club club: clubList){
                    toggleSelection(club.getId());
                    oleClubNameAdapter.selectClubs(club);
                }
            }

            @Override
            public void onSwitchOn(CupertinoSwitch view) {
            }

            @Override
            public void onSwitchOff(CupertinoSwitch view) {

            }
        });


    }

    UniversalAdapter.ItemClickListener clubNameClickListener = new UniversalAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Club club = clubList.get(pos);
            selectedIndex = pos;
            selectedClubId = club.getId();
            toggleSelection(selectedClubId);
            oleClubNameAdapter.selectClubs(club);

//            selectedIndex = pos;
//            selectedClubId = String.valueOf(clubList.get(selectedIndex).getId()); //check this
//            oleClubNameAdapter.setSelectedIndex(selectedIndex);
//            toggleSelection(selectedClubId);


        }
    };


    GiftTargetAdapter.ItemClickListener itemClickListener = new GiftTargetAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            giftTargetAdapter.setSelectedIndex(pos);
            targetId = String.valueOf(giftTargetList.get(pos).getId());

        }
    };


    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.imgVu) {
            updateGiftImage();

        }
        else if (v == binding.btnAdd) {
            if (binding.etName.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Please write title", FancyToast.INFO);
                return;
            }

            if (binding.etDesc.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Please write description", FancyToast.INFO);
                return;
            }

            if (startDate.isEmpty() || endDate.isEmpty()){
                Functions.showToast(getContext(), "Please enter date", FancyToast.INFO);
                return;
            }

            if (selectedClubIds.isEmpty()){
                Functions.showToast(getContext(), "Please chose club", FancyToast.INFO);
                return;
            }

            if (targetId.isEmpty()){
                Functions.showToast(getContext(), "Please chose type of gift", FancyToast.INFO);
                return;
            }

            if (update){
                updateGiftApi(true,selectedClubIds,binding.etName.getText().toString(),targetId,startDate,endDate,binding.etDesc.getText().toString());

            }
            else{
                addGiftApi(true,selectedClubIds,binding.etName.getText().toString(),targetId,startDate,endDate,binding.etDesc.getText().toString());
            }


        }
        else if (v == binding.etDate) {
            showDateRangePicker();
        }

    }


    private void updateGiftImage() {
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
                //file = new File(resultUri.getPath());
                photoFilePath = resultUri.getPath();
                file = new File(photoFilePath);
                Glide.with(getApplicationContext()).load(file).into(binding.imgVu);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        else {
            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                .setAspectRatio(1,1)
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



    private void getGiftDetails(boolean isLoader, int id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getGiftDetails(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            Gift gift = new Gson().fromJson(data.toString(), Gift.class);
                            for (int i=0; i<giftTargetList.size(); i++ ){
                                if (Objects.equals(giftTargetList.get(i).getId(), gift.getGiftTarget().getId())){
                                    giftTargetAdapter.setSelectedIndex(i);
                                    targetId = String.valueOf(giftTargetList.get(i).getId());
                                    break;
                                }
                            }
                            for (int i=0; i<clubList.size(); i++ ){
                                if (Objects.equals(clubList.get(i).getId(), gift.getClub().getId())){
                                    oleClubNameAdapter.setSelectedIndex(i);
                                    selectedClubId = String.valueOf(clubList.get(i).getId());
                                    binding.switchAllClub.setChecked(true);
                                    break;
                                }
                            }
                            populateData(gift);

                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
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

    private void getGiftTargetList(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getGiftTargetList();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            giftTargetList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                GiftTarget giftTarget = gson.fromJson(arr.get(i).toString(), GiftTarget.class);
                                giftTargetList.add(giftTarget);
                            }
                            giftTargetAdapter.notifyDataSetChanged();

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

    private void addGiftApi(boolean isLoader, String selectedClubIds, String title, String targetId, String startDate, String endDate, String desc) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        MultipartBody.Part filePart = null;
        if (photoFilePath != null) {
            File file = new File(photoFilePath);
            String mimeTypeCover = getMimeType(file);
            RequestBody requestBody = RequestBody.create(file, MediaType.parse(mimeTypeCover));
            filePart = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.addGiftApi(filePart,
                RequestBody.create(selectedClubIds, MediaType.parse("text/plain")),
                RequestBody.create(title, MediaType.parse("text/plain")),
                RequestBody.create(targetId, MediaType.parse("text/plain")),
                RequestBody.create(startDate, MediaType.parse("text/plain")),
                RequestBody.create(endDate, MediaType.parse("text/plain")),
                RequestBody.create(desc, MediaType.parse("text/plain")));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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

    private void updateGiftApi(boolean isLoader, String selectedClubIds, String title, String targetId, String startDate, String endDate, String desc) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        MultipartBody.Part filePart = null;
        if (photoFilePath != null) {
            File file = new File(photoFilePath);
            String mimeTypeCover = getMimeType(file);
            RequestBody requestBody = RequestBody.create(file, MediaType.parse(mimeTypeCover));
            filePart = MultipartBody.Part.createFormData("photo", file.getName(), requestBody);
        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.updateGiftApi(filePart,
                RequestBody.create(String.valueOf(giftId), MediaType.parse("text/plain")),
                RequestBody.create(selectedClubIds, MediaType.parse("text/plain")),
                RequestBody.create(title, MediaType.parse("text/plain")),
                RequestBody.create(targetId, MediaType.parse("text/plain")),
                RequestBody.create(startDate, MediaType.parse("text/plain")),
                RequestBody.create(endDate, MediaType.parse("text/plain")),
                RequestBody.create(desc, MediaType.parse("text/plain")));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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

    private void populateData(Gift gift) {
        binding.title.setText("Update Gift");
        binding.tvBtn.setText("Update");
        binding.etName.setText(gift.getTitle());
        binding.etDesc.setText(gift.getDescription());
        binding.etDate.setText(String.format("%s - %s", gift.getStartDate(), gift.getEndDate()));
        Glide.with(getApplicationContext()).load(gift.getPhotoUrl()).into(binding.imgVu);
        startDate =  formatDate(gift.getStartDate());
        endDate  = formatDate(gift.getEndDate());
        targetId = String.valueOf(gift.getGiftTarget().getId());
    }

    private void showDateRangePicker() {
        // Select Start Date
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // Format and store the start date
            startDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);

            // Select End Date
            new DatePickerDialog(this, (view2, endYear, endMonth, endDayOfMonth) -> {
                // Format and store the end date
                endDate = String.format("%04d-%02d-%02d", endYear, endMonth + 1, endDayOfMonth);

                // Generate the date range string
                dateRange = startDate + " - " + endDate;

                binding.etDate.setText(dateRange);

                // Show the result or use it
                Toast.makeText(this, "Date Range: " + dateRange, Toast.LENGTH_SHORT).show();

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public static String formatDate(String dateStr) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void toggleSelection(String item) {
        if (selectedClubIds.contains(item)) {
            selectedClubIds = selectedClubIds.replace(item, "").replace(",,", ",").trim();
            if (selectedClubIds.startsWith(",")) {
                selectedClubIds = selectedClubIds.substring(1);
            }
            if (selectedClubIds.endsWith(",")) {
                selectedClubIds = selectedClubIds.substring(0, selectedClubIds.length() - 1);
            }
        } else {
            if (!selectedClubIds.isEmpty()) {
                selectedClubIds += ",";
            }
            selectedClubIds += item;
        }
    }




}