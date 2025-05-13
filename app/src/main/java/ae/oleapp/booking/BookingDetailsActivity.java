package ae.oleapp.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONObject;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import ae.oleapp.R;
import ae.oleapp.adapters.CancellationHistoryAdapter;
import ae.oleapp.adapters.OleFacilityAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.booking.balanceHistory.BalanceActivity;
import ae.oleapp.booking.bottomSheets.BookingDetailSheetFragment;
import ae.oleapp.booking.bottomSheets.CancelBookingSheetFragment;
import ae.oleapp.booking.bottomSheets.EditBookingPriceFragment;
import ae.oleapp.booking.bottomSheets.TotalAmountSheetFragment;
import ae.oleapp.databinding.ActivityBookingDetailsBinding;
import ae.oleapp.hire.HirePlayerActivity;
import ae.oleapp.models.CancelledHistory;
import ae.oleapp.models.FullBookingDetail;
import ae.oleapp.models.Hiring;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.PlayerSkill;
import ae.oleapp.owner.OleFastBookingActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ActivityBookingDetailsBinding binding;
    private int bookingId;
    private FullBookingDetail bookingDetail;
    private CancellationHistoryAdapter cancellationHistoryAdapter;
    private OleFacilityAdapter oleFacilityAdapter;
    private final List<CancelledHistory> cancelledHistoryList =  new ArrayList<>();
    private final List<OleClubFacility> oleClubFacilityList =  new ArrayList<>();
    private final List<Hiring> goalKeeperList =  new ArrayList<>();
    private Hiring referee;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            bookingId = bundle.getInt("booking_id");
        }

        getBookingFullDetail(true, bookingId);

        LinearLayoutManager cancellationLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.cancellationHistoryRecyclerVu.setLayoutManager(cancellationLayoutManager);
        cancellationHistoryAdapter = new CancellationHistoryAdapter(getContext(), cancelledHistoryList);
        cancellationHistoryAdapter.setItemClickListener(itemClickListener);
        binding.cancellationHistoryRecyclerVu.setAdapter(cancellationHistoryAdapter);

        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.facilityRecyclerVu.setLayoutManager(facLayoutManager);
        oleFacilityAdapter = new OleFacilityAdapter(getContext(), oleClubFacilityList, true);
        binding.facilityRecyclerVu.setAdapter(oleFacilityAdapter);

        // oleFacilityAdapter.setOnItemClickListener(facClickListener);

        binding.btnBack.setOnClickListener(this);
        binding.btnCall.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.bookingInfo.setOnClickListener(this);
        binding.btnChangeTime.setOnClickListener(this);
        binding.taInfo.setOnClickListener(this);
        binding.tpInfo.setOnClickListener(this);
        binding.ppInfo.setOnClickListener(this);
        binding.discountInfo.setOnClickListener(this);
        binding.relGk1.setOnClickListener(this);
        binding.relGk2.setOnClickListener(this);
        binding.relRef.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.btnBalance.setOnClickListener(this);

        binding.btnGk1Call.setOnClickListener(this);
        binding.btnGk2Call.setOnClickListener(this);
        binding.btnRefCall.setOnClickListener(this);

        binding.btnGk1Delete.setOnClickListener(this);
        binding.btnGk2Delete.setOnClickListener(this);
        binding.btnRefDelete.setOnClickListener(this);

        binding.btnChangeGk1.setOnClickListener(this);
        binding.btnChangeGk2.setOnClickListener(this);
        binding.btnChangeRef.setOnClickListener(this);

    }

    CancellationHistoryAdapter.ItemClickListener itemClickListener = (view, pos) -> {

    };

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.btnCall) {
            makeCall(bookingDetail.getUser().getPhone());
        }
        else if (v == binding.btnContinue) {
            continueClicked();

        } else if (v == binding.btnCancel) {
            showBookingCancellationDetails(String.valueOf(bookingDetail.getId()));
        }
        else if (v == binding.bookingInfo) {
            showBookingSheetDetails();
        }
        else if (v == binding.btnChangeTime) {
            changeTime();
        }
        else if (v == binding.taInfo) {
            showAmountDetails(true);
        }
        else if (v == binding.tpInfo) {
            showAmountDetails(false);
        }
        else if (v == binding.ppInfo) {

        }
        else if (v == binding.discountInfo) {

        }
        else if (v == binding.relGk1 || v == binding.relGk2) {
            Intent intent = new Intent(getContext(), HirePlayerActivity.class);
            intent.putExtra("skill", "GOALKEEPER");
            intent.putExtra("booking_id", String.valueOf(bookingDetail.getId()));
            startActivity(intent);
        }
        else if (v == binding.relRef) {
            Intent intent = new Intent(getContext(), HirePlayerActivity.class);
            intent.putExtra("skill", "REFEREE");
            intent.putExtra("booking_id", String.valueOf(bookingDetail.getId()));
            startActivity(intent);
        }
        else if (v == binding.btnEdit) {
            editBookingDetails();
        }
        else if (v == binding.btnBalance) {
            balanceClicked();
        }
        else if (v == binding.btnGk1Call) {
//            makeCall();
        }
        else if (v == binding.btnGk2Call) {
        }
        else if (v == binding.btnRefCall) {
        }
        else if (v == binding.btnGk1Delete) {
            removeHiring(bookingDetail.getId(), goalKeeperList.get(0).getId());
        }
        else if (v == binding.btnGk2Delete) {
            removeHiring(bookingDetail.getId(), goalKeeperList.get(1).getId());
        }
        else if (v == binding.btnRefDelete) {
            removeHiring(bookingDetail.getId(), referee.getId());
        }
        else if (v == binding.btnChangeGk1) {
        }
        else if (v == binding.btnChangeGk2) {
        }
        else if (v == binding.btnChangeRef) {
        }

    }

    private void balanceClicked() {
        Intent intent = new Intent(getContext(), BalanceActivity.class);
        intent.putExtra("club_id", bookingDetail.getClub().getId());
        intent.putExtra("type", bookingDetail.getUser().getType());
        intent.putExtra("id", bookingDetail.getUser().getId());
        startActivity(intent);
    }

    private void changeTime() {
        Intent intent = new Intent(getContext(), OleFastBookingActivity.class);
        intent.putExtra("is_padel", false);
        intent.putExtra("index", 0);
        intent.putExtra("booking_id", bookingDetail.getId());
        intent.putExtra("name", bookingDetail.getUser().getName());
        intent.putExtra("number", bookingDetail.getUser().getNumber());
        intent.putExtra("countryId", bookingDetail.getUser().getCountryId());
        intent.putExtra("change_time", true);
        startActivity(intent);
    }

    private void continueClicked() {
        if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kPendingBooking)) {
            confirmBooking(bookingId);
        }
        else if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kConfirmedBooking)) {
            openCompleteDialog();
        }
        else if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kCompletedBooking)) {

        }
    }

    private void openCompleteDialog() {
        Intent intent = new Intent(getContext(), CompleteBookingActivity.class);
        intent.putExtra("price", bookingDetail.getTotalPrice());
        intent.putExtra("currency", bookingDetail.getCurrency());
        intent.putExtra("duration", bookingDetail.getDuration());
        intent.putExtra("id", bookingDetail.getId());
        fillBookingCompleteDetailResultLauncher.launch(intent);

        //  Intent intent = new Intent(getContext(), CompleteBookingActivity.class);
        //  intent.putExtra("price", bookingDetail.getTotalPrice());
        //  intent.putExtra("currency", bookingDetail.getCurrency());
        //  intent.putExtra("duration", bookingDetail.getDuration());
        //  intent.putExtra("id", bookingDetail.getId());
        //  startActivityForResult(intent, 1000);
    }

    //    @Override
        //    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //        super.onActivityResult(requestCode, resultCode, data);
        //        if (resultCode == RESULT_OK && requestCode == 1000) {
        //            Bundle bundle = data.getExtras();
        //            String note = bundle.getString("note");
        //            String discount = bundle.getString("discount");
        //            String invoiceNo = bundle.getString("invoiceNo");
        //            String extraTime = bundle.getString("extraTime");
        //            String price = bundle.getString("price");
        //            String balance = bundle.getString("balance");
        //            String posPayment = bundle.getString("posPayment");
        ////            String filePath = bundle.getString("filePath");
        //            completeBooking(true , String.valueOf(bookingDetail.getId()), balance, price, discount, posPayment);
        //        }
        //    }

    private void getBookingFullDetail(boolean isLoader, int bookingId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getBookingFullDetail(bookingId);
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
                            JSONObject User = data.getJSONObject("user");

                            int clubId = Club.getInt("id");
                            int fieldId = Field.getInt("id");
                            int userId = User.getInt("id");

                            bookingDetail = new Gson().fromJson(data.toString(), FullBookingDetail.class);
                            bookingDetail.getClub().setId(String.valueOf(clubId));
                            bookingDetail.getField().setId(String.valueOf(fieldId));
                            bookingDetail.getUser().setId(String.valueOf(userId));

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

    private void populateData(FullBookingDetail bookingDetail){
        if (!bookingDetail.getUser().getPicture().isEmpty()){
            Glide.with(getApplicationContext()).load(bookingDetail.getUser().getPicture()).into(binding.emojiImgVu);
        }
        if (!bookingDetail.getUser().getShirt().isEmpty()){
            Glide.with(getApplicationContext()).load(bookingDetail.getUser().getShirt()).into(binding.shirtImgVu);
        }
        binding.tvUserName.setText(bookingDetail.getUser().getName());

        labelBookingStatus(bookingDetail);

        binding.tvFieldSize.setText(bookingDetail.getField().getSize());
        binding.tvFieldName.setText(bookingDetail.getField().getName());
        binding.tvBookingsDate.setText(bookingDetail.getDate());
        binding.tvTimeDuration.setText(bookingDetail.getTime());
        binding.tvClubName.setText(bookingDetail.getClub().getName());
        binding.tvDur.setText(bookingDetail.getDuration());

        if (bookingDetail.getBy().equalsIgnoreCase("OWNER")){
            binding.tvBookedBy.setText("Owner Booked");
        }
        else if (bookingDetail.getBy().equalsIgnoreCase("PLAYER")) {
            binding.tvBookedBy.setText("Player Booked");

        }
        else{
            binding.tvBookedBy.setText("Employee Booked");
        }

        binding.tvTotalAmt.setText(String.format("%s %s", bookingDetail.getTotalPrice(), bookingDetail.getCurrency()));
        binding.tvTotalPaid.setText(String.format("%s %s", bookingDetail.getTotalPaid(), bookingDetail.getCurrency()));
        binding.tvPlayerPaid.setText(String.format("%s %s", 0, bookingDetail.getCurrency()));
        binding.tvDiscount.setText(String.format("%s %s", bookingDetail.getTotalDiscount(), bookingDetail.getCurrency()));

        binding.tvTotalBalance.setText(String.valueOf(bookingDetail.getTotalBalance()));

        binding.tvCompletedCount.setText(String.valueOf(bookingDetail.getBookingsDetails().getCompleted()));
        binding.tvCancelledCount.setText(String.valueOf(bookingDetail.getBookingsDetails().getCanceled()));
        binding.tvUpcomingCount.setText(String.valueOf(bookingDetail.getBookingsDetails().getPending()));
        binding.tvCurrMonthCount.setText(String.valueOf(bookingDetail.getBookingsDetails().getCurrentMonth()));

        binding.tvFinalPrice.setText(String.valueOf(bookingDetail.getTotalPrice()));
        binding.tvFinalCurrency.setText(bookingDetail.getCurrency());

        if (!bookingDetail.getHirings().isEmpty()){
            for (Hiring hiring : bookingDetail.getHirings()){
                if (hiring.getRole().equalsIgnoreCase("GOALKEEPER")){
                    goalKeeperList.add(hiring);
                    populateGoalKeeper(hiring);
                }
                else{
                    referee = hiring;
                    populateReferee(referee);
                }
            }

        }

        cancelledHistoryList.addAll(bookingDetail.getCancelledHistory());
        oleClubFacilityList.addAll(bookingDetail.getFacilities());

        cancellationHistoryAdapter.notifyDataSetChanged();
        oleFacilityAdapter.notifyDataSetChanged();

    }

    private void populateGoalKeeper(Hiring hiring) {
        if (binding.gk1Plus.getVisibility() == View.VISIBLE){
            binding.gk1Plus.setVisibility(View.GONE);
            binding.gk1InfoLay.setVisibility(View.VISIBLE);
            binding.gk1Actions.setVisibility(View.VISIBLE);

            Glide.with(getApplicationContext()).load(hiring.getUser().getPicture()).into(binding.gk1EmojiImgVu);
            Glide.with(getApplicationContext()).load(hiring.getUser().getShirt()).into(binding.gk1ShirtImgVu);
            binding.tvGk1Name.setText(hiring.getUser().getName());
            binding.tvGk1Price.setText(String.format("%s %s", hiring.getAmount(), hiring.getUser().getCurrency()));
        }
        else if (binding.gk2Plus.getVisibility() == View.VISIBLE){

            binding.gk2Plus.setVisibility(View.GONE);
            binding.gk2InfoLay.setVisibility(View.VISIBLE);
            binding.gk2Actions.setVisibility(View.VISIBLE);

            Glide.with(getApplicationContext()).load(hiring.getUser().getPicture()).into(binding.gk2EmojiImgVu);
            Glide.with(getApplicationContext()).load(hiring.getUser().getShirt()).into(binding.gk2ShirtImgVu);
            binding.tvGk2Name.setText(hiring.getUser().getName());
            binding.tvGk2Price.setText(String.format("%s %s", hiring.getAmount(), hiring.getUser().getCurrency()));
        }
    }

    private void populateReferee(Hiring hiring) {
        binding.refPlus.setVisibility(View.GONE);
        binding.refInfoLay.setVisibility(View.VISIBLE);
        binding.refActions.setVisibility(View.VISIBLE);

        Glide.with(getApplicationContext()).load(hiring.getUser().getPicture()).into(binding.refEmojiImgVu);
        Glide.with(getApplicationContext()).load(hiring.getUser().getShirt()).into(binding.refShirtImgVu);
        binding.tvRefName.setText(hiring.getUser().getName());
        binding.tvRefPrice.setText(String.format("%s %s", hiring.getAmount(), hiring.getUser().getCurrency()));

    }

    private void labelBookingStatus(FullBookingDetail bookingDetail) {
        if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kPendingBooking)) {

            binding.bookingStatusIc.setImageResource(R.drawable.v5_sand_ic);
            binding.tvBookingStatus.setText(bookingDetail.getStatus().toLowerCase(Locale.ENGLISH));
            binding.tvBookingStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.redBookingColor));
            binding.bookingStatusLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.redBookingColorOpacity));
            binding.tvBtnContinue.setText(getString(R.string.confirm));


            binding.paidUnpaidBg.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.redBookingColorOpacity));
            binding.isPaidIc.setImageResource(R.drawable.balance_ic);
            binding.tvIsPaid.setTextColor(ContextCompat.getColor(getContext(), R.color.redBookingColor));
            binding.tvIsPaid.setText("Unpaid");

        }
        else if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kConfirmedBooking)) {
            binding.bookingStatusIc.setImageResource(R.drawable.booking_confirmed_ic);
            binding.tvBookingStatus.setText(bookingDetail.getStatus().toLowerCase(Locale.ENGLISH));
            binding.tvBookingStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.blueColorNew));
            binding.bookingStatusLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5grayColor));
            binding.tvBtnContinue.setText(getString(R.string.complete));

            binding.paidUnpaidBg.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.redBookingColorOpacity));
            binding.isPaidIc.setImageResource(R.drawable.balance_ic);
            binding.tvIsPaid.setTextColor(ContextCompat.getColor(getContext(), R.color.redBookingColor));
            binding.tvIsPaid.setText("Unpaid");

        }
        else if (bookingDetail.getStatus().equalsIgnoreCase(Constants.kCompletedBooking)) {
            binding.bookingStatusIc.setImageResource(R.drawable.thumbs_up_ic);
            binding.tvBookingStatus.setText(bookingDetail.getStatus().toLowerCase(Locale.ENGLISH));
            binding.tvBookingStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
            binding.bookingStatusLay.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5grayColor));
            binding.tvBtnContinue.setText(getString(R.string.completed));
            binding.btnContinue.setEnabled(false);
            binding.btnCancel.setVisibility(View.GONE);
            binding.btnChangeTime.setVisibility(View.GONE);

            binding.paidUnpaidBg.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.greenBookingColorOpacity));
            binding.isPaidIc.setImageResource(R.drawable.balance_green__ic);
            binding.tvIsPaid.setTextColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
            binding.tvIsPaid.setText("paid");

        }
        else {
//            holder.confirmTag.setVisibility(View.GONE);
        }
    }

    protected void showAmountDetails(Boolean totalAmount) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("TotalAmountSheetFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        TotalAmountSheetFragment dialogFragment = new TotalAmountSheetFragment(bookingDetail.getPrices(), bookingDetail.getPayments(), totalAmount);
        dialogFragment.setDialogCallback(DialogFragment::dismiss);
        dialogFragment.show(fragmentTransaction, "TotalAmountSheetFragment");
    }

    protected void showBookingCancellationDetails(String slotId) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("CancelBookingSheetFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        CancelBookingSheetFragment dialogFragment = new CancelBookingSheetFragment();
        dialogFragment.setDialogCallback((df, cancel,  note) -> {
            df.dismiss();
            if (cancel){
                cancelBooking(slotId, note);
            }
        });
        dialogFragment.show(fragmentTransaction, "CancelBookingSheetFragment");
    }

    protected void showBookingSheetDetails() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("BookingDetailSheetFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        BookingDetailSheetFragment dialogFragment = new BookingDetailSheetFragment(bookingDetail.getLogs());
        dialogFragment.setDialogCallback((df) -> {
            df.dismiss();
        });
        dialogFragment.show(fragmentTransaction, "BookingDetailSheetFragment");
    }

    private void cancelBooking(String id, String note) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.cancelBooking(id, note);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            bookingDetail.setStatus(Constants.kCancelledBooking);
                            labelBookingStatus(bookingDetail);
                            finish();
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

    private void confirmBooking(int id) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.confirmBooking(id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            bookingDetail.setStatus(Constants.kConfirmedBooking);
                            labelBookingStatus(bookingDetail);
                            finish();
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

    private void completeBooking(boolean isLoader, String id, String balanceAmount, String extraAmount, String discountAmount, String posAmount) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.completeBooking(id,balanceAmount,extraAmount,discountAmount,posAmount);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            Intent intent = new Intent(getContext(), BookingCompletedActivity.class);
                            bookingDetail.setStatus(Constants.kCompletedBooking);
                            labelBookingStatus(bookingDetail);
                            bookingCompletedResultLauncher.launch(intent);
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

    private void editBookingPrice(boolean isLoader, String id, String price, String discount, String userid, String callUserId) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.editBookingPrice(id, price, discount, userid, callUserId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            finish();
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

    ActivityResultLauncher<Intent> bookingCompletedResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                assert result.getData() != null;
                boolean homeClicked = Objects.requireNonNull(result.getData().getExtras()).getBoolean("home_clicked");
                if (homeClicked){
                    finish();
                }
            }
        }
    });

    ActivityResultLauncher<Intent> fillBookingCompleteDetailResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                assert result.getData() != null;

                Bundle bundle = result.getData().getExtras();
                String note = bundle.getString("note");
                String discount = bundle.getString("discount");
                String invoiceNo = bundle.getString("invoiceNo");
                String extraTime = bundle.getString("extraTime");
                String price = bundle.getString("price");
                String balance = bundle.getString("balance");
                String posPayment = bundle.getString("posPayment");
//            String filePath = bundle.getString("filePath");
                completeBooking(true , String.valueOf(bookingDetail.getId()), balance, price, discount, posPayment);
            }
        }
    });

    protected void editBookingDetails() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("EditBookingPriceFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        EditBookingPriceFragment dialogFragment = new EditBookingPriceFragment(bookingDetail);
        dialogFragment.setDialogCallback((df, id, price, discount, userid, callUserId) -> {
            df.dismiss();
            editBookingPrice(true, id, price, discount, userid, callUserId);
        });
        dialogFragment.show(fragmentTransaction, "EditBookingPriceFragment");
    }

    private void removeHiring(int bookingId, int skillId) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.removeHiring(bookingId, skillId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), getString(R.string.success), FancyToast.SUCCESS);
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


}