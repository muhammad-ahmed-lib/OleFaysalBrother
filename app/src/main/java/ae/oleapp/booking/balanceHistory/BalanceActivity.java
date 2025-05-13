package ae.oleapp.booking.balanceHistory;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.BalanceAdapter;
import ae.oleapp.adapters.HirePlayerAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.booking.bottomSheets.BalanceAddedByBottomSheetFragment;
import ae.oleapp.booking.bottomSheets.BalanceDetailBottomSheetFragment;
import ae.oleapp.booking.bottomSheets.CancelBookingSheetFragment;
import ae.oleapp.databinding.ActivityBalanceBinding;
import ae.oleapp.models.AddedBy;
import ae.oleapp.models.Balance;
import ae.oleapp.models.Collection;
import ae.oleapp.models.PlayerSkill;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BalanceActivity extends BaseActivity implements View.OnClickListener {

    private ActivityBalanceBinding binding;
    private final List<Balance> pendindList =  new ArrayList<>();
    private final List<Balance> paidList =  new ArrayList<>();
    private BalanceAdapter adapter;
    private String clubId = "", id = "", type = "", finalCurrency = "";
    private boolean isPaid = false;
    private int finalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBalanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            clubId = bundle.getString("club_id");
            id = bundle.getString("id");
            type = bundle.getString("type");
            if (type.equalsIgnoreCase("call")){
                getBalanceHistory(clubId, "", String.valueOf(id));
            }
            else{
                getBalanceHistory(clubId, String.valueOf(id), "");
            }
        }

        LinearLayoutManager balanceLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(balanceLayoutManager);
        adapter = new BalanceAdapter(getContext(), pendindList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);


        binding.btnBack.setOnClickListener(this);
        binding.pendingVu.setOnClickListener(this);
        binding.paidVu.setOnClickListener(this);
        binding.btnPayAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.pendingVu) {
            pendingClicked();
        }
        else if (v == binding.paidVu) {
            paidClicked();
        }
        else if (v == binding.btnPayAll) {
            if (finalPrice != 0){
                payAllClicked();
            }
        }

    }

    private void payAllClicked() {
        Intent intent = new Intent(getContext(), PayBalanceActivity.class);
        intent.putExtra("club_id", clubId);
        intent.putExtra("price", String.valueOf(finalPrice));
        intent.putExtra("curr", finalCurrency);
        intent.putExtra("payall", true);
        intent.putExtra("type", type);
        intent.putExtra("id", String.valueOf(id));
        payBalanceResultLauncher.launch(intent);
    }

    private void pendingClicked() {
        binding.bottomContainer.setVisibility(View.VISIBLE);
        binding.pendingVu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.pendingVu.setCardBackgroundColor(Color.parseColor("#33000000"));
        binding.paidVu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.club_selection_color));
        binding.paidVu.setCardBackgroundColor(Color.TRANSPARENT);
        isPaid = false;
        adapter.setDataSource(pendindList);
        binding.tvFinalPrice.setText(String.valueOf(finalPrice));
        binding.tvFinalCurrency.setText(finalCurrency);
    }

    private void paidClicked() {
        binding.bottomContainer.setVisibility(View.GONE);
        binding.paidVu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.paidVu.setCardBackgroundColor(Color.parseColor("#33000000"));
        binding.pendingVu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.club_selection_color));
        binding.pendingVu.setCardBackgroundColor(Color.TRANSPARENT);
        isPaid = true;
        adapter.setDataSource(paidList);
    }

    private void getBalanceHistory(String clubId, String userId, String callUserId) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getBalanceHistory(clubId, userId, callUserId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            finalCurrency = object.getString("currency");
                            finalPrice = object.getInt("total_amount");
                            Gson gson = new Gson();
                            pendindList.clear();
                            paidList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                Balance balance = gson.fromJson(arr.get(i).toString(), Balance.class);
                                if (!balance.getIsCleared()){
                                    pendindList.add(balance);
                                }
                                else{
                                    paidList.add(balance);
                                }
                            }
                            if (!isPaid) {
                                pendingClicked();
                            }
                            else {
                                paidClicked();
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            pendindList.clear();
                            paidList.clear();
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

    BalanceAdapter.ItemClickListener itemClickListener = new BalanceAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

        }

        @Override
        public void payClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), PayBalanceActivity.class);
            intent.putExtra("balance", new Gson().toJson(pendindList.get(pos)));
            intent.putExtra("club_id", clubId);
            intent.putExtra("price", String.valueOf(finalPrice));
            intent.putExtra("curr", finalCurrency);
            intent.putExtra("payall", false);
            intent.putExtra("type", type);
            intent.putExtra("id", String.valueOf(id));
            payBalanceResultLauncher.launch(intent);
        }

        @Override
        public void infoClicked(View view, int pos, boolean isPending) {
            if (!isPending){
                showBalanceReceivedDetails(paidList.get(pos).getCollections());
            }
            else{
                showBalanceAddedByDetails(pendindList.get(pos).getAddedBy());
            }

        }
    };

    protected void showBalanceReceivedDetails(List<Collection> paidList) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("BalanceDetailBottomSheetFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        BalanceDetailBottomSheetFragment dialogFragment = new BalanceDetailBottomSheetFragment(paidList);
        dialogFragment.setDialogCallback((df) -> {
            df.dismiss();

        });
        dialogFragment.show(fragmentTransaction, "BalanceDetailBottomSheetFragment");
    }

    protected void showBalanceAddedByDetails(AddedBy addedBy) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("BalanceAddedByBottomSheetFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        BalanceAddedByBottomSheetFragment dialogFragment = new BalanceAddedByBottomSheetFragment(addedBy);
        dialogFragment.setDialogCallback((df) -> {
            df.dismiss();

        });
        dialogFragment.show(fragmentTransaction, "BalanceAddedByBottomSheetFragment");
    }

    ActivityResultLauncher<Intent> payBalanceResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (type.equalsIgnoreCase("call")){
                    getBalanceHistory(clubId, "", String.valueOf(id));
                }
                else{
                    getBalanceHistory(clubId, String.valueOf(id), "");
                }
            }
        }
    });



}