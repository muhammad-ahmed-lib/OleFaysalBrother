package ae.oleapp.promotions.loyalty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.LoyaltyAdapter;
import ae.oleapp.adapters.OleClubNameAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityLoyaltyCardBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.Loyalty;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveLoyaltyCardActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLoyaltyCardBinding binding;
    private final List<Club> clubList = new ArrayList<>();
    private OleClubNameAdapter oleClubNameAdapter;
    public String selectedClubId = "";
    private int selectedIndex = 0;
    private final List<Loyalty> loyaltyList = new ArrayList<>();
    private LoyaltyAdapter loyaltyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoyaltyCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Club club = new Club();
        club.setId("");
        club.setName(getString(R.string.all));
        clubList.add(club);
        clubList.addAll(AppManager.getInstance().clubs);

        LinearLayoutManager oleClubNameLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubNameRecyclerVu.setLayoutManager(oleClubNameLayoutManager);
        oleClubNameAdapter = new OleClubNameAdapter(getContext(), clubList);
        oleClubNameAdapter.setItemClickListener(clubNameClickListener);
        binding.clubNameRecyclerVu.setAdapter(oleClubNameAdapter);

        LinearLayoutManager loyaltyLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(loyaltyLayoutManager);
        loyaltyAdapter = new LoyaltyAdapter(getContext(), loyaltyList);
        loyaltyAdapter.setItemClickListener(loyaltyClickListener);
        binding.recyclerVu.setAdapter(loyaltyAdapter);

        binding.btnBack.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.relExpired.setOnClickListener(this);

    }

    OleClubNameAdapter.ItemClickListener clubNameClickListener = new OleClubNameAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            selectedIndex = pos;
            selectedClubId = String.valueOf(clubList.get(selectedIndex).getId()); //check this
            oleClubNameAdapter.setSelectedIndex(selectedIndex);
            // oleClubNameAdapter.setSelectedId(selectedClubId);
            // getOwnerClubDetail(false, Integer.parseInt(selectedClubId), pos);
            // populateClubData(selectedIndex);
        }
    };

    LoyaltyAdapter.ItemClickListener loyaltyClickListener = new LoyaltyAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), LoyaltyDetailActivity.class);
            intent.putExtra("loyalty", new Gson().toJson(loyaltyList.get(pos)));
            startActivity(intent);
        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        getloyalties(loyaltyList.isEmpty());
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.btnAdd) {
            Intent intent = new Intent(getContext(), AddUpdateLoyaltyActivity.class);
            intent.putExtra("update", false);
            startActivity(intent);
        }
        else if (v == binding.relExpired) {
            Intent intent = new Intent(getContext(), ExpiredLoyaltyActivity.class);
            startActivity(intent);
        }
    }


    private void getloyalties(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getloyalties();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            loyaltyList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                Loyalty loyalty = gson.fromJson(arr.get(i).toString(), Loyalty.class);
                                if (loyalty.getStatus().equalsIgnoreCase("Active")){
                                    loyaltyList.add(loyalty);
                                }
                            }

                            loyaltyAdapter.notifyDataSetChanged();

                            int totalExpired = object.getInt("expired_count");
                            binding.tvTotalExpired.setText(String.format("(%s %s)", "Total: ", totalExpired));
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