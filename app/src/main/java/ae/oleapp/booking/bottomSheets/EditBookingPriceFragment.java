package ae.oleapp.booking.bottomSheets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.Objects;

import ae.oleapp.R;
import ae.oleapp.adapters.BookingDetailAdapter;
import ae.oleapp.databinding.FragmentEditBookingPriceBinding;
import ae.oleapp.models.FullBookingDetail;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditBookingPriceFragment extends DialogFragment implements View.OnClickListener{

    private FragmentEditBookingPriceBinding binding;
    private ResultDialogCallback dialogCallback;
    private BookingDetailAdapter adapter;
    private FullBookingDetail bookingDetail;
    private int completed, cancelled;
    private String name = "", type = "";
    private int countryId, userId;
    private String price = "";

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public EditBookingPriceFragment() {
        // Required empty public constructor
    }

    public EditBookingPriceFragment(FullBookingDetail bookingDetail) {
        this.bookingDetail = bookingDetail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditBookingPriceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);



        String countryCode = bookingDetail.getUser().getCountryCode().replace("+", "");
        int countryCodeInt = Integer.parseInt(countryCode);
        binding.ccp.setCountryForPhoneCode(countryCodeInt);

        binding.tvPrice.setText(String.valueOf(bookingDetail.getTotalPrice()));
        binding.etPhone.setText(bookingDetail.getUser().getNumber());
        binding.tvCurrency.setText(bookingDetail.getCurrency());
        binding.tvCurrency4.setText(bookingDetail.getCurrency());
        binding.tvCurrency5.setText(bookingDetail.getCurrency());
        binding.etName.setText(bookingDetail.getUser().getName());
        price = String.valueOf(bookingDetail.getTotalPrice());
        type = bookingDetail.getUser().getType();

        userId = Integer.parseInt(bookingDetail.getUser().getId());
        countryId = FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());

        if (bookingDetail.getDuration().contains("90")) {
            binding.tvDuration.setText(String.format("%s %s","1.5", getResources().getString(R.string.hour)));
        }
        else if (bookingDetail.getDuration().contains("120")) {
            binding.tvDuration.setText(String.format("%s %s","2", getResources().getString(R.string.hour)));
        }
        else {
            binding.tvDuration.setText(String.format("%s %s","1", getResources().getString(R.string.hour)));
        }

        binding.etExtraPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addPriceTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phoneNumber = s.toString();
                int desiredLength = 9;
                if (phoneNumber.length() == desiredLength) {
                    findPhoneDetails(binding.etPhone.getText().toString());
                }

            }
        });
        binding.etDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                discountTextChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryId = FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());
            }
        });

        binding.btnClose.setOnClickListener(this);
        binding.btnUpdate.setOnClickListener(this);


        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
            dismiss();
        }
        else if (v == binding.btnUpdate) {
            if (type.equalsIgnoreCase("call")){
                dialogCallback.didSubmitResult(this, String.valueOf(bookingDetail.getId()), binding.etExtraPrice.getText().toString(), binding.etDiscount.getText().toString(), "", String.valueOf(userId));
            }
            else{
                dialogCallback.didSubmitResult(this, String.valueOf(bookingDetail.getId()), binding.etExtraPrice.getText().toString(), binding.etDiscount.getText().toString(), String.valueOf(userId), "");
            }

        }
    }

    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, String id, String price, String discount, String userid, String callUserId);
    }

    private void findPhoneDetails(String phone) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.findNumberDetailAPi(phone, String.valueOf(countryId), bookingDetail.getClub().getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            if (data.length() > 0) {
                                userId = data.getInt("id");
                                name = data.getString("user_name"); //data.optString("name", "");
                                type = data.getString("type");
                                completed = data.getInt("completed_bookings");
                                cancelled = data.getInt("canceled_bookings");
                                binding.etName.setText(name);
                                binding.tvCompCount.setText(String.valueOf(completed));
                                binding.tvCancCount.setText(String.valueOf(cancelled));
                                binding.relCcLay.setVisibility(View.VISIBLE);
                                binding.newPlayerLabel.setVisibility(View.GONE);

                            } else {

                                binding.newPlayerLabel.setVisibility(View.VISIBLE);
                                binding.tvCompCount.setText("");
                                binding.tvCancCount.setText("");
                                binding.relCcLay.setVisibility(View.GONE);
                                binding.newPlayerLabel.setVisibility(View.VISIBLE);
                            }
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

    protected int FindCountryId(String selectedCountryCode){
        for (int i = 0; i < AppManager.getInstance().countryPhoneLists.size(); i++) {
            if (selectedCountryCode.equalsIgnoreCase(AppManager.getInstance().countryPhoneLists.get(i).getCode())) {
                countryId = AppManager.getInstance().countryPhoneLists.get(i).getId();
                break;
            }
        }
        return countryId;
    }

    private void addPriceTextChanged() {
        binding.etDiscount.getText().clear();
        if (binding.etExtraPrice.getText().toString().isEmpty()) {
            binding.tvPrice.setText(price);
            binding.tvCurrency.setText(bookingDetail.getCurrency());
            binding.relDiscount.setVisibility(View.VISIBLE);
        }
        else {
            binding.relDiscount.setVisibility(View.GONE);
            double val = Double.parseDouble(binding.etExtraPrice.getText().toString());
            double total = Double.parseDouble(price) + val;
            binding.tvPrice.setText(String.valueOf(total));
            binding.tvCurrency.setText(bookingDetail.getCurrency());
        }
    }

    private void discountTextChanged() {
        binding.etExtraPrice.getText().clear();
        if (binding.etDiscount.getText().toString().isEmpty()) {
            binding.tvPrice.setText(price);
            binding.tvCurrency.setText(bookingDetail.getCurrency());
            binding.relAddPrice.setVisibility(View.VISIBLE);
        }
        else {
            binding.relAddPrice.setVisibility(View.GONE);
            double val = Double.parseDouble(binding.etDiscount.getText().toString());
            double total = Double.parseDouble(price) - val;
            if (val > Double.parseDouble(price)) {
                binding.etDiscount.setText(price);
                binding.tvPrice.setText("0");
                binding.tvCurrency.setText(bookingDetail.getCurrency());
            }
            else {
                binding.tvPrice.setText(String.valueOf(total));
                binding.tvCurrency.setText(bookingDetail.getCurrency());
            }
        }
    }

}