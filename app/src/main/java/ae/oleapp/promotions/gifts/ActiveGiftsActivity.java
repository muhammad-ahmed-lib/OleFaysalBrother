package ae.oleapp.promotions.gifts;

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
import ae.oleapp.adapters.GiftAdapter;
import ae.oleapp.adapters.OleClubNameAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityActiveGiftsBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.Gift;
import ae.oleapp.models.PromoCode;
import ae.oleapp.models.PromoCount;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveGiftsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityActiveGiftsBinding binding;
    private final List<Club> clubList = new ArrayList<>();
    private OleClubNameAdapter oleClubNameAdapter;
    public String selectedClubId = "";
    private int selectedIndex = 0;
    private final List<Gift> giftsList = new ArrayList<>();
    private GiftAdapter giftAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActiveGiftsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

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

        LinearLayoutManager giftsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(giftsLayoutManager);
        giftAdapter = new GiftAdapter(getContext(), giftsList);
        giftAdapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(giftAdapter);



//        getOwnerClubList(clubList.isEmpty(), 0.0,0.0,"");


        binding.btnBack.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.relExpired.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getGifts(giftsList.isEmpty());
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

    GiftAdapter.ItemClickListener itemClickListener = new GiftAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            Intent intent = new Intent(getContext(), GiftDetailActivity.class);
            intent.putExtra("gift", new Gson().toJson(giftsList.get(pos)));
            startActivity(intent);

        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.btnAdd) {
            Intent intent = new Intent(getContext(), AddUpdateGiftActivity.class);
            intent.putExtra("update", false);
            startActivity(intent);
        }
        else if (v == binding.relExpired) {
            Intent intent = new Intent(getContext(), ExpiredGiftsActivity.class);
            startActivity(intent);
        }
    }

    private void getGifts(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getGifts();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            giftsList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                Gift gift = gson.fromJson(arr.get(i).toString(), Gift.class);
                                if (gift.getStatus().equalsIgnoreCase("Active")){
                                    giftsList.add(gift);
                                }
                            }
                            giftAdapter.notifyDataSetChanged();

                            int totalExpired = object.getInt("expired_gifts");
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