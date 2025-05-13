package ae.oleapp.owner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityCreatePromotionBinding;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleCreatePromotionActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityCreatePromotionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityCreatePromotionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.bar.toolbarTitle.setText(R.string.create_promotion);

        binding.bar.backBtn.setOnClickListener(this);
        binding.offerVu.setOnClickListener(this);
        binding.promoVu.setOnClickListener(this);
        binding.discountVu.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getPromotionsAPI(false);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.bar.backBtn) {
            finish();
        }
        else if (v == binding.offerVu) {
            offerClicked();
        }
        else if (v == binding.promoVu) {
            promoClicked();
        }
        else if (v == binding.discountVu) {
            discountClicked();
        }
    }

    private void offerClicked() {
        startActivity(new Intent(getContext(), OleOfferListActivity.class));
    }

    private void promoClicked() {
    }

    private void discountClicked() {
    }

}