package ae.oleapp.promotions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityPromotionBinding;
import ae.oleapp.models.PromoCount;
import ae.oleapp.owner.OleOfferListActivity;
import ae.oleapp.promotions.fieldOffer.ActiveFieldOffersActivity;
import ae.oleapp.promotions.gifts.ActiveGiftsActivity;
import ae.oleapp.promotions.loyalty.ActiveLoyaltyCardActivity;
import ae.oleapp.promotions.promoCode.ActivePromoCodeActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromotionActivity extends BaseActivity implements View.OnClickListener {
    private ActivityPromotionBinding binding;
    private String clubId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPromotionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id","");
        }

        binding.btnBack.setOnClickListener(this);
        binding.btnPromo.setOnClickListener(this);
        binding.btnGift.setOnClickListener(this);
        binding.btnFieldOffer.setOnClickListener(this);
        binding.btnLoyalty.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPromotionsAPI(true);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.btnPromo) {
            promoClicked();
        }
        else if (v == binding.btnGift) {
            giftClicked();
        }
        else if (v == binding.btnFieldOffer) {
            offerClicked();
        }
        else if (v == binding.btnLoyalty) {
            loyaltyClicked();
        }
    }


    private void promoClicked() {
        Intent intent = new Intent(getContext(), ActivePromoCodeActivity.class);
        intent.putExtra("club_id", clubId);
        startActivity(intent);
    }

    private void giftClicked() {
        startActivity(new Intent(getContext(), ActiveGiftsActivity.class));
    }

    private void offerClicked() {
        startActivity(new Intent(getContext(), ActiveFieldOffersActivity.class));
//        startActivity(new Intent(getContext(), OleOfferListActivity.class));
    }

    private void loyaltyClicked() {
        startActivity(new Intent(getContext(), ActiveLoyaltyCardActivity.class));
//        startActivity(new Intent(getContext(), OleDiscountCardsActivity.class));

    }

    private void getPromotionsAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.ownerPromotionsList(clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            PromoCount promoCount = new Gson().fromJson(data.toString(), PromoCount.class);
                            populateCountData(promoCount);
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

    private void populateCountData(PromoCount promoCount) {

        binding.tvPromoActive.setText(String.format("%s: %s", getString(R.string.active), promoCount.getPromoActive()));
        binding.tvPromoExpire.setText(String.format("%s: %s", getString(R.string.expired), promoCount.getPromoExpired()));

        binding.tvGiftActive.setText(String.format("%s: %s", getString(R.string.active), promoCount.getGiftActive()));
        binding.tvGiftExpire.setText(String.format("%s: %s", getString(R.string.expired), promoCount.getGiftExpired()));

        binding.tvFieldOfferActive.setText(String.format("%s: %s", getString(R.string.active), promoCount.getOfferActive()));
        binding.tvFieldOfferExpire.setText(String.format("%s: %s", getString(R.string.expired), promoCount.getOfferExpired()));

        binding.tvLoyaltyActive.setText(String.format("%s: %s", getString(R.string.active), promoCount.getLoyaltyActive()));
        binding.tvLoyaltyExpire.setText(String.format("%s: %s", getString(R.string.expired), promoCount.getLoyaltyExpired()));


    }

}