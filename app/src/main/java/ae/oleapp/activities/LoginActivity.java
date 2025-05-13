package ae.oleapp.activities;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.facebook.CallbackManager;
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.gson.Gson;
import com.hbb20.CCPCountry;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityLoginBinding;
import ae.oleapp.models.CountryPhoneList;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.signup.EmpLoginActivity;
import ae.oleapp.signup.ForgotPassActivity;
import ae.oleapp.signup.UserTypeActivity;
import ae.oleapp.signup.VerifyPhoneActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityLoginBinding binding;
    String selectedCountryCode = "", source = "", loginType = "", allowModule = "";
    private String countryId = "";
    private boolean isPassVisible = false;
    private CredentialManager credentialManager;
    private CallbackManager callbackManager;
    private  List<CountryPhoneList> countryList = new ArrayList<>();
    private boolean isDataNull = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();


        credentialManager = CredentialManager.create(getContext());
        callbackManager = CallbackManager.Factory.create();
        setLabelText(Functions.getAppLangStr(getContext()));

        countryList = AppManager.getInstance().countryPhoneLists;
        countryId =  FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());


//        checkKeyboardListener();
        CCPCountry.setDialogTitle(getString(R.string.select_country_region));
        CCPCountry.setSearchHintMessage(getString(R.string.search_hint));

        binding.btnGuest.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);
        binding.resetPassword.setOnClickListener(this);
        binding.btnGoogle.setOnClickListener(this);
        binding.btnFacebook.setOnClickListener(this);
        binding.btnEmployee.setOnClickListener(this);
        binding.btnEye.setOnClickListener(this);
        binding.langEn.setOnClickListener(this);
        binding.langAr.setOnClickListener(this);

        allowModule = Functions.getPrefValue(getContext(), Constants.kAllowModule);
        loginType = Functions.getPrefValue(getContext(), Constants.kLoginType);

