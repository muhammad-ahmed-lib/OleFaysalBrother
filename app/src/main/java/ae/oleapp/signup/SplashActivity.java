package ae.oleapp.signup;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import ae.oleapp.BuildConfig;
import ae.oleapp.R;
import ae.oleapp.activities.LoginActivity;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivitySplashBinding;
import ae.oleapp.models.CountryPhoneList;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.presentation.ui.inventory.InventoryActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import ae.oleapp.utils.TinyDB;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {
    private OleactivitySplashBinding binding;

    private Handler handler;
    private Uri deepLinkUri;
    TextView version_name;
    private static final int REQUEST_CODE = 100;
    private final List<CountryPhoneList> countryList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        getCountries(false);

        new TinyDB(this).putToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwidG9rZW5fdHlwZSI6IkFDQ0VTUyIsImlhdCI6MTc0NzA1OTk5NywiZXhwIjoxNzUyMjQzOTk3fQ.TifhXxKctMMommy9XVR786v8u3gotqD24x-9c0oVNcQ");


        if (Functions.getAppLangStr(getContext()).isEmpty()) {
            Functions.setAppLang(getContext(), "en");
        }

        version_name = findViewById(R.id.version_text);
        PackageManager pm = getApplicationContext().getPackageManager();
        String pkgName = getApplicationContext().getPackageName();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);
            String ver = BuildConfig.VERSION_NAME;
            version_name.setText("Version "+ver);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        deepLinkUri = getIntent().getData();
        //version_name.setText(pkgInfo.versionName);

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler();
        handler.postDelayed(runnable, 500);
    }

    private void handleIntent(Intent intent) {

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                getProfileAPI(false);
             if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                    if (deepLinkUri != null){
                        Functions.showToast(getContext(), "You are currently logged in as Owner\nPlease login as player first.", FancyToast.ERROR);
                    }
                 //     Intent i = new Intent(getContext(), InventoryActivity.class);
                   Intent i = new Intent(getContext(), OleOwnerMainTabsActivity.class);
                    startActivity(i);
                    finish();
                }
            }
            else {
                if (deepLinkUri != null){
                    Functions.showToast(getContext(), "Please login first to join the game.", FancyToast.ERROR);
                }
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(handler != null) {
            handler.removeCallbacks(runnable);
        }

    }

    private void getProfileAPI(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getProfile();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject obj = object.getJSONObject(Constants.kData);
                            int userId = obj.getInt("id");
                            Gson gson = new Gson();
                            UserInfo userInfo = gson.fromJson(obj.toString(), UserInfo.class);
                            userInfo.setId(String.valueOf(userId));
                            SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.putString(Constants.kUserType, userInfo.getUserRole());
                            editor.putString(Constants.kCurrency, "AED");
                            editor.apply();

                            Functions.saveUserinfo(getContext(), userInfo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
            }
        });
    }

    private void getCountries(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getCountries();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray country = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            countryList.clear();
                            AppManager.getInstance().countryPhoneLists.clear();
                            for (int i = 0; i < country.length(); i++) {
                                countryList.add(gson.fromJson(country.get(i).toString(), CountryPhoneList.class));
                            }
                            AppManager.getInstance().countryPhoneLists.addAll(countryList);
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
