package ae.oleapp.promotions.loyalty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityExpiredLoyaltyBinding;
import ae.oleapp.databinding.ActivityLoyaltyDetailBinding;
import ae.oleapp.models.Loyalty;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpiredLoyaltyActivity extends BaseActivity implements View.OnClickListener {

    private ActivityExpiredLoyaltyBinding binding;
    private final List<Loyalty> loyaltyList = new ArrayList<>();
    private LoyaltyAdapter loyaltyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpiredLoyaltyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        LinearLayoutManager loyaltyLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(loyaltyLayoutManager);
        loyaltyAdapter = new LoyaltyAdapter(getContext(), loyaltyList);
        loyaltyAdapter.setItemClickListener(loyaltyClickListener);
        binding.recyclerVu.setAdapter(loyaltyAdapter);

        binding.btnBack.setOnClickListener(this);


    }

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
                                if (!loyalty.getStatus().equalsIgnoreCase("Active")){
                                    loyaltyList.add(loyalty);
                                }
                            }

                            loyaltyAdapter.notifyDataSetChanged();
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