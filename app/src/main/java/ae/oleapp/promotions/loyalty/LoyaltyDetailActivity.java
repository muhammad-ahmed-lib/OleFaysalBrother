package ae.oleapp.promotions.loyalty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONObject;
import java.net.UnknownHostException;
import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityLoyaltyDetailBinding;
import ae.oleapp.models.Loyalty;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoyaltyDetailActivity extends BaseActivity implements View.OnClickListener {

    private ActivityLoyaltyDetailBinding binding;
    private Loyalty loyalty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoyaltyDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            loyalty = new Gson().fromJson(bundle.getString("loyalty"), Loyalty.class);
            if (loyalty != null){
                populateData();
            }
        }

        binding.btnEdit.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.btnEdit) {
            if (loyalty.getId() == 0 ){
                Functions.showToast(getContext(), "Invalid Loyalty Id", FancyToast.ERROR);
                return;
            }
            Intent intent = new Intent(getContext(), AddUpdateLoyaltyActivity.class);
            intent.putExtra("update", true);
            intent.putExtra("id", loyalty.getId());
            startActivity(intent);
        }
        else if (v == binding.btnDelete) {
            if (loyalty.getId() == 0){
                Functions.showToast(getContext(), "Invalid loyalty Id", FancyToast.ERROR);
                return;
            }
            deleteLoyalty(true, loyalty.getId());
        }
    }

    private void populateData() {
        binding.tvClub.setText(loyalty.getClub().getName());
        binding.tvDate.setText(String.format("%s - %s", loyalty.getStartDate(), loyalty.getEndDate()));
        binding.tvCount.setText(String.valueOf(loyalty.getRequiredPoints()));
        binding.tvTitle.setText(loyalty.getTitle());
        binding.tvStatus.setText(loyalty.getStatus());
        if (loyalty.getStatus().equalsIgnoreCase("Active")){
            binding.tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
        }
        else{
            binding.tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.redBookingColor));
        }
        if (loyalty.getDiscountType().equalsIgnoreCase("FLAT_AMOUNT")){
            binding.tvType.setText("Flat Amount");
            binding.tvValue.setText(String.format("%s %s", loyalty.getDiscount(), loyalty.getCurrency()));

        }
        else{
            binding.tvType.setText("Percentage");
            binding.tvValue.setText(String.format("%s %%", loyalty.getDiscount()));
        }
    }


    private void deleteLoyalty(boolean isLoader, int id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.deleteLoyalty(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
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