//        getCountries(false);

        binding.ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryId =  FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());
                selectedCountryCode = binding.ccp.getSelectedCountryCodeWithPlus();
                populateData();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ipdetails(true);
    }

    @Override
    public void onClick(View v) {
         if (v == binding.btnContinue) {
            btnContinueClicked();
        }
        else if (v == binding.btnGuest) {
            skipClicked();
        }
        else if (v == binding.resetPassword){
            Intent intent = new Intent(getContext(), ForgotPassActivity.class);
            startActivity(intent);
        }
        else if (v == binding.btnEmployee) {
             Intent i = new Intent(getContext(), EmpLoginActivity.class);
             startActivity(i);
         }
         else if (v == binding.btnGoogle) {
             runGoogleLogin("239623316271-28m8lnkqe9kh06m10458e4tkn4gt2mv9.apps.googleusercontent.com");
         }
         else if (v == binding.btnFacebook){

         }
         else if (v == binding.btnEye) {
             btnEye_clicked();
         }
         else if (v == binding.langEn) {
             English();
         }
         else if (v == binding.langAr) {
             Arabic();
         }
    }


    private void English() {
        binding.langEn.setBackground(getResources().getDrawable(R.drawable.v5_circle_white));
        binding.langAr.setBackground(getResources().getDrawable(R.drawable.transparent_bg));
        selectLanguage("en");
    }

    private void Arabic() {
        binding.langEn.setBackground(getResources().getDrawable(R.drawable.transparent_bg));
        binding.langAr.setBackground(getResources().getDrawable(R.drawable.v5_circle_white));
        selectLanguage("ar");
    }

    public void selectLanguage(String lang) {

        if (lang.equalsIgnoreCase("ar")){
            Functions.setAppLang(getContext(), "ar");
            Functions.changeLanguage(getContext(),"ar");
        }else{
            Functions.setAppLang(getContext(), "en");
            Functions.changeLanguage(getContext(),"en");
        }
        setLabelText(lang);
        getContext().recreate();


    }

    private void setLabelText(String lang) {
        if (lang.equalsIgnoreCase("ar")) {
            binding.langAr.setText("Ar");
            binding.langEn.setBackground(getResources().getDrawable(R.drawable.transparent_bg));
            binding.langAr.setBackground(getResources().getDrawable(R.drawable.v5_circle_white));
        }
        else {
            binding.langEn.setText("En");
            binding.langEn.setBackground(getResources().getDrawable(R.drawable.v5_circle_white));
            binding.langAr.setBackground(getResources().getDrawable(R.drawable.transparent_bg));
        }
    }

    private void runGoogleLogin(@NonNull String clientId) {
        GetSignInWithGoogleOption googleIdOption = new GetSignInWithGoogleOption.Builder(clientId).build();
        GetCredentialRequest request = new GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build();
        credentialManager.getCredentialAsync(getContext(), request, null, THREAD_POOL_EXECUTOR, new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
            @Override
            public void onResult(GetCredentialResponse getCredentialResponse) {
                GoogleIdTokenCredential credential = (GoogleIdTokenCredential) getCredentialResponse.getCredential();
                String idToken = credential.getIdToken();
                source = "social";
                loginSignUpSocialApi(idToken, "GOOGLE");
            }
            @Override
            public void onError(@NonNull GetCredentialException e) {
                Log.e("Google error", e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Functions.showToast(getContext(), e.getMessage(), FancyToast.ERROR);
                    }
                });
            }
        });
    }


    private void btnContinueClicked() {
        if (countryId.isEmpty()){
            countryId =  FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());
        }
        selectedCountryCode = binding.ccp.getSelectedCountryCodeWithPlus();

        if (selectedCountryCode.isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.select_country_code), FancyToast.ERROR);
            return;
        }
        if (binding.etPhone.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_phone), FancyToast.ERROR);
            return;
        }
        if (binding.etPhone.getText().toString().startsWith("0")) {
            Functions.showToast(getContext(), getString(R.string.phone_not_start_0), FancyToast.ERROR);
        }

        if (!loginType.equalsIgnoreCase("otp")){
            binding.passwordVu.setVisibility(View.VISIBLE);
            binding.resetPassword.setVisibility(View.VISIBLE);
            if (binding.etPassword.getText().toString().isEmpty()){
                Functions.showToast(getContext(), getString(R.string.enter_password), FancyToast.ERROR);
            }else if (binding.etPassword.getText().length() < 4){
                Functions.showToast(getContext(), getString(R.string.pass_must), FancyToast.ERROR);
            }
            source = "password";
            loginWithPassword(String.valueOf(countryId), binding.etPhone.getText().toString(), binding.etPassword.getText().toString());

        }else {
            binding.passwordVu.setVisibility(View.GONE);
            binding.resetPassword.setVisibility(View.GONE);
            source = "otp";
            loginApi(String.valueOf(countryId), binding.etPhone.getText().toString());
        }
    }

    private void loginApi(String lCountryId, String phone) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.loginWithPhone(lCountryId, phone);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);

                                String token = obj.getString("token");
                                Intent intent = new Intent(getContext(), VerifyPhoneActivity.class);
                                intent.putExtra("country_id", countryId);
                                intent.putExtra("country_code", selectedCountryCode);
                                intent.putExtra("phone", phone);
                                intent.putExtra("token", token);
                                intent.putExtra("source", source);
                                startActivity(intent);
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
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR); //Do something with message Failed to connect to APi
                }
            }
        });
    }

    private void loginWithPassword(String CountryId, String phone, String password) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.loginWithPassword(CountryId, phone, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);

                            if (data.isNull("auth")) {
                                Functions.showToast(getContext(), "No Record Found Please Click Sign Up!", FancyToast.ERROR);
                                isDataNull = true;
                                source = "password";
                            }else{
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

                                Functions.saveUserinfo(getContext(), userInfo);

                                    Intent intent = new Intent(getContext(), OleOwnerMainTabsActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();

                                String fcmToken = Functions.getPrefValue(getContext(), Constants.kFCMToken);
                                if (!fcmToken.isEmpty()) {
                                    sendFcmTokenApi(fcmToken);
                                }
                            }

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
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR); //Do something with message Failed to connect to APi
                }
            }
        });
    }

    private void ipdetails(Boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(),"Image processing") : null;
        Call<ResponseBody>  call = AppManager.getInstance().apiInterfaceNew.getIpDetails();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            loginType = data.getString("login_type");
                            allowModule = data.getString("allow_module");
                            countryId = String.valueOf(data.getInt("id"));
                            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString(Constants.kLoginType, loginType);
                            editor.putString(Constants.kAllowModule, allowModule);
                            editor.apply();
                            populateData();
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

    private void btnEye_clicked() {
        if (isPassVisible) {
            binding.etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.btnEye.setImageResource(R.drawable.view_ic);
            isPassVisible = false;
        }
        else {
            binding.etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.btnEye.setImageResource(R.drawable.hide_ic);
            isPassVisible = true;
        }
    }


    private void skipClicked() {

    }

    private void populateData(){
        for (CountryPhoneList country: countryList){
            if (countryId.equalsIgnoreCase(String.valueOf(country.getId()))){
                loginType = country.getLoginType();
                allowModule = country.getAllowModule();
                SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                editor.putString(Constants.kLoginType, loginType);
                editor.putString(Constants.kAllowModule, allowModule);
                editor.apply();
            }
        }

        if (loginType.equalsIgnoreCase("OTP") && allowModule.equalsIgnoreCase("ALL")){
            binding.passwordVu.setVisibility(View.GONE);
            binding.resetPassword.setVisibility(View.GONE);
            binding.tvGuest.setText("Continue as Guest");
        }
        else if (loginType.equalsIgnoreCase("OTP") && allowModule.equalsIgnoreCase("LINEUP")) {
            binding.passwordVu.setVisibility(View.GONE);
            binding.resetPassword.setVisibility(View.GONE);
            binding.tvGuest.setText("Continue as Guest");
        }
        else if (loginType.equalsIgnoreCase("PASSWORD") && allowModule.equalsIgnoreCase("LINEUP")) {
            binding.passwordVu.setVisibility(View.VISIBLE);
            binding.resetPassword.setVisibility(View.VISIBLE);
            binding.tvGuest.setText("Sign Up!");
            isDataNull = true;
            source = "password";
        }
        else if (loginType.equalsIgnoreCase("PASSWORD") && allowModule.equalsIgnoreCase("ALL")) {
            binding.passwordVu.setVisibility(View.VISIBLE);
            binding.resetPassword.setVisibility(View.VISIBLE);
            binding.tvGuest.setText("Sign Up!");
            isDataNull = true;
            source = "password";
        }
        else{
            Functions.showToast(getContext(), "Login Types Mismatched", FancyToast.INFO);
        }

    }

    private void loginSignUpSocialApi(String token, String provider) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.loginSignUpSocialApi(provider, token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            JSONObject data = object.getJSONObject(Constants.kData);

                            if (data.isNull("auth")) {
                                Intent intent = new Intent(getContext(), UserTypeActivity.class);
                                intent.putExtra("source", source);
                                startActivity(intent);
                                finish();
                            }else{
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

                                Functions.saveUserinfo(getContext(), userInfo);

                                Intent intent = new Intent(getContext(), OleOwnerMainTabsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();


                                String fcmToken = Functions.getPrefValue(getContext(), Constants.kFCMToken);
                                if (!fcmToken.isEmpty()) {
                                    sendFcmTokenApi(fcmToken);
                                }
                            }

                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else if (response.errorBody() != null) {
                    try {
                        JSONObject errorObject = new JSONObject(response.errorBody().string());
                        Functions.showToast(getContext(), errorObject.getString(Constants.kMsg), FancyToast.ERROR);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //Handle if there is any issue
        }
    }

}
