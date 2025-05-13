package ae.oleapp.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.WaitingUserAdapter;
import ae.oleapp.databinding.FragmentShowWaitingUsersListDialogBinding;
import ae.oleapp.models.BookingWaitingList;
import ae.oleapp.models.BookingSlot;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class showWaitingUsersListDialogFragment extends DialogFragment implements View.OnClickListener{

    private FragmentShowWaitingUsersListDialogBinding binding;
    private ResultDialogCallback dialogCallback;
    private String clubId = "";
    private String fieldId = "";
    private String date = "";
    private String startTime = "";
    private String endTime = "";
    private String bookingStatus = "";
    private BookingSlot bookingSlot;
    private WaitingUserAdapter adapter;
    private final List<BookingWaitingList> bookingWaitingLists = new ArrayList<>();


    public showWaitingUsersListDialogFragment() {


    }

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public showWaitingUsersListDialogFragment(String clubId,String fieldId,String date,String startTime,String endTime, String bookingStatus) {
        this.clubId = clubId;
        this.fieldId = fieldId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookingStatus = bookingStatus;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShowWaitingUsersListDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

//        for (int i = 0; i < bookingSlot.getWaitingList().size(); i++){
//            bookingWaitingLists.add(bookingSlot.getWaitingList().get(i));
//        }

//        if (bookingWaitingLists.isEmpty()){
//            binding.empty.setVisibility(View.VISIBLE);
//        }
//        else{
//            binding.empty.setVisibility(View.GONE);
//        }
        getWaitingUserList();
        LinearLayoutManager slotLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(slotLayoutManager);
        adapter = new WaitingUserAdapter(getContext(), bookingWaitingLists, bookingStatus);
        adapter.setItemClickListener(itemClickListner);
        binding.recyclerVu.setAdapter(adapter);

        binding.btnBack.setOnClickListener(this);


        return view;
    }

    WaitingUserAdapter.ItemClickListener itemClickListner = new WaitingUserAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

        }
        @Override
        public void phoneClicked(View view, int pos) {
            makeCall(bookingWaitingLists.get(pos).getUserPhone());
        }
        @Override
        public void bookingClicked(View view, int pos) {
            dialogCallback.didSubmitResult(showWaitingUsersListDialogFragment.this,"booking","", bookingWaitingLists.get(pos).getUserName(), bookingWaitingLists.get(pos).getUserPhone());
        }
        @Override
        public void deleteClicked(View view, int pos) {
            removeWaitingUser(pos);
        }
    };


    private void removeWaitingUser(int pos){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.remove_waiting_title))
                .setMessage(getString(R.string.remove_waiting_user))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogCallback.didSubmitResult(showWaitingUsersListDialogFragment.this,"remove", String.valueOf(bookingWaitingLists.get(pos).getId()), "","");
                        bookingWaitingLists.remove(pos);
                        adapter.notifyDataSetChanged();
                        dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    public void makeCall(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_VIEW);
        callIntent.setData(Uri.parse("tel:" + phone));
        startActivity(callIntent);
    }



    @Override
    public void onClick(View v) {

        if (v == binding.btnBack){
            dismiss();
        }

    }

    private void getWaitingUserList() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getWaitingUserList(clubId, fieldId, date, startTime, endTime);
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
                            bookingWaitingLists.clear();
                            for (int i = 0; i < arr.length(); i++) {
                                bookingWaitingLists.add(gson.fromJson(arr.get(i).toString(), BookingWaitingList.class));
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



    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, String type, String id, String name, String phone);
    }
}