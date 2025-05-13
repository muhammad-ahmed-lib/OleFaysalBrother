package ae.oleapp.fragments;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.WaitingUserAdapter;
import ae.oleapp.databinding.FragmentAddWaitingUserDialogBinding;
import ae.oleapp.databinding.FragmentIncomeHistoryBottomSheetDialogBinding;
import ae.oleapp.models.BookingSlot;
import ae.oleapp.models.BookingWaitingList;
import ae.oleapp.models.CountryPhoneList;
import ae.oleapp.models.IncomeDetailsModel;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddWaitingUserDialogFragment extends DialogFragment implements View.OnClickListener{

    private FragmentAddWaitingUserDialogBinding binding;
    private ResultDialogCallback dialogCallback;
    private String bookingId = "";
    private String countryId = "";
    private String clubId = "";
    private String fieldId = "";
    private String date = "";
    private String startDate = "";
    private String endDate = "";
    private List<CountryPhoneList> countryList = new ArrayList<>();
    private BookingSlot bookingSlot;
    private WaitingUserAdapter adapter;
    private final List<BookingWaitingList> bookingWaitingLists = new ArrayList<>();





    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public AddWaitingUserDialogFragment() {
        // Required empty public constructor
    }


    public AddWaitingUserDialogFragment(String bookingId, String date,String startDate,String endDate,String clubId,String fieldId) {
        this.bookingId = bookingId;
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
        this.clubId = clubId;
        this.fieldId = fieldId;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddWaitingUserDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getWaitingUserList();

        LinearLayoutManager slotLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(slotLayoutManager);
        adapter = new WaitingUserAdapter(getContext(), bookingWaitingLists, "");
        adapter.setItemClickListener(itemClickListner);
        binding.recyclerVu.setAdapter(adapter);

        countryList = AppManager.getInstance().countryPhoneLists;
        countryId =  FindCountryId(binding.ccp.getSelectedCountryCodeWithPlus());



        binding.btnBack.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);


        return view;
    }

    WaitingUserAdapter.ItemClickListener itemClickListner = new WaitingUserAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

        }
        @Override
        public void phoneClicked(View view, int pos) {
//            makeCall(bookingWaitingLists.get(pos).getUserPhone());
        }
        @Override
        public void bookingClicked(View view, int pos) {
//            dialogCallback.didSubmitResult(this,"booking","", bookingWaitingLists.get(pos).getUserName(), bookingWaitingLists.get(pos).getUserPhone());
        }
        @Override
        public void deleteClicked(View view, int pos) {
//            removeWaitingUser(pos);
        }
    };


    protected String FindCountryId(String selectedCountryCode){
        for (int i = 0; i < AppManager.getInstance().countryPhoneLists.size(); i++) {
            if (selectedCountryCode.equalsIgnoreCase(AppManager.getInstance().countryPhoneLists.get(i).getCode())) {
                countryId = String.valueOf(AppManager.getInstance().countryPhoneLists.get(i).getId());
                break;
            }
        }
        return countryId;
    }



    @Override
    public void onClick(View v) {

        if (v == binding.btnBack){
            dismiss();
        }
        else if (v == binding.btnAdd){
            if (binding.etName.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Name cannot be empty!", FancyToast.ERROR);
                return;
            }
            if (binding.etPhone.getText().toString().isEmpty()){
                Functions.showToast(getContext(), "Phone number cannot be empty!", FancyToast.ERROR);
                return;
            }
            addUserToWaitingList(bookingId, clubId, binding.etName.getText().toString(), binding.etPhone.getText().toString());
        }

    }

    private void addUserToWaitingList(String bookingId, String clubId, String name, String phone) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.addUserToWaitingList(bookingId,clubId, name, phone, countryId, fieldId, date, startDate, endDate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), getString(R.string.success), FancyToast.SUCCESS);
//                            getSlotsAPI(selectedDate);
//                            dialogCallback.didSubmitResult(this, );

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

    private void getWaitingUserList() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getWaitingUserList(clubId, fieldId, date, startDate, endDate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), getString(R.string.success), FancyToast.SUCCESS);

//                            getSlotsAPI(selectedDate);
//                            dialogCallback.didSubmitResult(this, );

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




    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, String name, String phone);
    }
}