package ae.oleapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import ae.oleapp.R;
import ae.oleapp.adapters.BookingClubNameAdapter;
import ae.oleapp.adapters.OleBookingDateAdapter;
import ae.oleapp.adapters.OleBookingFieldAdapter;
import ae.oleapp.adapters.OleBookingListAdapter;
import ae.oleapp.base.BaseFragment;
import ae.oleapp.base.BaseTabActivity;
import ae.oleapp.booking.BookingDetailsActivity;
import ae.oleapp.databinding.OlefragmentBookingListBinding;
import ae.oleapp.models.BasicBookingDetail;
import ae.oleapp.models.OleBookingListDate;
import ae.oleapp.models.Club;
import ae.oleapp.models.Field;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleBookingListFragment extends BaseFragment implements View.OnClickListener {

    private OlefragmentBookingListBinding binding;
    private final String kBookDateFormat = "dd/MM/yyyy";
    private String fromDate = "";
    private String prevNextDate = "";
    private String toDate = "";
    private int selectedDateIndex = 1;
    private final List<OleBookingListDate> arrDate = new ArrayList<>();
    private final List<Field> fieldList = new ArrayList<>();
    private String bookingStatus = "";
    private OleBookingDateAdapter oleBookingDateAdapter;
    private OleBookingListAdapter oleBookingListAdapter;
    private String clubId = "";
    private String fieldId = "";
    private final List<BasicBookingDetail> oleBookingList = new ArrayList<>();
    private final List<BasicBookingDetail> cancelledList = new ArrayList<>();
    private final List<BasicBookingDetail> previousBookingsList = new ArrayList<>();
    private BookingClubNameAdapter clubAdapter;
    private final List<Club> clubList = new ArrayList<>();
    private OleBookingFieldAdapter fieldAdapter;
    private Animation hitAnimation, hitAnimation2;
    private final String lastWeekDate = "";
    private final String kBookingFormat = "yyyy-MM-dd";
    private String selectedDate = "";
    private String TAG = "OleBookingListFragmentInfo";


    public OleBookingListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = OlefragmentBookingListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Log.d(TAG, "onCreateView:Home Real ");
        LinearLayoutManager clubLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        binding.clubRecyclerVu.setLayoutManager(clubLayoutManager);
        clubAdapter = new BookingClubNameAdapter(getActivity(), clubList, selectedDateIndex, false);
        clubAdapter.setOnItemClickListener(clubClickListener);
        binding.clubRecyclerVu.setAdapter(clubAdapter);

        LinearLayoutManager horizLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        binding.dateRecyclerVu.setLayoutManager(horizLayoutManager);
        oleBookingDateAdapter = new OleBookingDateAdapter(getContext(), arrDate.toArray(), 0);
        oleBookingDateAdapter.setOnItemClickListener(clickListener);
        binding.dateRecyclerVu.setAdapter(oleBookingDateAdapter);

        LinearLayoutManager vertLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        binding.listRecyclerVu.setLayoutManager(vertLayoutManager);
        oleBookingListAdapter = new OleBookingListAdapter(getActivity(), oleBookingList);
        oleBookingListAdapter.setItemClickListener(itemClickListener);
        binding.listRecyclerVu.setAdapter(oleBookingListAdapter);

        selectedDateIndex = 0;
        fromDate = getDateStr(new Date());
        bookingClicked();

        UserInfo userInfo = Functions.getUserinfo(getContext());
        if (userInfo.getPicture().isEmpty()){
            Glide.with(getContext())
                    .load(R.drawable.partner_temp_img)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.profileImgVu);
        }else{
            Glide.with(getContext())
                    .load(userInfo.getPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.profileImgVu);
        }


        binding.relBooking.setOnClickListener(this);
        binding.relCancelled.setOnClickListener(this);
        binding.relPastBookings.setOnClickListener(this);
        binding.btnPrev.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        setBadgeValue();
        fromDate = getDateStr(new Date());
        getOwnerClubList(false);

    }

    private String getDateStr(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(kBookingFormat, Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat.format(date);
    }

    BookingClubNameAdapter.OnItemClickListener clubClickListener = new BookingClubNameAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            clubAdapter.setSelectedIndex(pos);
            Club club = clubList.get(pos);
            clubId = club.getId();
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constants.kOwnerBookingSelectedClub, clubId);
            editor.apply();
            fieldId = "";
            prevNextDate = getDateStr(new Date());
            if (clubId.isEmpty()){
                getBookingsAPI(true, "", getDateStr(new Date()), "", "");
            }
            else{
                getBookingsAPI(true, clubId, getDateStr(new Date()), "", "");
            }
        }
    };

    OleBookingDateAdapter.OnItemClickListener clickListener = new OleBookingDateAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            selectedDateIndex = pos;
            fromDate = arrDate.get(pos).getDate();
            oleBookingDateAdapter.setSelectedDateIndex(selectedDateIndex);
            toDate = "";
            prevNextDate = fromDate;
            getBookingsAPI(true, clubId, fromDate, "" , "");
        }
    };

    OleBookingListAdapter.ItemClickListener itemClickListener = new OleBookingListAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {

            BasicBookingDetail booking;
            if (bookingStatus.equalsIgnoreCase("booking")) {
                booking = oleBookingList.get(pos);
            } else if (bookingStatus.equalsIgnoreCase("cancelled")){
                booking = cancelledList.get(pos);
            }else{
                booking = previousBookingsList.get(pos);
            }

//            Intent intent = new Intent(getContext(), OleBookingDetailActivity.class);
            Intent intent = new Intent(getContext(), BookingDetailsActivity.class);
            intent.putExtra("booking_id", booking.getId());
            startActivity(intent);
        }

        @Override
        public void OnItemLongClick(View v, int pos) {
            BasicBookingDetail booking;
            if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
                if (bookingStatus.equalsIgnoreCase("booking")) {
                    booking = oleBookingList.get(pos);
                    if (booking.getStatus().equalsIgnoreCase("0") || booking.getStatus().equalsIgnoreCase("1") || booking.getStatus().equalsIgnoreCase("2")) {
//                        showWaitingUserSheet(booking.getId(), booking.getClubId());
                    }
                }

            }
        }

        @Override
        public void callClicked(View view, int pos) {
            if (bookingStatus.equalsIgnoreCase("booking")) {
                ((BaseTabActivity) requireActivity()).makeCall(oleBookingList.get(pos).getUserInfo().getPhone());
            } else if (bookingStatus.equalsIgnoreCase("cancelled")){
                ((BaseTabActivity) requireActivity()).makeCall(cancelledList.get(pos).getUserInfo().getPhone());
            }else{
                ((BaseTabActivity) requireActivity()).makeCall(previousBookingsList.get(pos).getUserInfo().getPhone());
            }

        }
    };


