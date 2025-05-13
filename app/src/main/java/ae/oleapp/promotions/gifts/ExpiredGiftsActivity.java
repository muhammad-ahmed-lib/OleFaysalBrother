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
import ae.oleapp.databinding.ActivityExpiredGiftsBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.Gift;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpiredGiftsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityExpiredGiftsBinding binding;
    private final List<Gift> giftsList = new ArrayList<>();
    private GiftAdapter giftAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityExpiredGiftsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        LinearLayoutManager giftsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(giftsLayoutManager);
        giftAdapter = new GiftAdapter(getContext(), giftsList);
        giftAdapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(giftAdapter);

        binding.btnBack.setOnClickListener(this);

    }

    GiftAdapter.ItemClickListener itemClickListener = new GiftAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), GiftDetailActivity.class);
            intent.putExtra("gift", new Gson().toJson(giftsList.get(pos)));
            startActivity(intent);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        getGifts(giftsList.isEmpty());
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
                                if (gift.getStatus().equalsIgnoreCase("Expired")){
                                    giftsList.add(gift);
                                }
                            }
                            giftAdapter.notifyDataSetChanged();

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