package ae.oleapp.booking.bottomSheets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ae.oleapp.R;
import ae.oleapp.adapters.CountAdapter;
import ae.oleapp.databinding.FragmentBookingConfirmationSheetBinding;
import ae.oleapp.databinding.FragmentConfirmCancelWaitingBookingSheetBinding;
import ae.oleapp.models.BasicBookingDetail;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.OwnerClub;
import ae.oleapp.models.UserInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmCancelWaitingBookingSheetFragment extends DialogFragment implements View.OnClickListener{

    private FragmentConfirmCancelWaitingBookingSheetBinding binding;
    private ResultDialogCallback dialogCallback;
    private String bookingId = "";

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public ConfirmCancelWaitingBookingSheetFragment() {
        // Required empty public constructor
    }

    public ConfirmCancelWaitingBookingSheetFragment(String bookingId) {
        this.bookingId = bookingId;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConfirmCancelWaitingBookingSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getBookingBasicDetail(false, bookingId);

        binding.btnClose.setOnClickListener(this);
        binding.btnWaitingList.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);
        binding.btnCall.setOnClickListener(this);


        return view;
    }

    private void populateData(BasicBookingDetail basicBookingDetail) {
        if (basicBookingDetail.getStatus().equalsIgnoreCase("CONFIRMED")){
            binding.btnConfirm.setVisibility(View.GONE);
        }else {
            binding.btnConfirm.setVisibility(View.VISIBLE);
        }

        binding.tvFieldSize.setText(String.format("%s (%s)", basicBookingDetail.getField().getName(), basicBookingDetail.getField().getSize()));
        binding.tvName.setText(basicBookingDetail.getUserInfo().getName());
        binding.tvTime.setText(String.format("%s (%s)", basicBookingDetail.getTime(), basicBookingDetail.getDuration()));
        binding.tvClubName.setText(basicBookingDetail.getClub().getName());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date dt = dateFormat.parse(basicBookingDetail.getDate());
            dateFormat.applyPattern("EEEE, dd/MM/yyyy");
            binding.tvDayDate.setText(dateFormat.format(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
            dismiss();
        }
        else if (v == binding.btnWaitingList){
            dialogCallback.didWaitingListClicked(this);
        }
        else if (v == binding.btnConfirm){
            dialogCallback.didConfirmClicked(this);
        }
        else if (v == binding.btnCancel){
            dialogCallback.didCancelClicked(this);
        } else if (v == binding.btnCall) {
            dialogCallback.callOption(this);

        }

    }


    private void getBookingBasicDetail(boolean isLoader, String bookingId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getBookingBasicDetail(Integer.parseInt(bookingId));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            JSONObject Club = data.getJSONObject("club");
                            JSONObject Field = data.getJSONObject("field");

                            int clubId = Club.getInt("id");
                            int fieldId = Field.getInt("id");

                            BasicBookingDetail bookingDetail = new Gson().fromJson(data.toString(), BasicBookingDetail.class);
                            bookingDetail.getClub().setId(String.valueOf(clubId));
                            bookingDetail.getField().setId(String.valueOf(fieldId));
                            populateData(bookingDetail);

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

    public interface ResultDialogCallback {
        void didWaitingListClicked(DialogFragment df);
        void didConfirmClicked(DialogFragment df);
        void didCancelClicked(DialogFragment df);
        void callOption(DialogFragment df);
    }
}