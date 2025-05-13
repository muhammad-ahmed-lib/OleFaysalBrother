package ae.oleapp.partner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivitySinglePartnerDetailBinding;
import ae.oleapp.fragments.AddWaitingUserDialogFragment;
import ae.oleapp.models.Club;
import ae.oleapp.models.FieldOffer;
import ae.oleapp.models.Partner;
import ae.oleapp.models.Transaction;
import ae.oleapp.partner.adapters.PartnerAdapter;
import ae.oleapp.partner.adapters.TransactionAdapter;
import ae.oleapp.partner.adapters.VerticalPartnerAdapter;
import ae.oleapp.partner.bottomSheets.ChangeCompanyBottomSheet;
import ae.oleapp.partner.bottomSheets.PaymentBottomSheetFragment;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SinglePartnerDetailActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySinglePartnerDetailBinding binding;
    private int partnerId = 0;
    private Partner partner;
    private String clubId = "";
    private final List<Transaction> transactionList = new ArrayList<>();
    private TransactionAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySinglePartnerDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        LinearLayoutManager transactionLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(transactionLayoutManager);
        adapter = new TransactionAdapter(getContext(), transactionList);
        binding.recyclerVu.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            partnerId = bundle.getInt("partner_id");
            clubId = bundle.getString("club_id", "");

        }

        getSinglePartnerDetails(true , partnerId, Integer.parseInt(clubId));

        binding.btnBack.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
        binding.btnCall.setOnClickListener(this);
        binding.summaryCardVu.setOnClickListener(this);
        binding.profitsCardVu.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.btnChange.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
        binding.btnFilter.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.btnDelete) {
            deletePartner(Integer.parseInt(clubId), partnerId);
        }
        else if (v == binding.btnCall) {
                if (partner != null){
                    String phone = partner.getPhoneCountryCode()+partner.getPhoneNumber();
                    makeCall(phone);
                }
        }
        else if (v == binding.summaryCardVu) {
            summaryCardVu();
        }
        else if (v == binding.profitsCardVu) {
            profitsCardVu();


        }
        else if (v == binding.btnEdit) {
            Intent intent = new Intent(getContext(), AddUpdatePartnerActivity.class);
            intent.putExtra("is_update", true);
            intent.putExtra("club_id", clubId);
            intent.putExtra("partner_id", partnerId);
            intent.putExtra("partner", new Gson().toJson(partner));

            updatePartnerResultLauncher.launch(intent);
        }
        else if (v == binding.btnChange) {
            showChangeCompanySheet();
        }
        else if (v == binding.btnPay) {
            showPaymentSheet(partner.getNetProfit().getNetProfit());
        }
        else if (v == binding.btnFilter) {

        }

    }

    private void summaryCardVu() {

        binding.summaryVu.setVisibility(View.VISIBLE);
        binding.btnDelete.setVisibility(View.VISIBLE);
        binding.profitsVu.setVisibility(View.GONE);
        binding.btnFilter.setVisibility(View.GONE);

        binding.summaryCardVu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.summaryCardVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.black20Color));

        binding.profitsCardVu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.club_selection_color));
        binding.profitsCardVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));

    }

    private void profitsCardVu() {

        binding.summaryVu.setVisibility(View.GONE);
        binding.btnDelete.setVisibility(View.GONE);
        binding.profitsVu.setVisibility(View.VISIBLE);
        binding.btnFilter.setVisibility(View.VISIBLE);

        binding.profitsCardVu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
        binding.profitsCardVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.black20Color));

        binding.summaryCardVu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.club_selection_color));
        binding.summaryCardVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));

        binding.partnerProfit.setText(String.format("%s %s", partner.getShares(), partner.getSharesType()));
        binding.tvProfit.setText(String.format("%s %s", partner.getCurrency(), partner.getNetProfit().getNetProfit()));

        getPartnerProfits(true);

    }

    private void getSinglePartnerDetails(boolean isLoader, int id, int clubId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfacePartner.getSinglePartnerDetails(id, clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            partner = new Gson().fromJson(data.toString(), Partner.class);
                            populateData(partner);

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

    private void getPartnerProfits(Boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfacePartner.getPartnerProfits(partnerId, "","");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            JSONArray transaction = data.getJSONArray("transactions");
                            transactionList.clear();
                            for (int i = 0; i < transaction.length(); i++) {
                                JSONObject transactionJsonObject = transaction.getJSONObject(i);
                                Transaction transac = new Gson().fromJson(transactionJsonObject.toString(), Transaction.class);
                                transactionList.add(transac);
                            }
                                adapter.notifyDataSetChanged();

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

    private void populateData(Partner partner) {
        if (!partner.getProfilePhoto().isEmpty()){
            Glide.with(getApplicationContext()).load(partner.getProfilePhoto()).apply(RequestOptions.circleCropTransform()).into(binding.imgVu);
        }
        else{
            Glide.with(getApplicationContext()).load(R.drawable.blue_ole_logo).apply(RequestOptions.circleCropTransform()).into(binding.imgVu);
        }

        binding.docCount.setText(String.valueOf(partner.getTotalDocuments()));
        binding.tvName.setText(partner.getName());
        binding.tvPartnerName.setText(partner.getName());
        binding.tvEmail.setText(partner.getEmail());
        binding.tvPhone.setText(partner.getPhoneNumber());
        binding.tvShare.setText(String.format("%s %s", partner.getShares(), partner.getSharesType()));
        binding.tvClubName.setText(partner.getClub().getName());
        binding.tvLoc.setText(String.format("%s %s", partner.getClub().getCity(), partner.getClub().getDistance()));

    }

    protected void showChangeCompanySheet() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment existingFragment = fragmentManager.findFragmentByTag("ChangeCompanyBottomSheet");
        if (existingFragment != null) {
            fragmentTransaction.remove(existingFragment);
        }
        fragmentTransaction.addToBackStack(null);
        ChangeCompanyBottomSheet dialogFragment = new ChangeCompanyBottomSheet(clubId);
        dialogFragment.setDialogCallback((df, isConfirmed, clubId) -> {
            df.dismiss();
            if (isConfirmed){
                assignPartnerClub(true, String.valueOf(partner.getShares()), clubId, null);
            }
        });
        dialogFragment.show(fragmentTransaction, "ChangeCompanyBottomSheet");
    }

    protected void showPaymentSheet(int amount) {
        Locale locale = Locale.getDefault();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", locale);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", locale);

        String month = monthFormat.format(calendar.getTime())+yearFormat.format(calendar.getTime());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment existingFragment = fragmentManager.findFragmentByTag("PaymentBottomSheetFragment");
        if (existingFragment != null) {
            fragmentTransaction.remove(existingFragment);
        }
        fragmentTransaction.addToBackStack(null);
        PaymentBottomSheetFragment dialogFragment = new PaymentBottomSheetFragment(amount, month);
        dialogFragment.setDialogCallback((df, isConfirmed, totalAmount) -> {
            df.dismiss();
            if (isConfirmed){
                payToPartner(Integer.parseInt(totalAmount), partnerId, clubId, "");
            }
        });
        dialogFragment.show(fragmentTransaction, "PaymentBottomSheetFragment");
    }

    private void payToPartner(int amount, int partnerId, String clubId, String month) {
        Call<ResponseBody> call = AppManager.getInstance().apiInterfacePartner.payToPartner(amount, partnerId, clubId, month);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            getPartnerProfits(transactionList.isEmpty());
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

            }
        });
    }

    private void assignPartnerClub(boolean isLoader,  String shareValue, String clubId, File file) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing") : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfacePartner.assignPartnerClub(null,
                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(partnerId)),
                RequestBody.create(MediaType.parse("multipart/form-data"), clubId),
                RequestBody.create(MediaType.parse("multipart/form-data"), shareValue));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), "Partner Assigned To Another Company", FancyToast.SUCCESS);
                            finish();
                        } else {
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
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void deletePartner(int clubId, int partnerId) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfacePartner.deletePartner(clubId, partnerId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), getString(R.string.delete_partner), FancyToast.SUCCESS);
                            finish();
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

    ActivityResultLauncher<Intent> updatePartnerResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
//                boolean isSubscribed = result.getData().getExtras().getBoolean("is_subscribed");
//                if (isSubscribed){
//                    getAllCountries(false);
//                    if(!Functions.getSelectedCountry(getContext(),Constants.kSelectedCountry).isEmpty()){
//                        selectedCountryId = Functions.getSelectedCountry(getContext(),Constants.kSelectedCountry);
//                    }
//                    getTeamAndShirtDetails(true, selectedCountryId);
//
//                }

            }
        }
    });

}