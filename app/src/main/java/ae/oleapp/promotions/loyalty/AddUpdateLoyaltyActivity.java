package ae.oleapp.promotions.loyalty;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
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
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityAddUpdateLoyaltyBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.Loyalty;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import io.github.vejei.cupertinoswitch.CupertinoSwitch;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUpdateLoyaltyActivity extends BaseActivity implements View.OnClickListener {
    private ActivityAddUpdateLoyaltyBinding binding;
    private List<Club> clubList = new ArrayList<>();
    private UniversalAdapter oleClubNameAdapter;
    private String selectedClubIds = "";
    private String startDate = "", endDate = "";
    private String dateRange = "";
    public String selectedClubId = "";
    private int selectedIndex = 0;
    private String discountType = "";
    private boolean update = false;
    private int loyaltyId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUpdateLoyaltyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            update = bundle.getBoolean("update");
            loyaltyId = bundle.getInt("id");
            if (update){
                getLoyaltyDetails(false, loyaltyId);
            }
        }



        clubList.clear();
        clubList = AppManager.getInstance().clubs;
        LinearLayoutManager oleClubNameLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubNameRecyclerVu.setLayoutManager(oleClubNameLayoutManager);
        oleClubNameAdapter = new UniversalAdapter(getContext(), clubList);
        oleClubNameAdapter.setItemClickListener(clubNameClickListener);
        binding.clubNameRecyclerVu.setAdapter(oleClubNameAdapter);

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

        binding.btnBack.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.etDate.setOnClickListener(this);
        binding.percentageLay.setOnClickListener(this);
        binding.fixedLay.setOnClickListener(this);


    }

    UniversalAdapter.ItemClickListener clubNameClickListener = new UniversalAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Club club = clubList.get(pos);
            selectedIndex = pos;
            selectedClubId = club.getId();
            toggleSelection(selectedClubId);
            oleClubNameAdapter.selectClubs(club);


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

            if (binding.etTotalBooking.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Please enter total booking needed", FancyToast.INFO);
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

            if (binding.etDiscount.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Please enter discount amount", FancyToast.INFO);
                return;
            }

            if (discountType.isEmpty()){
                Functions.showToast(getContext(), "Please chose discount detail", FancyToast.INFO);
                return;
            }

            if (update){
                updateLoyaltyApi(binding.etTitle.getText().toString(), selectedClubIds, binding.etTotalBooking.getText().toString(), startDate, endDate, binding.etDiscount.getText().toString(), discountType);
            }
            else{
                addLoyalty(binding.etTitle.getText().toString(), selectedClubIds, binding.etTotalBooking.getText().toString(), startDate, endDate, binding.etDiscount.getText().toString(), discountType);
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

    private void addLoyalty(String title, String clubIds, String requiredPoints, String startDate, String endDate, String discount, String discountType) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.addLoyalty(title, clubIds, requiredPoints, startDate, endDate, discount, discountType);
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

    private void updateLoyaltyApi(String title, String clubIds, String requiredPoints, String startDate, String endDate, String discount, String discountType) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.updateLoyaltyApi(loyaltyId, title, clubIds, requiredPoints, startDate, endDate, discount, discountType);
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

    private void getLoyaltyDetails(boolean isLoader, int id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getLoyaltyDetails(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            Loyalty loyalty = new Gson().fromJson(data.toString(), Loyalty.class);
                            for (int i=0; i<clubList.size(); i++ ){
                                if (Objects.equals(clubList.get(i).getId(), loyalty.getClub().getId())){
                                    oleClubNameAdapter.setSelectedIndex(i);
                                    selectedClubId = String.valueOf(clubList.get(i).getId());
                                    binding.switchAllClub.setChecked(true);
                                    break;
                                }
                            }
                            populateData(loyalty);

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

    private void populateData(Loyalty loyalty) {
        binding.title.setText("Update Loyalty");
        binding.tvBtn.setText("Update");
        binding.etTitle.setText(loyalty.getTitle());
        binding.etTotalBooking.setText(String.valueOf(loyalty.getRequiredPoints()));
        binding.etDate.setText(String.format("%s - %s", loyalty.getStartDate(), loyalty.getEndDate()));
        startDate =  formatDate(loyalty.getStartDate());
        endDate  = formatDate(loyalty.getEndDate());
        loyaltyId = loyalty.getId();
        binding.etDiscount.setText(String.valueOf(loyalty.getDiscount()));
        if (loyalty.getDiscountType().equalsIgnoreCase("PERCENTAGE")){
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



}