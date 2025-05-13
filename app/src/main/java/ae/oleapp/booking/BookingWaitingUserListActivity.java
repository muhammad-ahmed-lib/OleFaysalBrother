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
import ae.oleapp.databinding.ActivityBookingWaitingUserListBinding;
import ae.oleapp.models.BookingSlot;
import ae.oleapp.models.BookingWaitingList;
import ae.oleapp.models.CountryPhoneList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingWaitingUserListActivity extends BaseActivity implements View.OnClickListener {

    private ActivityBookingWaitingUserListBinding binding;
    private String bookingId = "";
    private final String countryId = "";
    private String clubId = "";
    private String fieldId = "";
    private String date = "";
    private String startTime = "";
    private String endTime = "";
    private String bookingStatus = "";
    private WaitingUserAdapter adapter;
    private final List<BookingWaitingList> bookingWaitingLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingWaitingUserListBinding.inflate(getLayoutInflater());
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

        binding.btnBack.setOnClickListener(this);
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


    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
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