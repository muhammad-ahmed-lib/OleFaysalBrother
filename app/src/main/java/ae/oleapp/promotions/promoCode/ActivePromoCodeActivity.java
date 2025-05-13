package ae.oleapp.promotions.promoCode;

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
import ae.oleapp.adapters.OleClubNameAdapter;
import ae.oleapp.adapters.PromoCodeAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityActivePromotionsBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.PromoCode;
import ae.oleapp.promotions.fieldOffer.ExpiredFieldOffersActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivePromoCodeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityActivePromotionsBinding binding;
    private final List<Club> clubList = new ArrayList<>();
    private final List<PromoCode> promoCodeList = new ArrayList<>();
    private OleClubNameAdapter oleClubNameAdapter;
    private PromoCodeAdapter promoCodeAdapter;
    public String selectedClubId = "";
    private int selectedIndex = 0;
    private String clubId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityActivePromotionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id","");
        }

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

        LinearLayoutManager promoLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(promoLayoutManager);
        promoCodeAdapter = new PromoCodeAdapter(getContext(), promoCodeList);
        promoCodeAdapter.setItemClickListener(promoClickListener);
        binding.recyclerVu.setAdapter(promoCodeAdapter);

        binding.btnBack.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
        binding.relExpired.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getPromoCode(promoCodeList.isEmpty(), clubId, "", "");
    }

    OleClubNameAdapter.ItemClickListener clubNameClickListener = new OleClubNameAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            selectedIndex = pos;
            selectedClubId = clubList.get(selectedIndex).getId(); //check this
            oleClubNameAdapter.setSelectedIndex(selectedIndex);
            getPromoCode(false, selectedClubId, "", "");
//            oleClubNameAdapter.setSelectedId(selectedClubId);
//            getOwnerClubDetail(false, Integer.parseInt(selectedClubId), pos);
//            populateClubData(selectedIndex);
        }
    };

    PromoCodeAdapter.ItemClickListener promoClickListener = new PromoCodeAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), PromoDetailsActivity.class);
            intent.putExtra("id", promoCodeList.get(pos).getId());
            startActivity(intent);
//            selectedIndex = pos;
//            selectedClubId = String.valueOf(clubList.get(selectedIndex).getId()); //check this
//            oleClubNameAdapter.setSelectedIndex(selectedIndex);
//            oleClubNameAdapter.setSelectedId(selectedClubId);
//            getOwnerClubDetail(false, Integer.parseInt(selectedClubId), pos);
//            populateClubData(selectedIndex);
        }

        @Override
        public void discountClicked(View view, int pos) {

        }

        @Override
        public void appliedClicked(View view, int pos) {

        }

        @Override
        public void eligibleClicked(View view, int pos) {

        }
    };


    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.btnAdd) {
            Intent intent = new Intent(getContext(), AddUpdatePromoCodeActivity.class);
            intent.putExtra("is_update", false);
            startActivity(intent);
        }
        else if (v == binding.relExpired) {
            Intent intent = new Intent(getContext(), ExpiredPromoCodeActivity.class);
            startActivity(intent);
        }
    }

    private void getPromoCode(boolean isLoader, String clubId, String startDate, String endDate) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getPromoCode(clubId, startDate, endDate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            promoCodeList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                PromoCode promo = gson.fromJson(arr.get(i).toString(), PromoCode.class);
                                if (promo.getStatus().equalsIgnoreCase("Active")){
                                    promoCodeList.add(promo);
                                }
                            }
                            promoCodeAdapter.notifyDataSetChanged();

                            int totalExpired = object.getInt("total_expired");
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