package ae.oleapp.hire;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.HirePlayerAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityHirePlayerBinding;
import ae.oleapp.models.PlayerSkill;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HirePlayerActivity extends BaseActivity implements View.OnClickListener {

    private ActivityHirePlayerBinding binding;
    private final List<PlayerSkill> playerSkillList =  new ArrayList<>();
    private HirePlayerAdapter adapter;
    private String skill = "", bookingId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHirePlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            skill = bundle.getString("skill", "");
            bookingId = bundle.getString("booking_id", "");
        }

        getAvailableHiringUsers(skill);

        LinearLayoutManager hirePlayerLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(hirePlayerLayoutManager);
        adapter = new HirePlayerAdapter(getContext(), playerSkillList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);

        binding.btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
    }

    HirePlayerAdapter.ItemClickListener itemClickListener = new HirePlayerAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            hirePlayerApi(playerSkillList.get(pos).getSkillId(), bookingId);
        }

        @Override
        public void phoneClicked(View view, int pos) {
            makeCall(playerSkillList.get(pos).getPhone());
        }
    };

    private void getAvailableHiringUsers(String skill) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getAvailableHiringUsers(skill);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            Gson gson = new Gson();
                            playerSkillList.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                playerSkillList.add(gson.fromJson(arr.get(i).toString(), PlayerSkill.class));
                            }
                            adapter.notifyDataSetChanged();
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

    private void hirePlayerApi(String skillId, String bookingId) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.hirePlayerApi(skillId, bookingId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

}
