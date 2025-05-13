package ae.oleapp.promotions.fieldOffer;

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
import ae.oleapp.adapters.FieldOfferAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityExpiredFieldOffersBinding;
import ae.oleapp.models.FieldOffer;
import ae.oleapp.promotions.loyalty.LoyaltyDetailActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpiredFieldOffersActivity extends BaseActivity implements View.OnClickListener {
    private ActivityExpiredFieldOffersBinding binding;
    private final List<FieldOffer> fieldOfferList = new ArrayList<>();
    private FieldOfferAdapter fieldOfferAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpiredFieldOffersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        LinearLayoutManager offerLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(offerLayoutManager);
        fieldOfferAdapter = new FieldOfferAdapter(getContext(), fieldOfferList);
        fieldOfferAdapter.setItemClickListener(offerClickListener);
        binding.recyclerVu.setAdapter(fieldOfferAdapter);

        binding.btnBack.setOnClickListener(this);

    }

    FieldOfferAdapter.ItemClickListener offerClickListener = new FieldOfferAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), FieldOfferDetailsActivity.class);
            intent.putExtra("offer", new Gson().toJson(fieldOfferList.get(pos)));
            startActivity(intent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getFieldOffers(fieldOfferList.isEmpty());
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }

    }

    private void getFieldOffers(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getFieldOffers();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            fieldOfferList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                FieldOffer offer = gson.fromJson(arr.get(i).toString(), FieldOffer.class);
                                if (offer.getStatus().equalsIgnoreCase("Expired")){
                                    fieldOfferList.add(offer);
                                }
                            }
                            fieldOfferAdapter.notifyDataSetChanged();
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