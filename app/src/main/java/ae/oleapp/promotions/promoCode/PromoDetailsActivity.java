package ae.oleapp.promotions.promoCode;

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
import ae.oleapp.databinding.ActivityPromoDetailsBinding;
import ae.oleapp.models.Loyalty;
import ae.oleapp.models.PromoCode;
import ae.oleapp.promotions.loyalty.AddUpdateLoyaltyActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromoDetailsActivity extends BaseActivity implements View.OnClickListener {
    private ActivityPromoDetailsBinding binding;
    private int promoId;
    private PromoCode promoCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPromoDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            promoId = bundle.getInt("id");
            if (promoId != 0){
                getPromoDetail(true, promoId);
            }
        }

        binding.btnBack.setOnClickListener(this);
        binding.detailsCardVu.setOnClickListener(this);
        binding.appliedCardVu.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.detailsCardVu) {
            detailCardVu();
        }
        else if (v == binding.appliedCardVu) {
            appliedCardVu();
        }
        else if (v == binding.btnDelete) {
            deletePromoCode(true, promoCode.getId());
        }
        else if (v == binding.btnEdit) {
            if (promoCode.getId() == 0 ){
                Functions.showToast(getContext(), "Invalid Promo Code Id", FancyToast.ERROR);
                return;
            }
            Intent intent = new Intent(getContext(), AddUpdatePromoCodeActivity.class);
            intent.putExtra("update", true);
            intent.putExtra("id", promoCode.getId());
            startActivity(intent);
        }
    }

    private void detailCardVu() {
        binding.detailsCardVu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.detailsCardVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.black20Color));

        binding.appliedCardVu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.club_selection_color));
        binding.appliedCardVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));

        binding.tvClub.setText(promoCode.getClub().getName());
        binding.tvPromo.setText(promoCode.getPromoType());
        if (promoCode.getDiscountType().equalsIgnoreCase("FLAT_AMOUNT")){
            binding.tvDiscountType.setText("Flat Amount");
            binding.tvDiscount.setText(String.format("%s %s", promoCode.getDiscount(), promoCode.getCurrency()));
        } else{
            binding.tvDiscountType.setText("Percentage");
            binding.tvDiscount.setText(String.format("%s %%", promoCode.getDiscount()));
        }
        if (promoCode.getStatus().equalsIgnoreCase("Active")){
            binding.tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
        } else{
            binding.tvStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.redBookingColor));
        }
        binding.tvPromoCode.setText(promoCode.getCode());
        binding.tvDate.setText(String.format("%s - %s", promoCode.getStartDate(), promoCode.getEndDate()));
        binding.tvTitle.setText(promoCode.getName());
        binding.tvStatus.setText(promoCode.getStatus());
        binding.tvEligiblePlayer.setText(String.valueOf(promoCode.getTotalAllowedPlayers()));
        binding.tvTotalUsage.setText(String.valueOf(promoCode.getTotalTimesUsed()));
        binding.tvUsageLimit.setText(String.valueOf(promoCode.getUsageLimit()));
        binding.tvEachPlayerLimit.setText(String.valueOf(promoCode.getEachPlayerLimit()));
        binding.tvTotalDiscount.setText(String.valueOf(promoCode.getTotalDiscount()));

    }

    private void appliedCardVu() {
        binding.detailsCardVu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.club_selection_color));
        binding.detailsCardVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));

        binding.appliedCardVu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.appliedCardVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.black20Color));
    }

    private void deletePromoCode(boolean isLoader, int id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.deletePromoCode(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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

    private void getPromoDetail(boolean isLoader, int id) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getPromoDetail(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            promoCode = new Gson().fromJson(data.toString(), PromoCode.class);
                            detailCardVu();

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