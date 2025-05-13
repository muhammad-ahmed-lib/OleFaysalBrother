package ae.oleapp.partner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.gson.Gson;
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
import ae.oleapp.adapters.OleClubNameAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityPartnerHomeBinding;
import ae.oleapp.models.Club;
import ae.oleapp.models.Partner;
import ae.oleapp.partner.adapters.PartnerAdapter;
import ae.oleapp.partner.adapters.VerticalPartnerAdapter;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartnerHomeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPartnerHomeBinding binding;
    private final List<Partner> partnerList = new ArrayList<>();
    private final List<Club> clubList = new ArrayList<>();
    private VerticalPartnerAdapter verticalAdapter;
    private OleClubNameAdapter oleClubNameAdapter;
    public String selectedClubId = "";
    private PartnerAdapter adapter;
    private int selectedIndex = 0;
    private String clubId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPartnerHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            clubId = bundle.getString("club_id","");
        }

        getAllPartners(true);

        Locale locale = Locale.getDefault(); // Change this to a specific locale if needed
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", locale);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", locale);

        binding.tvMonth.setText(monthFormat.format(calendar.getTime()));
        binding.tvYear.setText(yearFormat.format(calendar.getTime()));


        Club club = new Club();
        club.setId("");
        club.setName(getString(R.string.all));
        clubList.add(club);
        clubList.addAll(AppManager.getInstance().clubs);

        LinearLayoutManager oleClubNameLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubNameRecyclerVu.setLayoutManager(oleClubNameLayoutManager);
        oleClubNameAdapter = new OleClubNameAdapter(getContext(), clubList);
        oleClubNameAdapter.setItemClickListener(clubNameClickListener);
        binding.clubNameRecyclerVu.setAdapter(oleClubNameAdapter);

        LinearLayoutManager HorizPartnerLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.partnerRecyclerVu.setLayoutManager(HorizPartnerLayoutManager);
        adapter = new PartnerAdapter(getContext(), partnerList);
        adapter.setItemClickListener(itemClickListener);
        binding.partnerRecyclerVu.setAdapter(adapter);

        LinearLayoutManager VerPartnerLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(VerPartnerLayoutManager);
        verticalAdapter = new VerticalPartnerAdapter(getContext(), partnerList);
        verticalAdapter.setItemClickListener(vertItemClickListener);
        binding.recyclerVu.setAdapter(verticalAdapter);

        binding.btnBack.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);

    }

    OleClubNameAdapter.ItemClickListener clubNameClickListener = new OleClubNameAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            selectedIndex = pos;
            selectedClubId = clubList.get(selectedIndex).getId(); //check this
            oleClubNameAdapter.setSelectedIndex(selectedIndex);
            clubId = selectedClubId;
            getAllPartners(true);
        }
    };

    PartnerAdapter.ItemClickListener itemClickListener = new PartnerAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), SinglePartnerDetailActivity.class);
            intent.putExtra("partner_id", partnerList.get(pos).getId());
            intent.putExtra("club_id", clubId);
            startActivity(intent);

        }
    };

    VerticalPartnerAdapter.ItemClickListener vertItemClickListener = new VerticalPartnerAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            Intent intent = new Intent(getContext(), SinglePartnerDetailActivity.class);
            intent.putExtra("partner_id", partnerList.get(pos).getId());
            intent.putExtra("club_id", clubId);
            startActivity(intent);

        }

        @Override
        public void callClicked(View view, int pos) {
            String phone = partnerList.get(pos).getPhoneCountryCode()+partnerList.get(pos).getPhoneNumber();
            makeCall(phone);
        }
    };

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.btnAdd) {
            Intent intent = new Intent(getContext(), AddUpdatePartnerActivity.class);
            intent.putExtra("club_id", clubId);
            startActivity(intent);
        }
    }


    private void getAllPartners(Boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
            Call<ResponseBody> call = AppManager.getInstance().apiInterfacePartner.getAllPartners(clubId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Functions.hideLoader(hud);
                    if (response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                                JSONArray data = object.getJSONArray(Constants.kData);
                                Gson gson = new Gson();
                                partnerList.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    partnerList.add(gson.fromJson(data.get(i).toString(), Partner.class));
                                }
                                adapter.notifyDataSetChanged();
                                verticalAdapter.notifyDataSetChanged();
                            }
                            else {
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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