//    protected void showWaitingUserSheet(String bookingId, String clubId) {
//        FragmentManager fragmentManager = getParentFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        // Remove existing fragment with the same tag, if any
//        Fragment existingFragment = fragmentManager.findFragmentByTag("AddWaitingUserDialogFragment");
//        if (existingFragment != null) {
//            fragmentTransaction.remove(existingFragment);
//        }
//
//        // Add transaction to backstack if necessary
//        fragmentTransaction.addToBackStack(null);
//
//        // Create and show the new dialog fragment
//        AddWaitingUserDialogFragment dialogFragment = new AddWaitingUserDialogFragment("","", "", "", "", "");
//        dialogFragment.setDialogCallback((df, name, phone) -> {
//            df.dismiss();
////            addUserToWaitingList(bookingId, clubId, name, phone);
//        });
//        dialogFragment.show(fragmentTransaction, "AddWaitingUserDialogFragment");
//    }

    @Override
    public void onClick(View v) {
        if (v == binding.relMenu) {
            menuClicked();
        }
//        else if (v == binding.relCalendar) {
//            calendarClicked();
//        }
        else if (v == binding.relNotif) {
            notifClicked();
        }
        else if (v == binding.relBooking) {
            binding.dateRecyclerVu.setVisibility(View.VISIBLE);
            binding.pastBookingLay.setVisibility(View.GONE);

            bookingClicked();
        }
        else if (v == binding.relCancelled) {
            binding.dateRecyclerVu.setVisibility(View.VISIBLE);
            binding.pastBookingLay.setVisibility(View.GONE);

            cancelledClicked();
        }
        else if (v == binding.relPastBookings) {

            binding.dateRecyclerVu.setVisibility(View.GONE);
            binding.pastBookingLay.setVisibility(View.VISIBLE);

            previousBookings(prevNextDate);

        }
        else if (v == binding.btnPrev) {
            hitAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.hit_animation);
            binding.prevNextSingleDate.startAnimation(hitAnimation);

            hitAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.prevNextSingleDate.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            prevNextDate = getPrevNextDate(0, prevNextDate);
            previousBookings(prevNextDate);


        }
        else if (v == binding.btnNext) {
            hitAnimation2 = AnimationUtils.loadAnimation(getContext(), R.anim.hit_animation_rtl);
            binding.prevNextSingleDate.startAnimation(hitAnimation2);
            hitAnimation2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.prevNextSingleDate.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            prevNextDate = getPrevNextDate(1, prevNextDate);
            previousBookings(prevNextDate);
        }
    }

    private void menuClicked() {
        if (getActivity() instanceof OleOwnerMainTabsActivity) {
            ((OleOwnerMainTabsActivity) getActivity()).menuClicked();
        }
    }

    private void notifClicked() {
        if (getActivity() instanceof OleOwnerMainTabsActivity) {
            ((OleOwnerMainTabsActivity) getActivity()).notificationsClicked();
        }
    }

    public void setBadgeValue() {
        if (AppManager.getInstance().notificationCount > 0) {
            binding.toolbarBadge.setVisibility(View.VISIBLE);
            binding.toolbarBadge.setNumber(AppManager.getInstance().notificationCount);
        }
        else  {
            binding.toolbarBadge.setVisibility(View.GONE);
        }
    }

    private void bookingClicked() {

        binding.relBooking.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.whiteColor));
        binding.relBooking.setCardBackgroundColor(Color.parseColor("#33000000"));
        binding.tvBooking.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor));

        binding.relCancelled.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.club_selection_color));
        binding.relCancelled.setCardBackgroundColor(Color.TRANSPARENT);
        binding.tvCancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor));

        binding.relPastBookings.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.club_selection_color));
        binding.relPastBookings.setCardBackgroundColor(Color.TRANSPARENT);
        binding.tvPastDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor));


        prevNextDate = getPrevNextDate(0, fromDate);
        bookingStatus = "booking";
        getBookingsAPI(oleBookingList.isEmpty(), clubId, fromDate,"","");
        oleBookingListAdapter.setDataSource(oleBookingList);
    }

    private void cancelledClicked() {
        binding.relBooking.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.club_selection_color));
        binding.relBooking.setCardBackgroundColor(Color.TRANSPARENT);
        binding.tvBooking.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor));

        binding.relCancelled.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.whiteColor));
        binding.relCancelled.setCardBackgroundColor(Color.parseColor("#33000000"));
        binding.tvCancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor));

        binding.relPastBookings.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.club_selection_color));
        binding.relPastBookings.setCardBackgroundColor(Color.TRANSPARENT);
        binding.tvPastDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor));

        bookingStatus = "cancelled";
        getBookingsAPI(oleBookingList.isEmpty(), clubId, fromDate,"","");
        oleBookingListAdapter.setDataSource(cancelledList);
    }
    private void previousBookings(String date) {
        binding.relBooking.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.club_selection_color));
        binding.relBooking.setCardBackgroundColor(Color.TRANSPARENT);
        binding.tvBooking.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor));

        binding.relCancelled.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.club_selection_color));
        binding.relCancelled.setCardBackgroundColor(Color.TRANSPARENT);
        binding.tvCancelled.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor));

        binding.relPastBookings.setStrokeColor(ContextCompat.getColor(requireContext(), R.color.whiteColor));
        binding.relPastBookings.setCardBackgroundColor(Color.parseColor("#33000000"));
        binding.tvPastDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteColor));

        bookingStatus = "previous";
        getBookingsAPI(oleBookingList.isEmpty(), clubId, date,"", "prev");
        oleBookingListAdapter.setDataSource(previousBookingsList);
    }

