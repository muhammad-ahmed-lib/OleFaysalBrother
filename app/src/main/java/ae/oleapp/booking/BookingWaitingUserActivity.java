package ae.oleapp.booking;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.WaitingUserAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityBookingWaitingUserBinding;
import ae.oleapp.databinding.FragmentAddWaitingUserDialogBinding;
import ae.oleapp.fragments.AddWaitingUserDialogFragment;
import ae.oleapp.models.BasicBookingDetail;
import ae.oleapp.models.BookingSlot;
import ae.oleapp.models.BookingWaitingList;
import ae.oleapp.models.Club;
import ae.oleapp.models.CountryPhoneList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingWaitingUserActivity extends BaseActivity implements View.OnClickListener {

    private ActivityBookingWaitingUserBinding binding;
    private String bookingId = "";
    private String countryId = "";
    private String clubId = "";
    private String fieldId = "";
    private String date = "";
    private String startTime = "";
    private String endTime = "";
    private String bookingStatus = "";
    private List<CountryPhoneList> countryList = new ArrayList<>();
    private BookingSlot bookingSlot;
    private WaitingUserAdapter adapter;
    private final List<BookingWaitingList> bookingWaitingLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingWaitingUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            bookingId = bundle.getString("booking_id","");
            date = bundle.getString("date","");
            startTime = bundle.getString("start","");
            endTime = bundle.getString("end","");
            clubId = bundle.getString("club_id","");
            fieldId = bundle.getString("field_id","");
            bookingStatus = bundle.getString("booking_status","");
        }

        getWaitingUserList();

        LinearLayoutManager slotLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(slotLayoutManager);
        adapter = new WaitingUserAdapter(getContext(), bookingWaitingLists, bookingStatus);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        countryList = AppManager.getInstance().countryPhoneLists;
        countryId =  FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());

        binding.ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryId =  FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());
            }
        });

        binding.btnBack.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
    }


    WaitingUserAdapter.ItemClickListener itemClickListener = new WaitingUserAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

        }
        @Override
        public void phoneClicked(View view, int pos) {
//            makeCall(bookingWaitingLists.get(pos).getUserPhone());
        }
        @Override
        public void bookingClicked(View view, int pos) {
//            dialogCallback.didSubmitResult(this,"booking","", bookingWaitingLists.get(pos).getUserName(), bookingWaitingLists.get(pos).getUserPhone());
        }
        @Override
        public void deleteClicked(View view, int pos) {
//            removeWaitingUser(pos);
        }
    };

    protected String FindCountryId(String selectedCountryCode){
        for (int i = 0; i < AppManager.getInstance().countryPhoneLists.size(); i++) {
            if (selectedCountryCode.equalsIgnoreCase(AppManager.getInstance().countryPhoneLists.get(i).getCode())) {
                countryId = String.valueOf(AppManager.getInstance().countryPhoneLists.get(i).getId());
                break;
            }
        }
        return countryId;
    }

    @Override
    public void onClick(View v) {

        if (v == binding.btnBack){
           finish();
        }
        else if (v == binding.btnAdd){
            if (binding.etName.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Name cannot be empty!", FancyToast.ERROR);
                return;
            }
            if (binding.etPhone.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Phone number cannot be empty!", FancyToast.ERROR);
                return;
            }
            addUserToWaitingList(bookingId, clubId, binding.etName.getText().toString(), binding.etPhone.getText().toString());
        }

    }

    private void addUserToWaitingList(String bookingId, String clubId, String name, String phone) { //fieldID is empty, and countryId is not okay
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.addUserToWaitingList(bookingId,clubId, name, phone, countryId, fieldId, date, startTime, endTime);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), getString(R.string.success), FancyToast.SUCCESS);
//                            getSlotsAPI(selectedDate);
//                            dialogCallback.didSubmitResult(this, );

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

    private void getWaitingUserList() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getWaitingUserList(clubId, fieldId, date, startTime, endTime);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            bookingWaitingLists.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                bookingWaitingLists.add(gson.fromJson(arr.get(i).toString(), BookingWaitingList.class));
                            }
                            adapter.notifyDataSetChanged();
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