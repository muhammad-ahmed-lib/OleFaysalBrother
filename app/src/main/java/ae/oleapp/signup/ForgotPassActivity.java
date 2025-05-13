package ae.oleapp.signup;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.tabs.TabLayout;
import com.hbb20.CCPCountry;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.activities.LoginActivity;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityForgotPassBinding;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleKeyboardUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityForgotPassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityForgotPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        // checkKeyboardListener();

        CCPCountry.setDialogTitle(getString(R.string.select_country_region));
        CCPCountry.setSearchHintMessage(getString(R.string.search_hint));

        binding.btnBack.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
        binding.getHelp.setOnClickListener(this);
    }

//    private void checkKeyboardListener() {
//        OleKeyboardUtils.addKeyboardToggleListener(this, new OleKeyboardUtils.SoftKeyboardToggleListener() {
//            @Override
//            public void onToggleSoftKeyboard(boolean isVisible) {
//                if (isVisible) {
//                    setHeight(0.85f);
//                }
//                else {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            final Handler handler = new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //your code here
//                                    setHeight(0.5f);
//                                }
//                            }, 50);
//                        }
//                    });
//                }
//            }
//        });
//    }

//    private void setHeight(float height) {
//        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.bottomContainer.getLayoutParams();
//        lp.matchConstraintPercentHeight = height;
//        binding.bottomContainer.setLayoutParams(lp);
//    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack) {
            hideKeyboard();
            finish();
        }
        else if (v == binding.btnSubmit) {
            submitClicked();
        }
        else if (v == binding.btnLogin) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else if (v == binding.getHelp) {
            getHelpClicked();
        }
    }

    private void getHelpClicked(){
        String url = getString(R.string.get_help_number);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void submitClicked() {
      //  if (isEmailLogin) {
            if (!Functions.isValidEmail(binding.etEmail.getText().toString())) {
                Functions.showToast(getContext(), getString(R.string.invalid_email), FancyToast.ERROR);
                return;
            }
            resetPassApi(binding.etEmail.getText().toString());
        //}
       // else {
//            String countryCode = binding.ccp.getSelectedCountryCodeWithPlus();
//            if (countryCode.isEmpty()) {
//                Functions.showToast(getContext(), getString(R.string.select_country_code), FancyToast.ERROR);
//                return;
//            }
//            if (binding.etPhone.getText().toString().isEmpty()) {
//                Functions.showToast(getContext(), getString(R.string.enter_phone), FancyToast.ERROR);
//                return;
//            }
//            if (binding.etPhone.getText().toString().startsWith("0")) {
//                Functions.showToast(getContext(), getString(R.string.phone_not_start_0), FancyToast.ERROR);
//                return;
//            }
//            resetPassApi(String.format("%s%s", countryCode, binding.etPhone.getText().toString()));
        //}
    }

    private void resetPassApi(String email) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.resetPass(Functions.getAppLang(getContext()), email);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            populateEmail(email);
                            binding.mainLay.setVisibility(View.GONE);
                            binding.successLay.setVisibility(View.VISIBLE);

//                            Intent intent = new Intent(getContext(), RecoverPassActivity.class);
//                            intent.putExtra("username", username);
//                            startActivity(intent);
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

    private void populateEmail(String email) {

        binding.mainLay.setVisibility(View.GONE);
        binding.successLay.setVisibility(View.VISIBLE);

        SpannableStringBuilder spannable = new SpannableStringBuilder();

        spannable.append(getString(R.string.email_sent)).append(" ");

        int start = spannable.length();
        spannable.append(email);
        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#0077B6")), start, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.append(" ").append(getString(R.string.email_sent_rest));
        binding.taglineText.setText(spannable);
    }
}
