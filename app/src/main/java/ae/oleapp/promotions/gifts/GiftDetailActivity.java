package ae.oleapp.promotions.gifts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityGiftDetailBinding;
import ae.oleapp.models.Gift;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiftDetailActivity extends BaseActivity implements View.OnClickListener {

    private ActivityGiftDetailBinding binding;
    private Gift gift;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGiftDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            gift = new Gson().fromJson(bundle.getString("gift"), Gift.class);
            if (gift != null){
                populateData();
            }
        }

        binding.btnEdit.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);


    }

    private void populateData() {
        binding.tvClub.setText(gift.getClub().getName());
        binding.tvName.setText(gift.getTitle());
        binding.tvTarget.setText(gift.getGiftTarget().getName());
        binding.tvStatus.setText(gift.getStatus());

        if (gift.getStatus().equalsIgnoreCase("Active")){
            binding.tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
        }
        else{
            binding.tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.redBookingColor));
        }

        Glide.with(getApplicationContext()).load(gift.getPhotoUrl()).into(binding.imgVu);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.btnEdit) {
            if (gift.getId() == 0 ){
                Functions.showToast(getContext(), "Invalid Gift Id", FancyToast.ERROR);
                return;
            }
            Intent intent = new Intent(getContext(), AddUpdateGiftActivity.class);
            intent.putExtra("update", true);
            intent.putExtra("id", gift.getId());
            startActivity(intent);
        }
        else if (v == binding.btnDelete) {
            if (gift.getId() == 0 ){
                Functions.showToast(getContext(), "Invalid Gift Id", FancyToast.ERROR);
                return;
            }

            deleteGift(true, gift.getId());
        }
    }

    private void deleteGift(boolean isLoader, int id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.deleteGift(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            JSONObject data = object.getJSONObject(Constants.kData);
                            finish();
                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

}