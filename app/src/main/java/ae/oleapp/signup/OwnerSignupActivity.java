package ae.oleapp.signup;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import ae.oleapp.R;
import ae.oleapp.activities.OleWebVuActivity;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityOwnerSignupBinding;
import ae.oleapp.models.CountryPhoneList;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.util.OleKeyboardUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerSignupActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityOwnerSignupBinding binding;
    private String selectedCountryCode = "", countryCode = "", dobStr = "", loginType = "", allowModule = "", source = "";
    private String countryId = "";
    private List<CountryPhoneList> countryList = new ArrayList<>();
    private String phone = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityOwnerSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        countryList = AppManager.getInstance().countryPhoneLists;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            countryId = String.valueOf(bundle.getInt("country_id"));
            countryCode = bundle.getString("country_code", "");
            phone = bundle.getString("phone", "");
            source = bundle.getString("source", "");
            if (!phone.isEmpty()){
                binding.etPhone.setText(phone);
            }
        }

        if (countryId.equalsIgnoreCase("0")){
            if (countryCode.isEmpty()){
                countryId =  FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());
            }else{
                countryId =  FindCountryId(countryCode);
            }
        }

        broCheckUser();

        checkKeyboardListener();

        binding.btnBack.setOnClickListener(this);
        binding.btnComplete.setOnClickListener(this);
        binding.tvTerms.setOnClickListener(this);
        binding.etDob.setOnClickListener(this);
        binding.ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryId =  FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());
                selectedCountryCode = binding.ccp.getSelectedCountryCodeWithPlus();
            }
        });
    }

    private void checkKeyboardListener() {
        OleKeyboardUtils.addKeyboardToggleListener(this, new OleKeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                if (isVisible) {
//                    setHeight(0.88f);
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //your code here
//                                    setHeight(0.5f);
                                }
                            }, 50);
                        }
                    });
                }
            }
        });
    }

//    private void setHeight(float height) {
//        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) binding.bottomContainer.getLayoutParams();
//        lp.matchConstraintPercentHeight = height;
//        binding.bottomContainer.setLayoutParams(lp);
//    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack) {
            backClicked();
        }
        else if (v == binding.btnComplete) {
            signupClicked();
        }
        else if (v == binding.tvTerms) {
            termsClicked();
        }
        else if (v == binding.etDob) {
            dobClicked();
        }
    }

    private void backClicked() {
        hideKeyboard();
        finish();
    }

    private void signupClicked() {
        if (binding.etName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_name), FancyToast.ERROR);
            return;
        }
        if (Functions.isArabic(binding.etName.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.enter_name_english), FancyToast.ERROR);
            return;
        }
        if (!Functions.isValidEmail(binding.etEmail.getText().toString())) {
            Functions.showToast(getContext(), getString(R.string.invalid_email), FancyToast.ERROR);
            return;
        }
        if (dobStr.isEmpty()) {
            Functions.showToast(getContext(), "Enter date of birth", FancyToast.ERROR);
            return;
        }
        if (binding.etClubName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_club_name), FancyToast.ERROR);
            return;
        }
//        if (selectedCountryCode.isEmpty()) {
//            Functions.showToast(getContext(), getString(R.string.select_country_code), FancyToast.ERROR);
//            return;
//        }

        signUpApi(binding.etName.getText().toString(),  binding.etEmail.getText().toString(),  dobStr, countryId,  binding.etPhone.getText().toString(),
                binding.etPass.getText().toString(), "OWNER",  binding.etClubName.getText().toString(),   binding.etRefCode.getText().toString(), source);

    }

    private void termsClicked() {
        Intent intent = new Intent(getContext(), OleWebVuActivity.class);
        startActivity(intent);
    }

