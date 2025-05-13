package ae.oleapp.signup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONObject;
import java.net.UnknownHostException;
import ae.oleapp.R;
import ae.oleapp.activities.LoginActivity;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityVerifyPhoneBinding;
import ae.oleapp.dialogs.OleCustomAlertDialog;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import cn.iwgang.countdownview.CountdownView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyPhoneActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityVerifyPhoneBinding binding;
    private boolean isForUpdate = false;
    String allowModule = "";
    private String token = "", phone = "", countryCode = "", source = "";
    private int countryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityVerifyPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();
        binding.tvPhone.setText("");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isForUpdate = bundle.getBoolean("is_update", false);
            countryId = bundle.getInt("country_id");
            countryCode = bundle.getString("country_code", "");
            phone = bundle.getString("phone", "");
            token = bundle.getString("token", "");
            source = bundle.getString("source", "");
            binding.tvPhone.setText(countryCode+phone);
        }

        allowModule = Functions.getPrefValue(getContext(), Constants.kAllowModule);
        binding.pinVu.setItemBackgroundColor(Color.WHITE);
        binding.pinVu.requestFocus();
        startCountDown();

        binding.pinVu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.pinVu.getText().toString().length() == 4) {
                    verifyClicked();
                }
            }
        });

        binding.btnBack.setOnClickListener(this);
        binding.btnVerify.setOnClickListener(this);
        binding.btnResend.setOnClickListener(this);
        binding.getHelp.setOnClickListener(this);
    }

    private void startCountDown() {
        binding.countVu.start(60000);
        binding.btnResend.setVisibility(View.GONE);
        binding.countVu.setVisibility(View.VISIBLE);
        binding.tv.setVisibility(View.VISIBLE);
        binding.countVu.setOnCountdownEndListener(endListener);
    }

    private final CountdownView.OnCountdownEndListener endListener = new CountdownView.OnCountdownEndListener() {
        @Override
        public void onEnd(CountdownView cv) {
            cv.stop();
            binding.btnResend.setVisibility(View.VISIBLE);
            binding.tv.setVisibility(View.GONE);
            binding.countVu.setVisibility(View.GONE);
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack) {
            backClicked();
        }
        else if (v == binding.btnVerify) {
            verifyClicked();
        }
        else if (v == binding.btnResend) {
            resendClicked();
        }
        else if (v == binding.getHelp) {
            getHelpClicked();
        }
    }

    private void backClicked() {
        hideKeyboard();
        finish();
    }

    private void verifyClicked() {
        if (binding.pinVu.getText().toString().length() < 4 || token.isEmpty()) {
            Functions.showToast(this, getResources().getString(R.string.invalid_code), FancyToast.ERROR);
            return;
        }
        verifyCodeAPI(binding.pinVu.getText().toString(), token);
    }
    private void getHelpClicked(){
        String url = "https://api.whatsapp.com/send?phone=+971547215551";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void resendClicked() {
        resendCodeAPI();
    }

    private void gotoLoginActivity() {
        binding.countVu.stop();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void resendCodeAPI() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.resendCodeV2(Functions.getAppLang(getContext()), userId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            binding.countVu.stop();
                            startCountDown();
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

    private void verifyCodeAPI(String otp, String token) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        String userId = Functions.getPrefValue(getContext(), Constants.kUserID);
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.verifyOTP(otp, token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            binding.countVu.stop();
                            JSONObject data = object.getJSONObject(Constants.kData);


                            if (data.isNull("auth")) {
                                Intent intent = new Intent(getContext(), UserTypeActivity.class);
                                intent.putExtra("country_id", countryId);
                                intent.putExtra("country_code", countryCode);
                                intent.putExtra("phone", phone);
                                intent.putExtra("source", source);
                                startActivity(intent);
                                finish();
                            } else {

                                JSONObject auth = data.getJSONObject("auth");
                                JSONObject userObj = data.getJSONObject("user");
                                int userId = userObj.getInt("id");

                                UserInfo userInfo = new Gson().fromJson(userObj.toString(), UserInfo.class);
                                userInfo.setId(String.valueOf(userId));
                                userInfo.setCurrency("AED");
                                SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                                editor.putString(Constants.kUserID, userInfo.getId());
                                editor.putString(Constants.kIsSignIn, "1");
                                editor.putString(Constants.kUserType, userInfo.getUserRole());
                                editor.putString(Constants.kaccessToken, auth.getString("access"));
                                editor.putString(Constants.kRefreshToken, auth.getString("refresh"));
                                editor.putString(Constants.kCurrency, userInfo.getCurrency());
                                editor.apply();
//
                                Functions.saveUserinfo(getContext(), userInfo);

                                if (userInfo.getUserRole().equalsIgnoreCase(Constants.kOwnerType)) {
                                    Intent intent = new Intent(getContext(), OleOwnerMainTabsActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                                String fcmToken = Functions.getPrefValue(getContext(), Constants.kFCMToken);
                                if (!fcmToken.isEmpty()) {
                                    sendFcmTokenApi(fcmToken);
                                }
                            }
                        }
                        else if (object.getInt(Constants.kStatus) == 423) {
                            Functions.showAlert(getContext(), getString(R.string.alert), object.getString(Constants.kMsg), new OleCustomAlertDialog.OnDismiss() {
                                @Override
                                public void dismiss() {
                                    gotoLoginActivity();
                                }
                            });
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
