package ae.oleapp.promotions.fieldOffer;

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
import ae.oleapp.databinding.ActivityFieldOfferDetailsBinding;
import ae.oleapp.models.Day;
import ae.oleapp.models.Days;
import ae.oleapp.models.FieldOffer;
import ae.oleapp.models.Loyalty;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FieldOfferDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityFieldOfferDetailsBinding binding;
    private FieldOffer offer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFieldOfferDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            offer = new Gson().fromJson(bundle.getString("offer"), FieldOffer.class);
            if (offer != null) {
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
            if (offer.getId() == 0 ){
                Functions.showToast(getContext(), "Invalid Offer Id", FancyToast.ERROR);
                return;
            }
            Intent intent = new Intent(getContext(), AddUpdateFieldOfferActivity.class);
            intent.putExtra("update", true);
            intent.putExtra("id", offer.getId());
            startActivity(intent);
        }
        else if (v == binding.btnDelete) {
            if (offer.getId() == 0 ){
                Functions.showToast(getContext(), "Invalid offer Id", FancyToast.ERROR);
                return;
            }
            deleteOffer(true, offer.getId());
        }
    }

    private void populateData() {
        binding.tvClub.setText(offer.getClub().getName());
        binding.tvField.setText(offer.getField().getName());
        binding.tvTime.setText(String.format("%s - %s", offer.getStartTime(), offer.getEndTime()));
        binding.tvDate.setText(String.format("%s %s %s", offer.getStartDate(), getString(R.string.to), offer.getEndDate()));
        binding.tvTitle.setText(offer.getTitle());
        binding.tvStatus.setText(offer.getStatus());

        if (offer.getDiscountType().equalsIgnoreCase("FLAT_AMOUNT")){
            binding.tvType.setText("Flat Amount");
            binding.tvValue.setText(String.format("%s %s", offer.getDiscount(), offer.getCurrency()));

        }
        else{
            binding.tvType.setText("Percentage");
            binding.tvValue.setText(String.format("%s %%", offer.getDiscount()));
        }

        if (offer.getStatus().equalsIgnoreCase("Active")){
            binding.tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
        }
        else{
            binding.tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.redBookingColor));
        }

        StringBuilder days = new StringBuilder();
        for (Day day : offer.getDays()) {
            if (days.length() > 0) {
                days.append(", ");
            }
            days.append(day.getDayName());
        }
        binding.tvDays.setText(days.toString());


    }

    private void deleteOffer(boolean isLoader, int id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.deleteOffer(id);
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
