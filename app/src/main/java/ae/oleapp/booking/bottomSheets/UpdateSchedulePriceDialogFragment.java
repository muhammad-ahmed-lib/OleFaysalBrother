package ae.oleapp.booking.bottomSheets;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentUpdateSchedulePriceDialogBinding;
import ae.oleapp.models.OleScheduleList;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateSchedulePriceDialogFragment extends DialogFragment {

    private FragmentUpdateSchedulePriceDialogBinding binding;
    private OleScheduleList schedule;
    private String bookingIds = "", callUserId = "";
    private SchedulePriceDialogFragmentCallback fragmentCallback;
    private int countryId, userId;
    private int completed, cancelled;
    private String name = "", type = "";
    private String price = "";



    public UpdateSchedulePriceDialogFragment() {
        // Required empty public constructor
    }

    public UpdateSchedulePriceDialogFragment(OleScheduleList oleScheduleList, String bookingIds) {
        this.schedule = oleScheduleList;
        this.bookingIds = bookingIds;
    }

    public void setFragmentCallback(SchedulePriceDialogFragmentCallback fragmentCallback) {
        this.fragmentCallback = fragmentCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUpdateSchedulePriceDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        String countryCode = schedule.getUser().getCountryCode().replace("+", "");
        int countryCodeInt = Integer.parseInt(countryCode);
        binding.ccp.setCountryForPhoneCode(countryCodeInt);

        binding.tvPrice.setText(String.valueOf(schedule.getPrice()));
        binding.etPhone.setText(schedule.getUser().getNumber());
        binding.tvCurrency.setText(schedule.getCurrency());
        binding.tvCurrency4.setText(schedule.getCurrency());
        binding.tvCurrency5.setText(schedule.getCurrency());
        binding.etName.setText(schedule.getUser().getName());
        price = String.valueOf(schedule.getPrice());
        type = schedule.getUser().getType();

        userId = Integer.parseInt(schedule.getUserId());
        callUserId = String.valueOf(schedule.getCallBookingId());
        countryId = FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());

        if (schedule.getDuration().contains("90")) {
            binding.tvDuration.setText(String.format("%s %s","1.5", getResources().getString(R.string.hour)));
        }
        else if (schedule.getDuration().contains("120")) {
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
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateClicked();
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
        binding.ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryId = FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateClicked() {
        if (binding.etName.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_name), FancyToast.ERROR);
            return;
        }
        if (binding.etPhone.getText().toString().isEmpty()) {
            Functions.showToast(getContext(), getString(R.string.enter_phone), FancyToast.ERROR);
            return;
        }
        updateSchedulePriceAPI(true, binding.etName.getText().toString(), binding.etPhone.getText().toString(), binding.etExtraPrice.getText().toString(), binding.etDiscount.getText().toString());
    }

    private void updateSchedulePriceAPI(boolean isLoader, String name, String phone, String price, String discount) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.updateSchedulePrice(schedule.getClub().getId(), bookingIds, String.valueOf(userId), callUserId, String.valueOf(countryId), phone, name, price, discount);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            fragmentCallback.didUpdatePrice();
                            dismiss();
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

    private void findPhoneDetails(String phone) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.findNumberDetailAPi(phone, String.valueOf(countryId), schedule.getClub().getId());
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
                                name = data.getString("user_name");
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
            binding.tvCurrency.setText(schedule.getCurrency());
            binding.relDiscount.setVisibility(View.VISIBLE);
        }
        else {
            binding.relDiscount.setVisibility(View.GONE);
            double val = Double.parseDouble(binding.etExtraPrice.getText().toString());
            double total = Double.parseDouble(price) + val;
            binding.tvPrice.setText(String.valueOf(total));
            binding.tvCurrency.setText(schedule.getCurrency());
        }
    }

    private void discountTextChanged() {
        binding.etExtraPrice.getText().clear();
        if (binding.etDiscount.getText().toString().isEmpty()) {
            binding.tvPrice.setText(price);
            binding.tvCurrency.setText(schedule.getCurrency());
            binding.relAddPrice.setVisibility(View.VISIBLE);
        }
        else {
            binding.relAddPrice.setVisibility(View.GONE);
            double val = Double.parseDouble(binding.etDiscount.getText().toString());
            double total = Double.parseDouble(price) - val;
            if (val > Double.parseDouble(price)) {
                binding.etDiscount.setText(price);
                binding.tvPrice.setText("0");
                binding.tvCurrency.setText(schedule.getCurrency());
            }
            else {
                binding.tvPrice.setText(String.valueOf(total));
                binding.tvCurrency.setText(schedule.getCurrency());
            }
        }
    }

    public interface SchedulePriceDialogFragmentCallback {
        void didUpdatePrice();
    }
}
