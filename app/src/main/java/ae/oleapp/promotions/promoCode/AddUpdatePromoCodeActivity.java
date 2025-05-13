package ae.oleapp.promotions.promoCode;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ae.oleapp.R;
import ae.oleapp.adapters.UniversalAdapter;
import ae.oleapp.adapters.UniversalFieldAdapter;
import ae.oleapp.adapters.UniversalSlotPatternAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddUpdatePromotionBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.GeneralField;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.PromoCode;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.github.vejei.cupertinoswitch.CupertinoSwitch;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUpdatePromoCodeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddUpdatePromotionBinding binding;
    private String startDate, endDate, dateRange;
    private int selectedIndex = 0, selectedFieldIndex = 0, selectedDurationIndex = 0;
    private String selectedClubId = "";
    private String selectedClubIds = "";
    private String selectedFieldId = "";
    private String selectedDurationId = "";
    private String selectedFieldIds = "";
    private String discountType = "";
    private String promoType = "";
    private final String playerIds = "";
    private List<Club> clubList = new ArrayList<>();
    private final List<GeneralField> fieldList = new ArrayList<>();
    private final List<OleKeyValuePair> durationList = new ArrayList<>();
    private UniversalAdapter oleClubNameAdapter;
    private UniversalFieldAdapter fieldAdapter;
    private UniversalSlotPatternAdapter slotPatternAdapter;
    private final boolean promoAdded = false;
    private boolean update = false;
    private int promoId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUpdatePromotionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            update = bundle.getBoolean("update");
            promoId = bundle.getInt("id");
            if (update){
                getPromoDetail(false, promoId);
            }
        }

        clubList.clear();
        clubList = AppManager.getInstance().clubs;

        LinearLayoutManager oleClubNameLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubNameRecyclerVu.setLayoutManager(oleClubNameLayoutManager);
        oleClubNameAdapter = new UniversalAdapter(getContext(), clubList);
        oleClubNameAdapter.setItemClickListener(clubNameClickListener);
        binding.clubNameRecyclerVu.setAdapter(oleClubNameAdapter);

        LinearLayoutManager fieldLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.fieldNameRecyclerVu.setLayoutManager(fieldLayoutManager);
        fieldAdapter = new UniversalFieldAdapter(getContext(), fieldList);
        fieldAdapter.setItemClickListener(fieldClickListener);
        binding.fieldNameRecyclerVu.setAdapter(fieldAdapter);

        LinearLayoutManager slotPatternLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.slotsPatternRecyclerVu.setLayoutManager(slotPatternLayoutManager);
        slotPatternAdapter = new UniversalSlotPatternAdapter(getContext(), durationList);
        slotPatternAdapter.setItemClickListener(slotPatternClickListener);
        binding.slotsPatternRecyclerVu.setAdapter(slotPatternAdapter);


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
//                    binding.fieldNameRecyclerVu.setVisibility(View.GONE);
//                    binding.slotsPatternRecyclerVu.setVisibility(View.GONE);
            }

            @Override
            public void onSwitchOff(CupertinoSwitch view) {
//                binding.fieldNameRecyclerVu.setVisibility(View.VISIBLE);
//                binding.slotsPatternRecyclerVu.setVisibility(View.VISIBLE);

            }
        });
        binding.switchAutoMsg.setOnStateChangeListener(new CupertinoSwitch.OnStateChangeListener() {
            @Override
            public void onChanged(CupertinoSwitch view, boolean checked) {

            }

            @Override
            public void onSwitchOn(CupertinoSwitch view) {

            }

            @Override
            public void onSwitchOff(CupertinoSwitch view) {

            }
        });

        binding.btnBack.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.etDate.setOnClickListener(this);
        binding.percentageLay.setOnClickListener(this);
        binding.fixedLay.setOnClickListener(this);
        binding.birdayLay.setOnClickListener(this);
        binding.newUserLay.setOnClickListener(this);
        binding.normalLay.setOnClickListener(this);
        binding.btnEligiblePlayer.setOnClickListener(this);
        binding.specificLay.setOnClickListener(this);
        binding.allPlayerLay.setOnClickListener(this);
        binding.btnAssignPlayer.setOnClickListener(this);
        binding.limitedLay.setOnClickListener(this);
        binding.unlimitedLay.setOnClickListener(this);

    }

    UniversalAdapter.ItemClickListener clubNameClickListener = new UniversalAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Club club = clubList.get(pos);
            selectedIndex = pos;
            selectedClubId = club.getId();
            toggleSelection(selectedClubId);
            oleClubNameAdapter.selectClubs(club);
            getClubFields(true, club.getId());

        }
    };

    UniversalFieldAdapter.ItemClickListener fieldClickListener = new UniversalFieldAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            GeneralField field = fieldList.get(pos);
            selectedFieldIndex = pos;
            selectedFieldId = String.valueOf(field.getId());
            toggleFieldSelection(selectedFieldId);
            fieldAdapter.selectedFields(field);
            populateDuration(false);

        }
    };

    UniversalSlotPatternAdapter.ItemClickListener slotPatternClickListener = new UniversalSlotPatternAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OleKeyValuePair duration = durationList.get(pos);
            selectedDurationIndex = pos;
            selectedDurationId = String.valueOf(duration.getKey());
            toggleDurationSelection(selectedDurationId);
            slotPatternAdapter.selectedDurationIds(duration);
        }
    };


    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.btnAdd) {
            btnContinueClicked();

        }
        else if (v == binding.etDate) {
            showDateRangePicker();
        }
        else if (v == binding.percentageLay) {

            percentageClicked();

        }
        else if (v == binding.fixedLay) {
            fixedClicked();
        }
        else if (v == binding.birdayLay) {
          birdayClicked();

        }
        else if (v == binding.newUserLay) {
            newUserClicked();


        }
        else if (v == binding.normalLay) {
           normalClicked();
        }
        else if (v == binding.btnEligiblePlayer) {

        }
        else if (v == binding.specificLay) {
            specificClicked();

        }
        else if (v == binding.allPlayerLay) {
            allPlayerClicked();
        }
        else if (v == binding.btnAssignPlayer) {

        }
        else if (v == binding.limitedLay) {
           limitedClicked();

        }
        else if (v == binding.unlimitedLay) {
          unlimitedClicked();

        }


    }

    private void unlimitedClicked() {
        binding.unlimitedLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
        binding.tvUnlimited.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

        binding.limitedLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.tvlimited.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));

        binding.maxLay.setVisibility(View.GONE);
        binding.eachPlayerLay.setVisibility(View.GONE);
    }

    private void limitedClicked() {
        binding.limitedLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
        binding.tvlimited.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

        binding.unlimitedLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.tvUnlimited.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));

        binding.maxLay.setVisibility(View.VISIBLE);
        binding.eachPlayerLay.setVisibility(View.VISIBLE);
    }

    private void allPlayerClicked() {
        binding.allPlayerLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
        binding.tvAllPlayer.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

        binding.specificLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.tvSpecific.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));

        binding.assignPlayerLay.setVisibility(View.GONE);
    }

    private void specificClicked() {
        binding.specificLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
        binding.tvSpecific.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

        binding.allPlayerLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.tvAllPlayer.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));

        binding.assignPlayerLay.setVisibility(View.VISIBLE);
    }

    private void normalClicked() {
        promoType = "NORMAL";
        binding.birdayLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.tvBirday.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));

        binding.newUserLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.tvNewUser.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));

        binding.normalLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
        binding.tvNormal.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

        binding.eligiblePlayerLay.setVisibility(View.GONE);
        binding.normalSelectedLay.setVisibility(View.VISIBLE);
    }

    private void newUserClicked() {
        promoType = "NEW_USER";
        binding.birdayLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.tvBirday.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));

        binding.newUserLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
        binding.tvNewUser.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

        binding.normalLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.tvNormal.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));
        binding.eligiblePlayerLay.setVisibility(View.VISIBLE);
    }

    private void birdayClicked() {
        promoType = "BIRTHDAY";
        binding.birdayLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
        binding.tvBirday.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

        binding.newUserLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.tvNewUser.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));

        binding.normalLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.tvNormal.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));
        binding.eligiblePlayerLay.setVisibility(View.VISIBLE);
    }

    private void fixedClicked() {
        discountType = "FLAT_AMOUNT";
        binding.percentageLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.tvPercentage.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));

        binding.fixedLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
        binding.tvFixed.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
    }

    private void percentageClicked() {
        discountType = "PERCENTAGE";
        binding.percentageLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
        binding.tvPercentage.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

        binding.fixedLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.tvFixed.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));
    }

    private void btnContinueClicked(){

        if (binding.etName.getText().toString().isEmpty()){
            Functions.showToast(getContext(), "Title cannot be empty", FancyToast.ERROR);
            return;
        }
        else if (binding.etPromoCode.getText().toString().isEmpty()){
            Functions.showToast(getContext(), "Promo cannot be empty", FancyToast.ERROR);
            return;
        }
        else if (binding.etDate.getText().toString().isEmpty()){
            Functions.showToast(getContext(), "Date cannot be empty", FancyToast.ERROR);
            return;
        }

        if (!update){
            addPromoCode(promoType,  binding.etName.getText().toString(), binding.etPromoCode.getText().toString().trim(), binding.etDiscount.getText().toString(), discountType,
                    startDate, endDate,  binding.etUsageLimit.getText().toString(), binding.etEachPlayerLimit.getText().toString(), selectedClubIds, selectedFieldIds, selectedDurationId, playerIds);
        }
        else{
            updatePromoCode(promoType,  binding.etName.getText().toString(), binding.etPromoCode.getText().toString().trim(), binding.etDiscount.getText().toString(), discountType, startDate, endDate,  binding.etUsageLimit.getText().toString(), binding.etEachPlayerLimit.getText().toString(), selectedClubIds, selectedFieldIds, selectedDurationId, playerIds);
        }


    }
    private void addPromoCode(String promoType, String name, String code, String discount, String discountType, String startDate, String endDate, String usageLimit, String eachPlayerLimit, String clubIds, String fieldIds, String durations, String playerIds) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.addPromoCode(promoType, name, code, discount, discountType, startDate,
                endDate, usageLimit, eachPlayerLimit, clubIds, fieldIds, durations, playerIds);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    private void updatePromoCode(String promoType, String name, String code, String discount, String discountType, String startDate, String endDate, String usageLimit, String eachPlayerLimit, String clubIds, String fieldIds, String durations, String playerIds) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.updatePromoCode(promoId, promoType, name, code, discount, discountType, startDate,
                endDate, clubIds, fieldIds, durations, playerIds);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void getClubFields(boolean isLoader, String clubId) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getClubFields(Integer.parseInt(clubId));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            fieldList.clear();
                            for (int i=0; i<arr.length();i++) {
                                GeneralField field = gson.fromJson(arr.get(i).toString(), GeneralField.class);
                                fieldList.add(field);
                            }
                            fieldAdapter.notifyDataSetChanged();
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
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void populateDuration(boolean isRefresh) {
        durationList.clear();
        if (!clubList.get(selectedIndex).getSlotPattern().isEmpty()){
            for (int i = 0; i<clubList.get(selectedIndex).getSlotPattern().size(); i++){
                if (clubList.get(selectedIndex).getSlotPattern().get(i).equalsIgnoreCase("1")){
                    durationList.add(new OleKeyValuePair("1", getString(R.string.one_hour)));
                }
                else if (clubList.get(selectedIndex).getSlotPattern().get(i).equalsIgnoreCase("1.5")) {
                    durationList.add(new OleKeyValuePair("1.5", getString(R.string.one_half_hour)));
                }else if (clubList.get(selectedIndex).getSlotPattern().get(i).equalsIgnoreCase("2")){
                    durationList.add(new OleKeyValuePair("2", getString(R.string.two_hour)));
                }
            }

        }
        slotPatternAdapter.notifyDataSetChanged();

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

    public void toggleFieldSelection(String item) {
        if (selectedFieldIds.contains(item)) {
            selectedFieldIds = selectedFieldIds.replace(item, "").replace(",,", ",").trim();
            if (selectedFieldIds.startsWith(",")) {
                selectedFieldIds = selectedFieldIds.substring(1);
            }
            if (selectedFieldIds.endsWith(",")) {
                selectedFieldIds = selectedFieldIds.substring(0, selectedFieldIds.length() - 1);
            }
        } else {
            if (!selectedFieldIds.isEmpty()) {
                selectedFieldIds += ",";
            }
            selectedFieldIds += item;
        }
    }

    public void toggleDurationSelection(String item) {
        if (selectedDurationId.contains(item)) {
            selectedDurationId = selectedDurationId.replace(item, "").replace(",,", ",").trim();
            if (selectedDurationId.startsWith(",")) {
                selectedDurationId = selectedDurationId.substring(1);
            }
            if (selectedDurationId.endsWith(",")) {
                selectedDurationId = selectedDurationId.substring(0, selectedDurationId.length() - 1);
            }
        } else {
            if (!selectedDurationId.isEmpty()) {
                selectedDurationId += ",";
            }
            selectedDurationId += item;
        }

        slotPatternAdapter.notifyDataSetChanged();
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

    private void getPromoDetail(boolean isLoader, int id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getPromoDetail(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            PromoCode promoCode = new Gson().fromJson(data.toString(), PromoCode.class);
                            for (int i=0; i<clubList.size(); i++ ){
                                if (Objects.equals(clubList.get(i).getId(), promoCode.getClub().getId())){
                                    oleClubNameAdapter.setSelectedIndex(i);
                                    selectedClubId = String.valueOf(clubList.get(i).getId());
                                    binding.switchAllClub.setChecked(true);
                                    break;
                                }
                            }
                            populatePromoData(promoCode);

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

    private void populatePromoData(PromoCode promoCode) {
        binding.title.setText("Update Promo");
        binding.tvBtn.setText("Update");
        binding.etName.setText(promoCode.getName());
        binding.etPromoCode.setText(promoCode.getCode());
        binding.etDate.setText(String.format("%s - %s", promoCode.getStartDate(), promoCode.getEndDate()));
        promoId = promoCode.getId();
        binding.etDiscount.setText(String.valueOf(promoCode.getDiscount()));
        if (promoCode.getDiscountType().equalsIgnoreCase("PERCENTAGE")){
            percentageClicked();
        }
        else{
            fixedClicked();
        }
        if (promoCode.getPromoType().equalsIgnoreCase("BIRTHDAY")){
            birdayClicked();
        }
        else if (promoCode.getPromoType().equalsIgnoreCase("NEW_USER")){
            newUserClicked();
//            binding.tvEligibleCount.setText(String.valueOf(promoCode.getTotalEligible()));
        }
        else{
            normalClicked();
        }
        if (promoCode.getTotalAllowedPlayers() == 0){
            allPlayerClicked();
        }
        else{
            specificClicked();
            binding.tvAssignCount.setText(String.valueOf(promoCode.getTotalAllowedPlayers()));
        }
        if (promoCode.getUsageLimit() == 0){
            unlimitedClicked();
        }
        else{
            limitedClicked();
        }
        promoType = promoCode.getPromoType();
        discountType = promoCode.getDiscountType();
        startDate = promoCode.getStartDate();
        endDate = promoCode.getEndDate();
        selectedClubIds = promoCode.getClub().getId();
        startDate =  formatDate(promoCode.getStartDate());
        endDate  = formatDate(promoCode.getEndDate());

        //  playerIds = promoCode.getAllowedPlayers();
        //  selectedDurationId = promoCode.getAllowedDurations();
        //  selectedFieldIds = promoCode.getAllowedField().get(0).getId();

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

}