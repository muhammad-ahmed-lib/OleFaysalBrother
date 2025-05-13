package ae.oleapp.booking.schedule;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baoyz.actionsheet.ActionSheet;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleClubNameAdapter;
import ae.oleapp.adapters.OleScheduleListAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityScheduleListBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleScheduleList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleScheduleListActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityScheduleListBinding binding;
    private final List<OleScheduleList> oleScheduleList = new ArrayList<>();
    private OleScheduleListAdapter adapter;
    private String clubId = "";
    private final String selectedClubId = "";
    private int index = 0;
    private final List<Club> clubList = new ArrayList<>();
    private OleClubNameAdapter oleClubNameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityScheduleListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id","");
        }

        for (int i =0; i<AppManager.getInstance().clubs.size(); i++){
            if (clubId.equalsIgnoreCase(AppManager.getInstance().clubs.get(i).getId())){
                index = i;
            }
        }

        LinearLayoutManager oleClubNameLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(oleClubNameLayoutManager);
        oleClubNameAdapter = new OleClubNameAdapter(getContext(), AppManager.getInstance().clubs);
        oleClubNameAdapter.setItemClickListener(clubNameClickListener);
        oleClubNameAdapter.setSelectedIndex(index);
        binding.clubRecyclerVu.setAdapter(oleClubNameAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleScheduleListAdapter(getContext(), oleScheduleList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.btnBack.setOnClickListener(this);
    }

    OleClubNameAdapter.ItemClickListener clubNameClickListener = new OleClubNameAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            oleClubNameAdapter.setSelectedIndex(pos);
            clubId = AppManager.getInstance().clubs.get(pos).getId();
            getScheduleListAPI(true, clubId);
        }
    };

    OleScheduleListAdapter.ItemClickListener itemClickListener = new OleScheduleListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            OleScheduleList obj = oleScheduleList.get(pos);
            Intent intent = new Intent(getContext(), OleScheduleDetailActivity.class);
            intent.putExtra("club_id", obj.getClub().getId());
            intent.putExtra("user_id", obj.getUserId());
            intent.putExtra("call_booking_id", String.valueOf(obj.getCallBookingId()));
            startActivity(intent);
        }

        @Override
        public void delClicked(View view, int pos) {
            deleteSchedule(pos);
        }

        @Override
        public void callClicked(View view, int pos) {
            makeCall(oleScheduleList.get(pos).getUser().getPhone());
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!clubId.isEmpty()){
            getScheduleListAPI(oleScheduleList.isEmpty(), clubId);
        }
    }

    private void deleteSchedule(int pos) {
        ActionSheet.createBuilder(getContext(), getSupportFragmentManager())
                .setCancelButtonTitle(getResources().getString(R.string.cancel))
                .setOtherButtonTitles(getResources().getString(R.string.delete))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        if (index == 0) {
                            deleteScheduleAPI(true, clubId,  String.valueOf(oleScheduleList.get(pos).getCallBookingId()), oleScheduleList.get(pos).getScheduleStartDate(), oleScheduleList.get(pos).getScheduleEndDate(), pos);
                        }
                    }
                }).show();
    }

    private void getScheduleListAPI(boolean isLoader, String clubId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getScheduleList(clubId, "", "","");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            oleScheduleList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                OleScheduleList schedule = gson.fromJson(arr.get(i).toString(), OleScheduleList.class);
                                oleScheduleList.add(schedule);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            oleScheduleList.clear();
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

    private void deleteScheduleAPI(boolean isLoader, String clubId, String ids, String startDate, String endDate, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.deleteSchedule(clubId, ids, startDate, endDate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            oleScheduleList.remove(pos);
                            adapter.notifyItemRemoved(pos);
                            adapter.notifyItemRangeChanged(pos, oleScheduleList.size());
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

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
    }
}
