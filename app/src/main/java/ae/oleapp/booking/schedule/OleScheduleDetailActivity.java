package ae.oleapp.booking.schedule;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.OleScheduleDetailAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.booking.BookingDetailsActivity;
import ae.oleapp.booking.bottomSheets.UpdateSchedulePriceDialogFragment;
import ae.oleapp.databinding.OleactivityScheduleDetailBinding;
import ae.oleapp.models.OleScheduleList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleScheduleDetailActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityScheduleDetailBinding binding;
    private final List<OleScheduleList> oleScheduleList = new ArrayList<>();
    private OleScheduleDetailAdapter adapter;
    private String clubId = "", userId = "", callBookingId = "";
    private boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityScheduleDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id");
            userId = bundle.getString("user_id");
            callBookingId = bundle.getString("call_booking_id");
        }

        binding.btnLayout.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new OleScheduleDetailAdapter(getContext(), oleScheduleList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.btnSelect.setOnClickListener(this);
        binding.btnUpdateDetails.setOnClickListener(this);
        binding.btnRemoveDetails.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
    }

    OleScheduleDetailAdapter.ItemClickListener itemClickListener = new OleScheduleDetailAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            if (isUpdate) {
                adapter.setSelection(oleScheduleList.get(pos));
                binding.tvSelected.setText(String.format("Selected (%s)", adapter.getSelectedList().size()));
            }
            else {
                Intent intent = new Intent(getContext(), BookingDetailsActivity.class);
                intent.putExtra("booking_id", oleScheduleList.get(pos).getId());
                startActivity(intent);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getScheduleDetailAPI(oleScheduleList.isEmpty(), clubId, callBookingId, userId);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack) {
            finish();
        }
        else if (v == binding.btnSelect) {
            updateClicked();
        }
        else if (v == binding.btnUpdateDetails) {
            updateDetailsClicked();
        }
        else if (v == binding.btnRemoveDetails) {
            removeDetailsClicked();
        }
    }

    private void updateClicked() {
        if (isUpdate) {
            isUpdate = false;
            binding.btnLayout.setVisibility(View.GONE);
            binding.btnSelect.setText(R.string.select);
        }
        else {
            isUpdate = true;
            binding.btnLayout.setVisibility(View.VISIBLE);
            binding.btnSelect.setText(R.string.unselect);
        }
        adapter.setUpdate(isUpdate);
    }

    private void updateDetailsClicked() {
        if (adapter.getSelectedList().size() == 0) {
            return;
        }
        String ids = "";
        for (OleScheduleList data : adapter.getSelectedList()) {
            if (ids.isEmpty()) {
                ids = String.valueOf(data.getId());
            }
            else {
                ids = String.format("%s,%s", ids, data.getId());
            }
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("UpdateSchedulePriceDialogFragment");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        UpdateSchedulePriceDialogFragment dialogFragment = new UpdateSchedulePriceDialogFragment(adapter.getSelectedList().get(0), ids);
        dialogFragment.setFragmentCallback(new UpdateSchedulePriceDialogFragment.SchedulePriceDialogFragmentCallback() {
            @Override
            public void didUpdatePrice() {
                isUpdate = false;
                binding.btnLayout.setVisibility(View.GONE);
                binding.btnSelect.setText(R.string.select);
                getScheduleDetailAPI(true, clubId, callBookingId, userId);
            }
        });
        dialogFragment.show(fragmentTransaction, "UpdateSchedulePriceDialogFragment");
    }

    private void removeDetailsClicked() {
        if (adapter.getSelectedList().size() == 0) {
            return;
        }
        String ids = "";
        for (OleScheduleList data : adapter.getSelectedList()) {
            if (ids.isEmpty()) {
                ids = String.valueOf(data.getId());
            }
            else {
                ids = String.format("%s,%s", ids, data.getId());
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String finalIds = ids;
        builder.setTitle(getString(R.string.remove))
                .setMessage(getString(R.string.remove_schedule_booking))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeSelectedSchedule(true, finalIds);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        builder.show();

    }

    private void removeSelectedSchedule(boolean isLoader, String ids) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.removeSelectedSchedule(clubId, ids);
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

    private void getScheduleDetailAPI(boolean isLoader, String clubId, String callBookingId, String userId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getScheduleDetailAPI(clubId, callBookingId, userId);
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
                            adapter.setUpdate(false);
                            adapter.notifyDataSetChanged();
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