//    private void calendarClicked() {
//        ((BaseActivity) getActivity()).showDateRangeFilter(fromDate, toDate, new OleDateRangeFilterDialogFragment.DateRangeFilterDialogFragmentCallback() {
//            @Override
//            public void filterData(DialogFragment df, String from, String to) {
//                df.dismiss();
//                if (from.isEmpty() && to.isEmpty()) {
//                    fromDate = getDateStr(new Date());
//                    toDate = "";
//                }
//                else {
//                    fromDate = from;
//                    toDate = to;
//                }
//                getBookingsAPI(true, "1", getDateStr(new Date()));
//                selectedDateIndex = -1;
//                oleBookingDateAdapter.setSelectedDateIndex(selectedDateIndex);
//            }
//        });
//    }
//
    private void setLabelCount() {
        String displayDate = getPrevNextDate(0, prevNextDate);
        binding.tvPastDate.setText(displayDate);
    }


    private void getOwnerClubList(boolean isLoader) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getOwnerClubList(0.0,0.0, fromDate);
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
                            clubList.clear();

                            Club allClub = new Club();
                            allClub.setId("");
                            allClub.setName(getString(R.string.all));
                            clubList.add(allClub);

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject clubJsonObject = data.getJSONObject(i);
                                Club club = gson.fromJson(clubJsonObject.toString(), Club.class);
                                int clubId = clubJsonObject.getInt("id");
                                club.setId(String.valueOf(clubId));
                                clubList.add(club);
                            }

                            SimpleDateFormat dateFormat = new SimpleDateFormat(kBookingFormat, Locale.ENGLISH);
                            selectedDate = dateFormat.format(new Date());
                            getAllSlotDates(true, clubList.get(selectedDateIndex).getId());

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

    private void getAllSlotDates(boolean isLoader, String clubId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getAllSlotDates(clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            arrDate.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                OleBookingListDate date = gson.fromJson(arr.get(i).toString(), OleBookingListDate.class);
                                arrDate.add(date);
                            }

                            oleBookingDateAdapter.setDataSource(arrDate.toArray());
                            clubAdapter.notifyDataSetChanged();
                            binding.clubsNameLinear.setVisibility(View.VISIBLE);

//                            fieldAdapter.setSelectedDate(selectedDate);
//                            fieldAdapter.notifyDataSetChanged();
                        } else {
//                            fieldList.clear();
//                            fieldAdapter.notifyDataSetChanged();
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

    private void getBookingsAPI(boolean isLoader, String clubId, String from, String to, String prev) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getActivity(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getBookingsAPI(clubId, from, to);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);

                            oleBookingList.clear();
                            cancelledList.clear();
                            previousBookingsList.clear();
                            if (!prev.isEmpty()){
                                Gson gson = new Gson();
                                for (int i = 0; i < arr.length(); i++) {
                                    BasicBookingDetail booking = gson.fromJson(arr.get(i).toString(), BasicBookingDetail.class);
                                    if (booking.getStatus().equalsIgnoreCase(Constants.kCompletedBooking)) {
                                        previousBookingsList.add(booking);
                                    }
                                }

                                if (bookingStatus.equalsIgnoreCase("previous") || bookingStatus.isEmpty()) {
                                    oleBookingListAdapter.setDataSource(previousBookingsList);
                                }

                            }
                            else{
                                Gson gson = new Gson();
                                for (int i = 0; i < arr.length(); i++) {
                                    BasicBookingDetail booking = gson.fromJson(arr.get(i).toString(), BasicBookingDetail.class);
                                    if (booking.getStatus().equalsIgnoreCase(Constants.kCancelledBooking)) { //||booking.getStatus().equalsIgnoreCase(Constants.kBlockedBooking)
                                        cancelledList.add(booking);
                                    }
                                    else {
                                        oleBookingList.add(booking);
                                    }
                                }

                                if (bookingStatus.equalsIgnoreCase("booking") || bookingStatus.isEmpty()) {
                                    oleBookingListAdapter.setDataSource(oleBookingList);
                                }
                                else if (bookingStatus.equalsIgnoreCase("cancelled")){
                                    oleBookingListAdapter.setDataSource(cancelledList);
                                }
                            }
                            prevNextDate = fromDate;
                            setLabelCount();


//                            if (isDateNeeded.equalsIgnoreCase("1"))  {
//                                arrDate.clear();
//                                JSONArray arrD = object.getJSONArray("dates");
//                                for (int i = 0; i < arrD.length(); i++) {
//                                    arrDate.add(gson.fromJson(arrD.get(i).toString(), OleBookingListDate.class));
//                                }
//                                oleBookingDateAdapter.setDataSource(arrDate.toArray());
//                            }

                        }
                        else {
                            oleBookingList.clear();
                            cancelledList.clear();
                            oleBookingListAdapter.setDataSource(new ArrayList<>());
//                            setLabelCount();
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

    private String getPrevNextDate(int status, String date) { // date format: 2024-12-09
        String formattedDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                LocalDate localDate = LocalDate.parse(date);
                localDate = (status == 0) ? localDate.minusDays(7) : localDate.plusDays(7);
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH);
                formattedDate = localDate.format(outputFormatter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return formattedDate;
    }



//    private String getPrevNextDate(int status, String date) { // date format: 2024-12-09
//        LocalDate localDate = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            localDate = LocalDate.parse(date);
//            localDate = (status == 0) ? localDate.minusDays(7) : localDate.plusDays(7);
//            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
//            Date date = inputFormat.parse(localDate);
//
//        }
//
//        return date.toString();
//    }
//    private String formatDate(String dateStr) {
//        try {
//
//            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
//            Date date = inputFormat.parse(dateStr);
//            return outputFormat.format(date);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//    }


//    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE", new Locale(Functions.getAppLangStr(context)));
//            holder.tvDayName.setText(dateFormat.format(date));
//            dateFormat.applyPattern("MMM");
//            holder.tvMonth.setText(dateFormat.format(date));
//    dateFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
//            holder.tvDate.setText(dateFormat.format(date));



}