//    private void signUpApi(String fullName, String email, String type) {
//        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.register(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID),  fullName, email, type, "android"); // Functions.getPrefValue(getContext(), Constants.kUserID),
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
//                            JSONObject obj = object.getJSONObject(Constants.kData);
//                            UserInfo userInfo = new Gson().fromJson(obj.toString(), UserInfo.class);
//                            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
//                            editor.putString(Constants.kUserID, userInfo.getId());
//                            editor.putString(Constants.kIsSignIn, "1");
//                            editor.putString(Constants.kUserType, userInfo.getUserRole());
//                            editor.putString(Constants.kCurrency, userInfo.getCurrency());
//                            editor.apply();
//
//                            Functions.saveUserinfo(getContext(), userInfo);
//
//                            String fcmToken = Functions.getPrefValue(getContext(), Constants.kFCMToken);
//                            if (!fcmToken.isEmpty()) {
//                                sendFcmTokenApi(fcmToken);
//                            }
//
//                            Intent intent = new Intent(getContext(), OleOwnerMainTabsActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();
//
//                        }
//                        else if (object.getInt(Constants.kStatus) == 423) {
//
//                            Intent intent = new Intent(getContext(), OwnerSignupConfirmationActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();
//
//                            // when field owner account is not activiated by admin
////                            Functions.showAlert(getContext(), getString(R.string.alert), object.getString(Constants.kMsg), new OleCustomAlertDialog.OnDismiss() {
////                                @Override
////                                public void dismiss() {
////                                    Intent intent = new Intent(getContext(), LoginActivity.class);
////                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                                    startActivity(intent);
////                                    finish();
////                                }
////                            });
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                }
//                else {
//                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                }
//                else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
//    }


    private void signUpApi(String name, String email, String dob, String countryId, String number, String password, String role, String clubName, String referralCode, String source) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.signUpApi(name, email, dob, countryId, number, password, role, clubName, referralCode, source);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            JSONObject auth = obj.getJSONObject("auth");
                            JSONObject userObj = obj.getJSONObject("user");
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

                            String fcmToken = Functions.getPrefValue(getContext(), Constants.kFCMToken);
                            if (!fcmToken.isEmpty()) {
                                sendFcmTokenApi(fcmToken);
                            }

                            Intent intent = new Intent(getContext(), OleOwnerMainTabsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        }
                        else if (object.getInt(Constants.kStatus) == 423) {

                            Intent intent = new Intent(getContext(), OwnerSignupConfirmationActivity.class);
                            startActivity(intent);
                            finish();

                            // when field owner account is not activiated by admin
//                            Functions.showAlert(getContext(), getString(R.string.alert), object.getString(Constants.kMsg), new OleCustomAlertDialog.OnDismiss() {
//                                @Override
//                                public void dismiss() {
//                                    Intent intent = new Intent(getContext(), LoginActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            });
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

//    private void getCountries(boolean isLoader) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getCountries();
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            JSONArray country = object.getJSONArray(Constants.kData);
//                            Gson gson = new Gson();
//                            countryPhoneList.clear();
//                            for (int i = 0; i < country.length(); i++) {
//                                countryPhoneList.add(gson.fromJson(country.get(i).toString(), CountryPhoneList.class));
//                            }
//                            FindCountryId();
//                        } else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                } else {
//                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                } else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
//    }


    private void broCheckUser(){
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
            binding.passVu.setVisibility(View.GONE);
            binding.phoneVu.setVisibility(View.GONE);
        }
        else if (loginType.equalsIgnoreCase("OTP") && allowModule.equalsIgnoreCase("LINEUP")) {
            binding.passVu.setVisibility(View.GONE);
            binding.phoneVu.setVisibility(View.GONE);
        }
        else if (loginType.equalsIgnoreCase("PASSWORD") && allowModule.equalsIgnoreCase("LINEUP")) {
            binding.passVu.setVisibility(View.VISIBLE);
            binding.phoneVu.setVisibility(View.VISIBLE);
        }
        else if (loginType.equalsIgnoreCase("PASSWORD") && allowModule.equalsIgnoreCase("ALL")) {
            binding.passVu.setVisibility(View.VISIBLE);
            binding.phoneVu.setVisibility(View.VISIBLE);
        }
        else{
            Functions.showToast(getContext(), "Login Types Mismatched", FancyToast.INFO);
        }

    }


    private void dobClicked() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), R.style.datepicker, new android.app.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                dobStr = formatter.format(calendar.getTime());
                formatter.applyPattern("dd/MM/yyyy");
                binding.etDob.setText(formatter.format(calendar.getTime()));

            }
        },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        pickerDialog.show();
    }
}
