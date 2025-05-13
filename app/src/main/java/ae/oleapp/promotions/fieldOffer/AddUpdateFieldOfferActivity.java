package ae.oleapp.promotions.fieldOffer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import ae.oleapp.R;
import ae.oleapp.adapters.UniversalAdapter;
import ae.oleapp.adapters.UniversalDayAdapter;
import ae.oleapp.adapters.UniversalFieldAdapter;
import ae.oleapp.adapters.UniversalSlotPatternAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddUpdateFieldOfferBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.Days;
import ae.oleapp.models.FieldOffer;
import ae.oleapp.models.GeneralField;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.github.vejei.cupertinoswitch.CupertinoSwitch;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUpdateFieldOfferActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddUpdateFieldOfferBinding binding;
    private List<Club> clubList = new ArrayList<>();
    private String selectedClubIds = "", selectedDaysId = "";
    private String startDate = "", startTime = "", endDate = "", endTime = "";
    private String dateRange = "";
    public String selectedClubId = "";
    public String selectedDayId = "";
    private int selectedIndex = 0, selectedFieldIndex = 0, selectedDurationIndex = 0;
    private int selectedDayIndex = 0;
    private String discountType = "";
    private boolean update = false;
    private int offerId;
    private final List<Days> daysList = new ArrayList<>();
    private UniversalDayAdapter daysAdapter;
    private int autoMsg;

    private final List<GeneralField> fieldList = new ArrayList<>();
    private final List<OleKeyValuePair> durationList = new ArrayList<>();
    private UniversalAdapter oleClubNameAdapter;
    private UniversalFieldAdapter fieldAdapter;
    private UniversalSlotPatternAdapter slotPatternAdapter;

    private String selectedFieldId = "";
    private String selectedDurationId = "";
    private  String selectedFieldIds = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUpdateFieldOfferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getDays(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            update = bundle.getBoolean("update");
            offerId = bundle.getInt("id");
            if (update){
                binding.clubHead.setVisibility(View.GONE);
                getOfferDetails(false, offerId);
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

        LinearLayoutManager oleDaysLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.daysRecyclerVu.setLayoutManager(oleDaysLayoutManager);
        daysAdapter = new UniversalDayAdapter(getContext(), daysList);
        daysAdapter.setItemClickListener(daysClickListener);
        binding.daysRecyclerVu.setAdapter(daysAdapter);

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

        binding.everydaySwitch.setOnStateChangeListener(new CupertinoSwitch.OnStateChangeListener() {
            @Override
            public void onChanged(CupertinoSwitch view, boolean checked) {
                for (Days day: daysList){
                    toggleSelectionDay(String.valueOf(day.getId()));
                    daysAdapter.selectClubs(day);
                }
            }

            @Override
            public void onSwitchOn(CupertinoSwitch view) {
            }

            @Override
            public void onSwitchOff(CupertinoSwitch view) {

            }
        });

        binding.switchAutoMsg.setOnStateChangeListener(new CupertinoSwitch.OnStateChangeListener() {
            @Override
            public void onChanged(CupertinoSwitch view, boolean checked) {

            }

            @Override
            public void onSwitchOn(CupertinoSwitch view) {
                autoMsg = 1;
            }

            @Override
            public void onSwitchOff(CupertinoSwitch view) {
                autoMsg = 0;
            }
        });

        binding.btnBack.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.etDate.setOnClickListener(this);
        binding.percentageLay.setOnClickListener(this);
        binding.fixedLay.setOnClickListener(this);
        binding.etTime.setOnClickListener(this);

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
            populateDuration(false);

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

    UniversalDayAdapter.ItemClickListener daysClickListener = new UniversalDayAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Days days = daysList.get(pos);
            selectedDayIndex = pos;
            selectedDayId = String.valueOf(days.getId());
            toggleSelectionDay(selectedDayId);
            daysAdapter.selectClubs(days);

        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.btnAdd) {
            if (binding.etTitle.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Please write title", FancyToast.INFO);
                return;
            }

            // if (binding.etTotalBooking.getText().toString().isEmpty()){
            //      Functions.showToast(getContext(), "Please enter total booking needed", FancyToast.INFO);
            //      return;
            //    }

            if (startDate.isEmpty() || endDate.isEmpty()){
                Functions.showToast(getContext(), "Please enter date", FancyToast.INFO);
                return;
            }

            if (selectedClubIds.isEmpty()){
                Functions.showToast(getContext(), "Please chose club", FancyToast.INFO);
                return;
            }

            if (binding.etDiscount.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Please enter discount amount", FancyToast.INFO);
                return;
            }

            if (discountType.isEmpty()){
                Functions.showToast(getContext(), "Please chose discount detail", FancyToast.INFO);
            }

            if (update){
                updateFieldOfferApi(binding.etTitle.getText().toString(),binding.etDiscount.getText().toString(),discountType,startDate,endDate,startTime,endTime,selectedClubIds,"",selectedDaysId,"0");
            }
            else{
                addFieldOffer(binding.etTitle.getText().toString(),binding.etDiscount.getText().toString(),discountType,startDate,endDate,startTime,endTime,selectedClubIds,"",selectedDaysId,0);
            }

        }
        else if (v == binding.percentageLay) {
            percentageClicked();

        }
        else if (v == binding.fixedLay) {
            fixedClicked();

        }
        else if (v == binding.etDate) {
            showDateRangePicker();
        }
        else if (v == binding.etTime) {
            showTimePickerDialogs();
        }

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
    }

    public void toggleSelectionDay(String item) {
        if (selectedDaysId.contains(item)) {
            selectedDaysId = selectedDaysId.replace(item, "").replace(",,", ",").trim();
            if (selectedDaysId.startsWith(",")) {
                selectedDaysId = selectedDaysId.substring(1);
            }
            if (selectedDaysId.endsWith(",")) {
                selectedDaysId = selectedDaysId.substring(0, selectedDaysId.length() - 1);
            }
        } else {
            if (!selectedDaysId.isEmpty()) {
                selectedDaysId += ",";
            }
            selectedDaysId += item;
        }
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

    private void populateData(FieldOffer offer) {
        binding.title.setText("Update Offer");
        binding.tvBtn.setText("Update");
        binding.etTitle.setText(offer.getTitle());
        binding.etTime.setText(String.format("%s - %s", offer.getStartTime(), offer.getEndTime()));
        binding.etDate.setText(String.format("%s - %s", offer.getStartDate(), offer.getEndDate()));
        startDate =  formatDate(offer.getStartDate());
        endDate  = formatDate(offer.getEndDate());
        offerId = offer.getId();
        binding.etDiscount.setText(String.valueOf(offer.getDiscount()));
        if (offer.getDiscountType().equalsIgnoreCase("PERCENTAGE")){
            percentageClicked();
        }
        else{
            fixedClicked();
        }
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

    private void addFieldOffer(String title, String discount, String discountType, String startDate, String endDate, String startTime, String endTime, String clubIds, String fieldIds, String DayIds, int sendSms) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.addFieldOffer(title, discount, discountType, startDate, endDate, startTime, endTime,  clubIds, fieldIds, DayIds, sendSms);
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

            }
        });
    }

    private void updateFieldOfferApi(String title, String discount, String discountType, String startDate, String endDate, String startTime, String endTime, String clubIds, String fieldIds, String DayIds, String sendSms) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.updateFieldOfferApi(offerId, title, discount, discountType, startDate, endDate, startTime, endTime, DayIds);
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

            }
        });
    }

    private void getOfferDetails(boolean isLoader, int id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getOfferDetails(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            FieldOffer offer = new Gson().fromJson(data.toString(), FieldOffer.class);
                            for (int i=0; i<clubList.size(); i++ ){
                                if (Objects.equals(clubList.get(i).getId(), offer.getClub().getId())){
                                    oleClubNameAdapter.setSelectedIndex(i);
                                    selectedClubId = String.valueOf(clubList.get(i).getId());
                                    toggleSelection(selectedClubId);
                                    oleClubNameAdapter.selectClubs(clubList.get(i));
                                    break;
                                }
                            }
                            for (int i=0; i < offer.getDays().size(); i++){
                                for (int j = 0; j < daysList.size(); j++) {
                                    if (offer.getDays().get(j).getDayId().equalsIgnoreCase(String.valueOf(daysList.get(i).getId()))) {
                                        selectedDayId = String.valueOf(daysList.get(i).getId());
                                        toggleSelectionDay(selectedDayId);
                                        daysAdapter.selectClubs(daysList.get(i));
                                        daysAdapter.setSelectedIndex(i);
                                        break;
                                    }
                                }
                            }
                            populateData(offer);

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

    private void getDays(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getDays();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            daysList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                Days day = gson.fromJson(arr.get(i).toString(), Days.class);
                                daysList.add(day);
                            }
                            daysAdapter.notifyDataSetChanged();
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

    private void showTimePickerDialogs() {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                startTime = formatter.format(calendar.getTime());

                com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(new com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                        endTime = formatter.format(calendar.getTime());

                        binding.etTime.setText(startTime + " - " + endTime);
                    }
                }, hour, minute, false);
                timePickerDialog.enableSeconds(false);
                timePickerDialog.setTimeInterval(1, 30);
                timePickerDialog.show(getSupportFragmentManager(), "timePickerDialog");
            }
        }, hour, minute, false);
        timePickerDialog.enableSeconds(false);
        timePickerDialog.setTimeInterval(1, 30);
        timePickerDialog.show(getSupportFragmentManager(), "timePickerDialog");
    }



}