package ae.oleapp.owner;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;

import ae.oleapp.R;
import ae.oleapp.adapters.OleBookingDateAdapter;
import ae.oleapp.adapters.OleBookingDurationAdapter;
import ae.oleapp.adapters.OleBookingSlotAdapter;
import ae.oleapp.adapters.OleClubNameAdapter;
import ae.oleapp.adapters.OleFacilityAdapter;
import ae.oleapp.adapters.OleFastBookingFieldAdapter;
import ae.oleapp.adapters.OleRankClubAdapter;
import ae.oleapp.adapters.SlotPatternAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.booking.BookingConfirmedActivity;
import ae.oleapp.booking.BookingConflictActivity;
import ae.oleapp.booking.BookingWaitingUserActivity;
import ae.oleapp.booking.bottomSheets.CallSheetFragment;
import ae.oleapp.booking.bottomSheets.CancelBookingSheetFragment;
import ae.oleapp.booking.bottomSheets.ConfirmCancelWaitingBookingSheetFragment;
import ae.oleapp.booking.bottomSheets.FacilityQuantitySheetFragment;
import ae.oleapp.databinding.OleactivityFastBookingBinding;
import ae.oleapp.dialogs.OleCustomAlertDialog;
import ae.oleapp.fragments.AddWaitingUserDialogFragment;
import ae.oleapp.fragments.showWaitingUsersListDialogFragment;
import ae.oleapp.models.BookingSlot;
import ae.oleapp.models.OleBookingListDate;
import ae.oleapp.models.Club;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.Field;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.OleOfferData;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OleFastBookingActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityFastBookingBinding binding;
    private Club club;
    private boolean isPadel;
    private String fieldId = "";
    private Field fieldDetail;
    private List<Club> clubList = new ArrayList<>();
    private final List<Field> fieldList = new ArrayList<>();
    private final List<OleClubFacility> facilityList = new ArrayList<>();
    private String selectedDuration = "";
    private final String isWaitingUser = "0";
    private String selectedSlotDuration = "";
    private String selectedStartTime = "";
    private String selectedEndTime = "";
    private String selectedSlotTime = "";
    private String selectedShift = "";
    private String selectedDate = "";
    private String price = "";
    private final String offerDiscountType = "";
    private final List<OleBookingListDate> arrDate = new ArrayList<>();
    private int selectedDateIndex = 0;
    private final String kBookingFormat = "yyyy-MM-dd";
    private double offerDiscount = 0;
    private double selectedFacilityPrice = 0;
    private OleFacilityAdapter oleFacilityAdapter;
    private OleBookingDurationAdapter durationAdapter;
    private OleBookingDateAdapter daysAdapter;
    private OleFastBookingFieldAdapter fieldAdapter;
    private OleRankClubAdapter clubAdapter;
    private final List<OleKeyValuePair> durationList = new ArrayList<>();
    private int selectedSlotIndex = -1;
    private int selectedFieldIndex = -1;
    private int clubIndex = -1;
    private OleClubNameAdapter oleClubNameAdapter;
    private SlotPatternAdapter slotPatternAdapter;
    private String countryId = "", playerName = "", playerNumber = "";
    private int bookingId,  playerCountryId;
    private Boolean changeTime = false;
    private int fieldOfferId = 0;
    private String clubId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityFastBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            isPadel = bundle.getBoolean("is_padel", false);
            clubIndex = bundle.getInt("index");
            bookingId = bundle.getInt("booking_id");
            playerName = bundle.getString("name");
            playerNumber = bundle.getString("number");
            playerCountryId = bundle.getInt("countryId");
            changeTime = bundle.getBoolean("change_time");
            clubId = bundle.getString("club_id");
            Gson gson = new Gson();
            club = gson.fromJson(bundle.getString("club", ""), Club.class);
        }

        clubList.clear();
        clubList = AppManager.getInstance().clubs;
        club = clubList.get(clubIndex);
        populateDuration(false);

        LinearLayoutManager clubNameLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubNameRecyclerVu.setLayoutManager(clubNameLayoutManager);
        oleClubNameAdapter = new OleClubNameAdapter(getContext(), clubList);
        oleClubNameAdapter.setItemClickListener(clubNameClickListener);
        binding.clubNameRecyclerVu.setAdapter(oleClubNameAdapter);
        oleClubNameAdapter.setSelectedIndex(clubIndex);

        selectedDuration = durationList.get(0).getKey();
        LinearLayoutManager slotPatternLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.slotsPatternRecyclerVu.setLayoutManager(slotPatternLayoutManager);
        slotPatternAdapter = new SlotPatternAdapter(getContext(), durationList, selectedDuration);
        slotPatternAdapter.setItemClickListener(slotPatternClickListener);
        binding.slotsPatternRecyclerVu.setAdapter(slotPatternAdapter);


//        facilityList = club.getFacilities();
        LinearLayoutManager facLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.facRecyclerVu.setLayoutManager(facLayoutManager);
        oleFacilityAdapter = new OleFacilityAdapter(getContext(), facilityList, false);
        oleFacilityAdapter.setOnItemClickListener(facClickListener);


        SimpleDateFormat dateFormat = new SimpleDateFormat(kBookingFormat, Locale.ENGLISH);
        selectedDate = dateFormat.format(new Date());
        getAllSlotDates(true, clubId);
        getClubFacilities(false, clubId);

        LinearLayoutManager fieldLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.fieldRecyclerVu.setLayoutManager(fieldLayoutManager);
        fieldAdapter = new OleFastBookingFieldAdapter(getContext(), fieldList, selectedDate, false);
        fieldAdapter.setPadel(false);
        fieldAdapter.setOnItemClickListener(fieldClickListener);
        binding.fieldRecyclerVu.setAdapter(fieldAdapter);


        LinearLayoutManager daysLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.daysRecyclerVu.setLayoutManager(daysLayoutManager);
        daysAdapter = new OleBookingDateAdapter(getContext(), arrDate.toArray(), selectedDateIndex);
        daysAdapter.setOnItemClickListener(daysClickListener);
        binding.daysRecyclerVu.setAdapter(daysAdapter);

        getAllSlots(true, selectedDate);

        binding.btnBack.setOnClickListener(this);
        binding.btnContinue.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack) {
            finish();
        }
        else if (v == binding.btnContinue) {
            bookClicked("","");
        }


    }

//    private void timeSlots(String val){
//        if (val.equalsIgnoreCase("1")){
//            selectedDuration = "1";
//
//            binding.slot60Vu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
//            binding.clockImg60.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_slot_clock_ic_green));
//            binding.tvSlot60.setTextColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
//
//            binding.slot90Vu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.transparent));
//            binding.clockImg90.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_slot_clock_ic));
//            binding.tvSlot90.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));
//
//            binding.slot120Vu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.transparent));
//            binding.clockImg120.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_slot_clock_ic));
//            binding.tvSlot120.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));
//
//
//        }
//        else if (val.equalsIgnoreCase("1.5")) {
//            selectedDuration = "1.5";
//            binding.slot60Vu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.transparent));
//            binding.clockImg60.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_slot_clock_ic));
//            binding.tvSlot60.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));
//
//            binding.slot90Vu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
//            binding.clockImg90.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_slot_clock_ic_green));
//            binding.tvSlot90.setTextColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
//
//            binding.slot120Vu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.transparent));
//            binding.clockImg120.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_slot_clock_ic));
//            binding.tvSlot120.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));
//
//        }
//        else  {
//            selectedDuration = "2";
//            binding.slot60Vu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.transparent));
//            binding.clockImg60.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_slot_clock_ic));
//            binding.tvSlot60.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));
//
//            binding.slot90Vu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.transparent));
//            binding.clockImg90.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_slot_clock_ic));
//            binding.tvSlot90.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));
//
//            binding.slot120Vu.setStrokeColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
//            binding.clockImg120.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.v5_slot_clock_ic_green));
//            binding.tvSlot120.setTextColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
//
//        }
//
//    }


//    private void doItBitch(){
//        for (int i =0; i<clubList.size(); i++){
//            if (id.equalsIgnoreCase(clubList.get(i).getId())){
//                this.club = null;
//                Club club = clubList.get(i);
//                clubAdapter.setSelectedId(id);
//                this.club = club;
//                populateDuration(true);
//                fieldAdapter.setPadel(club.getClubType().equalsIgnoreCase(Constants.kPadelModule));
//                fieldId = "";
//                selectedStartTime = "";
//                getAllFieldsAPI(true, selectedDate);
//                Iterator<OleClubFacility> iterator = club.getFacilities().iterator();
//                oleFacilityAdapter.selectedFacility.clear();
//                while (iterator.hasNext()) {
//                    OleClubFacility facility = iterator.next();
//                    if (facility.getPrice().equalsIgnoreCase("")) {
//                        oleFacilityAdapter.selectedFacility.add(facility);
//                        iterator.remove();
//                    }
//                }
//                oleFacilityAdapter.setDataSource(club.getFacilities());
//                fieldAdapter.notifyDataSetChanged();
//                daysAdapter.notifyDataSetChanged();
//
//            }
//        }
//    }

    OleClubNameAdapter.ItemClickListener clubNameClickListener = new OleClubNameAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            binding.tvPrice.setText("00");
            clubIndex = pos;
            oleClubNameAdapter.setSelectedIndex(clubIndex);
            clubId =  clubList.get(pos).getId();
            getAllSlots(true, selectedDate);
        }
    };

    SlotPatternAdapter.ItemClickListener slotPatternClickListener = new SlotPatternAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            binding.tvPrice.setText("00");
            selectedDuration = clubList.get(clubIndex).getSlotPattern().get(pos);
            slotPatternAdapter.setSelectedDuration(selectedDuration);
            getAllSlots(true, selectedDate);

        }
    };

    OleFacilityAdapter.OnItemClickListener facClickListener = new OleFacilityAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            OleClubFacility facility = facilityList.get(pos);
            if (facility.getPrice() == 0) {
                return;
            }
            oleFacilityAdapter.setSelectedFacility(facility);
            showFacilityQuantity(pos);
        }

        @Override
        public void OnPlusClick(View v, int pos) {
            OleClubFacility facility = club.getFacilities().get(pos);
            int maxQty = Integer.parseInt(facility.getMaxQuantity());
            if (facility.getQty() < maxQty) {
                facility.setQty(facility.getQty()+1);
                if (selectedSlotDuration.isEmpty()) {
                    setButtonText(selectedDuration);
                }
                else {
                    setButtonText(selectedSlotDuration);
                }
                oleFacilityAdapter.notifyDataSetChanged();
            }
            else {
                Functions.showToast(getContext(), String.format(getResources().getString(R.string.you_select_max_qty_place), maxQty), FancyToast.ERROR);
            }
        }

        @Override
        public void OnMinusClick(View v, int pos) {
            OleClubFacility facility = club.getFacilities().get(pos);
            facility.setQty(facility.getQty()-1);
            if (facility.getQty() == 0) {
                int index = oleFacilityAdapter.isExistInSelected(facility.getFacilityId());
                if (index != -1) {
                    oleFacilityAdapter.selectedFacility.remove(index);
                }
            }
            if (selectedSlotDuration.isEmpty()) {
                setButtonText(selectedDuration);
            }
            else {
                setButtonText(selectedSlotDuration);
            }
            oleFacilityAdapter.notifyDataSetChanged();
        }
    };

    protected void showFacilityQuantity(int pos) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("FacilityQuantitySheetFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        FacilityQuantitySheetFragment dialogFragment = new FacilityQuantitySheetFragment();
        dialogFragment.setDialogCallback((df,value) -> {
            df.dismiss();
            if (!value.isEmpty()){
                OleClubFacility facility = facilityList.get(pos);
                int maxQty = Integer.parseInt(facility.getMaxQuantity());
                if (Integer.parseInt(value) <= maxQty){
                    facility.setQty(Integer.parseInt(value));
                    if (selectedSlotDuration.isEmpty()) {
                        setButtonText(selectedDuration);
                    }
                    else {
                        setButtonText(selectedSlotDuration);
                    }
                    oleFacilityAdapter.notifyDataSetChanged();

                }
                else{
                    Functions.showToast(getContext(), String.format(getResources().getString(R.string.you_select_max_qty_place), maxQty), FancyToast.ERROR);
                }
            }

        });
        dialogFragment.show(fragmentTransaction, "FacilityQuantitySheetFragment");
    }

    OleFastBookingFieldAdapter.OnItemClickListener fieldClickListener = new OleFastBookingFieldAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(OleFastBookingFieldAdapter.ViewHolder fieldHolder, OleBookingSlotAdapter.ViewHolder v, int slotPos, int fieldPos) {
            if (selectedFieldIndex != -1) { // unselect previous slot
                OleFastBookingFieldAdapter.ViewHolder holder = (OleFastBookingFieldAdapter.ViewHolder) binding.fieldRecyclerVu.findViewHolderForAdapterPosition(selectedFieldIndex);
                if (holder != null && holder.slotAdapter != null) {
                    holder.slotAdapter.setSelectedSlotIndex(-1, "");
                }
                else {
                    return;
                }
            }
            BookingSlot slot = fieldList.get(fieldPos).getSlotList().get(slotPos);
            if (slot.getStatus().equalsIgnoreCase("booked")) {
                fieldId = fieldList.get(fieldPos).getId();
                showConfirmCancelWaitingBookingDetails(slot);
                return;
            }
            if (slot.getStatus().equalsIgnoreCase("hidden")) {
                Functions.showToast(getContext(), getString(R.string.you_hide_this_slot), FancyToast.SUCCESS);
                return;
            }

            selectedFieldIndex = fieldPos;
            selectedSlotIndex = slotPos;
            fieldDetail = fieldList.get(fieldPos);
            fieldId = fieldDetail.getId();
//            populateData(fieldDetail);
            slotClicked(fieldHolder, v, slot);
        }

        @Override
        public void OnItemLongClick(OleFastBookingFieldAdapter.ViewHolder fieldHolder, OleBookingSlotAdapter.ViewHolder v, int slotPos, int fieldPos) {
            BookingSlot slot = fieldList.get(fieldPos).getSlotList().get(slotPos);
//            if (!slot.getStatus().equalsIgnoreCase("available")) {
//                showBookingAlert(slot.getStart(), slot.getEnd());
//            }

//            if (!slot.getStatus().equalsIgnoreCase("available") ) {
//                showWaitingUsersList(slot);
//            } else
                if (slot.getHasWaitingList().equalsIgnoreCase("1")) { //slot.getStatus().equalsIgnoreCase("available") &&
                selectedFieldIndex = fieldPos;
                selectedSlotIndex = slotPos;
                fieldDetail = fieldList.get(fieldPos);
                fieldId = fieldDetail.getId();
                slotClicked(fieldHolder, v, slot);
                showWaitingUsersList(slot);
            }
        }
    };

//    OleBookingDurationAdapter.OnItemClickListener durClickListener = new OleBookingDurationAdapter.OnItemClickListener() {
//        @Override
//        public void OnItemClick(View v, int pos) {
//            selectedDuration = durationList.get(pos).getKey();
//            durationAdapter.setSelectedDuration(selectedDuration);
//            setButtonText(selectedDuration);
//            selectedStartTime = "";
//            getAllFieldsAPI(true, arrDate.get(selectedDateIndex).getDate());
//        }
//    };

    OleBookingDateAdapter.OnItemClickListener daysClickListener = new OleBookingDateAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View v, int pos) {
            if (!selectedDuration.isEmpty()) {
                binding.tvPrice.setText("00");
                String date = arrDate.get(pos).getDate();
                selectedDateIndex = pos;
                daysAdapter.setSelectedDateIndex(pos);
//                setFieldPriceByDay(date);
//                selectedStartTime = "";
//                offerDiscount = 0;
//                setButtonText(selectedDuration);
                selectedDate = date;
                getAllSlots(true, selectedDate);

//                getAllFieldsAPI(true, selectedDate);
            }
            else {
                Functions.showToast(getContext(), getString(R.string.select_duration), FancyToast.ERROR);
            }
        }
    };

    private void slotClicked(OleFastBookingFieldAdapter.ViewHolder fieldHolder, OleBookingSlotAdapter.ViewHolder v, BookingSlot slot) {
        if (selectedDuration.equalsIgnoreCase(v.slotDuration)) {
            selectedSlotDuration = v.slotDuration;
            selectedStartTime = slot.getStart();
            selectedEndTime = slot.getEnd();
            selectedSlotTime = slot.getSlot();
            selectedShift = slot.getShift();
            offerDiscount = 0;
            fieldOfferId = slot.getFieldOfferId();
            if (selectedSlotDuration.isEmpty()) {
                setButtonText(selectedDuration);
            }
            else {
                setButtonText(selectedSlotDuration);
            }

            calculateOffer(slot.getIsOfferedSlot(), slot);

//            try {
//                offerDiscount = checkOfferForSlot(selectedFieldIndex, slot.getStart(), slot.getEnd());
//                if (selectedSlotDuration.isEmpty()) {
//                    setButtonText(selectedDuration);
//                }
//                else {
//                    setButtonText(selectedSlotDuration);
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            fieldHolder.slotAdapter.setSelectedSlotIndex(selectedSlotIndex, selectedDate);
        }
        else {
            String msg = getResources().getString(R.string.slot_selected_dur_msg, selectedDuration, v.slotDuration, v.slotDuration);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.confirmation))
                    .setMessage(msg)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setButtonText(v.slotDuration);
                            selectedSlotDuration = v.slotDuration;
                            selectedStartTime = slot.getStart();
                            selectedEndTime = slot.getEnd();
                            selectedSlotTime = slot.getSlot();
                            selectedShift = slot.getShift();
                            offerDiscount = 0;
//                            try {
//                                offerDiscount = checkOfferForSlot(selectedFieldIndex, slot.getStart(), slot.getEnd());
//                                if (selectedSlotDuration.isEmpty()) {
//                                    setButtonText(selectedDuration);
//                                }
//                                else {
//                                    setButtonText(selectedSlotDuration);
//                                }
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
                            fieldHolder.slotAdapter.setSelectedSlotIndex(selectedSlotIndex, selectedDate);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            builder.show();
        }
    }
//
//    private void showBookingAlert(String startTime, String endTime) {
//        if (Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            Fragment prev = getSupportFragmentManager().findFragmentByTag("BookingAlertDialogFragment");
//            if (prev != null) {
//                fragmentTransaction.remove(prev);
//            }
//            fragmentTransaction.addToBackStack(null);
//            OleBookingAlertDialogFragment dialogFragment = new OleBookingAlertDialogFragment();
//            dialogFragment.setFragmentCallback(new OleBookingAlertDialogFragment.BookingAlertDialogFragmentCallback() {
//                @Override
//                public void didSubmit(String phone) {
//                    notificationAPI(startTime, endTime, phone);
//                }
//            });
//            dialogFragment.show(fragmentTransaction, "BookingAlertDialogFragment");
//        }
//    }
//
//    private void confirmCancelBookingAlert(String bookingId, int position, int fieldPos, String bookingStatus) {
//        String[] arr;
//        if (!bookingStatus.equalsIgnoreCase("1")) {
//            arr = new String[]{getResources().getString(R.string.add_waiting_user),getResources().getString(R.string.confirm_booking), getResources().getString(R.string.cancel_booking)};
//        }
//        else {
//            arr = new String[]{getResources().getString(R.string.add_waiting_user), getResources().getString(R.string.cancel_booking)};
//        }
//        ActionSheet.createBuilder(this, getSupportFragmentManager())
//                .setCancelButtonTitle(getResources().getString(R.string.dismiss))
//                .setOtherButtonTitles(arr)
//                .setCancelableOnTouchOutside(true)
//                .setListener(new ActionSheet.ActionSheetListener() {
//                    @Override
//                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
//
//                    }
//
//                    @Override
//                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
//
//
//                    }
//                }).show();
//    }

    private void bookClicked(String name, String phone) {
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        if (club.getOwnerClub().getSubscription().getStatus().equalsIgnoreCase("EXPIRED")) {
            Functions.showToast(getContext(), getString(R.string.renew_membership_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        if (fieldDetail == null) {
            Functions.showToast(getContext(), getString(R.string.select_field), FancyToast.ERROR);
            return;
        }
        //        if (fieldDetail.getIsBlock().equalsIgnoreCase("1")) {
        //            Functions.showToast(getContext(), "انت محظور من قبل الملعب", FancyToast.ERROR);
        ////            Functions.showToast(getContext(), getResources().getString(R.string.you_are_blocked_by_field_owner), FancyToast.ERROR);
        //            return;
        //        }
        if (selectedDuration.isEmpty()) {
            Functions.showToast(getContext(), getResources().getString(R.string.select_duration), FancyToast.ERROR);
            return;
        }
        if (selectedDate.isEmpty()) {
            Functions.showToast(getContext(), getResources().getString(R.string.select_date), FancyToast.ERROR);
            return;
        }
        if (selectedStartTime.isEmpty()) {
            Functions.showToast(getContext(), getResources().getString(R.string.select_time), FancyToast.ERROR);
            return;
        }
        if (fieldList.isEmpty()) {
            return;
        }

        Intent intent = new Intent(getContext(), OleBookingInfoActivity.class);
        intent.putExtra("price",String.valueOf(Double.parseDouble(price)-offerDiscount+selectedFacilityPrice));
        intent.putExtra("duration",selectedSlotDuration);
//        if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
//            intent.putExtra("size", "");
//        }
//        else {
//            intent.putExtra("size",fieldDetail.getFieldSize().getName());
//        }

        intent.putExtra("size",  fieldDetail.getSize());
        intent.putExtra("club_name",club.getName());
        intent.putExtra("field_name",fieldDetail.getName());
        intent.putExtra("currency",fieldDetail.getCurrency());
        intent.putExtra("club_id",club.getId());
        intent.putExtra("field_id",fieldId);
        intent.putExtra("date",selectedDate);
        intent.putExtra("time",selectedSlotTime);
        intent.putExtra("name", name);
        intent.putExtra("phone", phone);
        intent.putExtra("player_name", playerName);
        intent.putExtra("number", playerNumber);
        intent.putExtra("countryId", playerCountryId);
        intent.putExtra("change_time", changeTime);

        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // from owner booking info
            Bundle bundle = data.getExtras();
            if (bundle != null){
                String userId = bundle.getString("user_id");
                String name = bundle.getString("name");
                countryId = bundle.getString("countryId");
                String phone = bundle.getString("phone");
                String discount = bundle.getString("discount");
                String days = bundle.getString("days");
                String fromDate = bundle.getString("from_date");
                String toDate = bundle.getString("to_date");
                String schedule = bundle.getString("schedule");
                String addPrice = bundle.getString("add_price");
                String day = bundle.getString("day");
                String time = bundle.getString("time");
                addBookingAPI(userId, name, countryId, phone, discount, "cash", days, fromDate, toDate, addPrice, 0, "", offerDiscount, "", "", schedule, day, time);
            }
        }
    }

//    private void populateData(Field field) {
//        if (club.getSlots60().equalsIgnoreCase("1")) {
//            price = field.getOneHour();
//        }
//        else if (club.getSlots90().equalsIgnoreCase("1")) {
//            price = field.getOneHalfHours();
//        }
//        else if (club.getSlots120().equalsIgnoreCase("1")) {
//            price = field.getTwoHours();
//        }
//
//        if (club.getClubType().equalsIgnoreCase(Constants.kPadelModule)) {
//            if (club.getSlots90().equalsIgnoreCase("1")) {
//                price = field.getOneHalfHours();
//            }
//            else if (club.getSlots120().equalsIgnoreCase("1")) {
//                price = field.getTwoHours();
//            }
//        }
//
//        if (arrDate.size() > 0) {
//            setFieldPriceByDay(arrDate.get(selectedDateIndex).getDate());
//        }
//        else {
//            SimpleDateFormat dateFormat = new SimpleDateFormat(kBookingFormat, Locale.ENGLISH);
//            setFieldPriceByDay(dateFormat.format(new Date()));
//        }
//        binding.btnBook.setTitle(String.format("%s %s %s", getString(R.string.book_now), fieldDetail.getCurrency(), price));
//    }

    private void populateDuration(boolean isRefresh) {
        durationList.clear();
        if (!clubList.get(clubIndex).getSlotPattern().isEmpty()){
            for (int i = 0; i<clubList.get(clubIndex).getSlotPattern().size(); i++){
                if (clubList.get(clubIndex).getSlotPattern().get(i).equalsIgnoreCase("1")){
                    durationList.add(new OleKeyValuePair("1", getString(R.string.one_hour)));
                }
                else if (clubList.get(clubIndex).getSlotPattern().get(i).equalsIgnoreCase("1.5")) {
                    durationList.add(new OleKeyValuePair("1.5", getString(R.string.one_half_hour)));
                }else if (clubList.get(clubIndex).getSlotPattern().get(i).equalsIgnoreCase("2")){
                    durationList.add(new OleKeyValuePair("2", getString(R.string.two_hour)));
                }
            }
            if (isRefresh) {
                durationAdapter.notifyDataSetChanged();
            }
        }

    }
//
//    private void setFieldPriceByDay(String date) {
//        if (fieldDetail != null) {
//            SimpleDateFormat dateFormat = new SimpleDateFormat(kBookingFormat, Locale.ENGLISH);
//            Date dt = null;
//            try {
//                dt = dateFormat.parse(date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            dateFormat.applyPattern("EEEE");
//            String dayName = dateFormat.format(dt);
//            String dayId = Functions.getDayIdByName(dayName.toLowerCase());
//            for (OleFieldPrice dayPrice : fieldDetail.getDaysPrice()) {
//                if (dayPrice.getDayId().equalsIgnoreCase(dayId)) {
//                    fieldDetail.setOneHour(dayPrice.getOneHour());
//                    fieldDetail.setOneHalfHours(dayPrice.getOneHalfHour());
//                    fieldDetail.setTwoHours(dayPrice.getTwoHour());
//                    break;
//                }
//            }
//        }
//    }

//    public double checkOfferForSlot(int fieldPos, String time1, String time2) throws ParseException {
//        double result = 0;
//        if (fieldList.get(fieldPos).getOffers().size() > 0) {
//            for (OleOfferData oleOfferData : fieldList.get(fieldPos).getOffers()) {
//                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
//                dateFormat.setTimeZone(TimeZone.getDefault());
//                Date slotTime1 = dateFormat.parse(time1);
//                Date slotTime2 = dateFormat.parse(time2);
//                dateFormat.applyPattern("dd/MM/yyyy hh:mma");
//                Date slot1DT = dateFormat.parse(dateFormat.format(slotTime1));
//                Date slot2DT = dateFormat.parse(dateFormat.format(slotTime2));
//                dateFormat.applyPattern(kBookingFormat);
//                Date date = dateFormat.parse(selectedDate);
//                Date offerExpiryDate = dateFormat.parse(oleOfferData.getOfferExpiry());
//                dateFormat.applyPattern("dd/MM/yyyy");
//                String todayDate = dateFormat.format(date);
//
//                // check offer expired date if selected date is greater than offer end date then return false
//                boolean isExpired = date.compareTo(offerExpiryDate) > 0;
//
//                // check day
//                String dayName = Functions.getDayName(date);
//                String[] dayIdArr = oleOfferData.getDayId().split(",");
//                boolean isFound = false;
//                for (String id : dayIdArr) {
//                    if (dayName.equalsIgnoreCase(Functions.getEngDayById(getContext(), id))) {
//                        isFound = true;
//                        break;
//                    }
//                }
//
//                dateFormat.applyPattern("dd/MM/yyyy");
//                Date offerStartDate = dateFormat.parse(oleOfferData.getTimimgStart().split(" ")[0]);
//                Date offerEndDate = dateFormat.parse(oleOfferData.getTimingEnd().split(" ")[0]);
//                if (offerEndDate.compareTo(offerStartDate) > 0) {
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTime(date);
//                    calendar.add(Calendar.DAY_OF_MONTH, 1);
//                    String nextDate = dateFormat.format(calendar.getTime());
//                    dateFormat.applyPattern("dd/MM/yyyy hh:mma");
//                    offerStartDate = dateFormat.parse(String.format("%s %s", todayDate, oleOfferData.getTimimgStart().split(" ")[1]));
//                    offerEndDate = dateFormat.parse(String.format("%s %s", nextDate, oleOfferData.getTimingEnd().split(" ")[1]));
//                }
//                else {
//                    dateFormat.applyPattern("dd/MM/yyyy hh:mma");
//                    offerStartDate = dateFormat.parse(String.format("%s %s", todayDate, oleOfferData.getTimimgStart().split(" ")[1]));
//                    offerEndDate = dateFormat.parse(String.format("%s %s", todayDate, oleOfferData.getTimingEnd().split(" ")[1]));
//                }
//
//                if (((offerStartDate.compareTo(slot1DT)<0 || offerStartDate.compareTo(slot1DT) == 0)
//                        && (slot1DT.compareTo(offerEndDate) < 0 || slot1DT.compareTo(offerEndDate) == 0))
//                        && (offerStartDate.compareTo(slot2DT)<0 || offerStartDate.compareTo(slot2DT) == 0)
//                        && (slot2DT.compareTo(offerEndDate) < 0 || slot2DT.compareTo(offerEndDate) == 0)
//                        && isFound && !isExpired) {
//                    if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
//                        result = 0;
//                        offerDiscountType = "";
//                    }
//                    else {
//                        result = Double.parseDouble(oleOfferData.getDiscount());
//                        offerDiscountType = oleOfferData.getDiscountType();
//                    }
//                }
//            }
//        }
//        return result;
//    }

    private void setButtonText(String duration) {
        if (fieldDetail != null) {
            binding.tvPrice.setText(fieldDetail.getPrice());
            binding.tvCurrency.setText(fieldDetail.getCurrency());
//
//            if (offerDiscountType.isEmpty() || offerDiscountType.equalsIgnoreCase("amount")) {
//                double p = Double.parseDouble(price) - offerDiscount;
//                p = p + selectedFacilityPrice;
//                binding.tvPrice.setText(String.format("%s %s %s", getString(R.string.book_now), fieldDetail.getCurrency(), p));
//            }
//            else {
//                double p = Double.parseDouble(price);
//                p = p - ((offerDiscount / 100) * p);
//                p = p + selectedFacilityPrice;
//                binding.tvPrice.setText(String.format("%s %s %s", getString(R.string.book_now), fieldDetail.getCurrency(), p));
//
//            }


            selectedFacilityPrice = calculateFacPrice();
            if (duration.equalsIgnoreCase("1")) {
                price = fieldDetail.getPrice();
            }
            else if (duration.equalsIgnoreCase("1.5")) {
                price =  fieldDetail.getPrice();
            }
            else if (duration.equalsIgnoreCase("2")) {
                price =  fieldDetail.getPrice();
            }
            else {
                price = "";
            }
            if (offerDiscountType.isEmpty() || offerDiscountType.equalsIgnoreCase("amount")) {
                double p = Double.parseDouble(price) - offerDiscount;
                p = p + selectedFacilityPrice;
                binding.tvPrice.setText(String.valueOf(p));
                binding.tvCurrency.setText(fieldDetail.getCurrency());
            }
            else {
                double p = Double.parseDouble(price);
                p = p - ((offerDiscount / 100) * p);
                p = p + selectedFacilityPrice;
                binding.tvPrice.setText((String.valueOf(p)));
                binding.tvCurrency.setText(fieldDetail.getCurrency());
            }

//
//            if (price.isEmpty()) {
//                binding.tvPrice.setText(fieldDetail.getPrice());
//                binding.tvCurrency.setText(fieldDetail.getCurrency());
//            }
//            else {
//                if (offerDiscountType.isEmpty() || offerDiscountType.equalsIgnoreCase("amount")) {
//                    double p = Double.parseDouble(price) - offerDiscount;
//                    p = p + selectedFacilityPrice;
//                    binding.tvPrice.setText(String.valueOf(p));
//                    binding.tvCurrency.setText(fieldDetail.getCurrency());
//
//                }
//                else {
//                    double p = Double.parseDouble(price);
//                    p = p - ((offerDiscount / 100) * p);
//                    p = p + selectedFacilityPrice;
//                    binding.tvPrice.setText((String.valueOf(p)));
//                    binding.tvCurrency.setText(fieldDetail.getCurrency());
//
//                }
//
//            }
        }
    }

    private double calculateFacPrice() {
        double p = 0;
        for (OleClubFacility facility : oleFacilityAdapter.selectedFacility) {
            if (facility.getPrice() != 0) {
                if (facility.getType().equalsIgnoreCase("SELECTABLE")) {
                    p = p + (Double.parseDouble(String.valueOf(facility.getPrice())) * facility.getQty());
                }
                else {
                    p = p + Double.parseDouble(String.valueOf(facility.getPrice()));
                }
            }
        }
        return p;
    }
    private void getAllSlots(boolean isLoader, String date) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getAllSlots(clubId, selectedDuration, date);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            fieldList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject fieldObject = arr.getJSONObject(i);
                                int id = fieldObject.getInt("id");
                                Field field = gson.fromJson(fieldObject.toString(), Field.class);
                                field.setId(String.valueOf(id));
                                fieldList.add(field);
                            }
                            fieldAdapter.setSelectedDate(selectedDate);
                            fieldAdapter.notifyDataSetChanged();
                        }
                        else {
                            fieldList.clear();
                            fieldAdapter.notifyDataSetChanged();
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        fieldList.clear();
                        fieldAdapter.notifyDataSetChanged();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    fieldList.clear();
                    fieldAdapter.notifyDataSetChanged();
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                fieldList.clear();
                fieldAdapter.notifyDataSetChanged();
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

//
//    private void notificationAPI(String start, String end, String phone) {
//        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.notifyAvailability(Functions.getAppLang(getContext()), fieldId, club.getId(), start, end, phone, Functions.getPrefValue(getContext(), Constants.kUserID));
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                }
//                else {
//                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
//                }
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                }
//                else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
//    }
//

    private void addBookingAPI(String registeredUserId, String userName, String countryId, String phone, String discount, String paymentType, String days, String fromDate,
                               String toDate, String addPrice, int promoDiscount, String promoId,
                               double offerDis, String padelPlayers, String padelPlayersForPayment, String schedule, String day, String time) {
        double totalP = 0;
        if (!addPrice.isEmpty()) {
            totalP = Double.parseDouble(price) + Double.parseDouble(addPrice);
        }
        else {
            totalP = Double.parseDouble(price);
        }

        if (offerDiscountType.equalsIgnoreCase("percent")) {
            double p = Double.parseDouble(price);
            offerDis = (offerDis / 100) * p;
        }

        if (selectedSlotDuration.isEmpty()) {
            selectedSlotDuration = selectedDuration;
        }
//        String fieldType = club.getClubType();
        String facilities = "";
        try {
            JSONArray array = new JSONArray();
            for (OleClubFacility facility : oleFacilityAdapter.selectedFacility) {
                JSONObject object = new JSONObject();
                object.put("facility_id", facility.getFacilityId());
                if (facility.getQty() > 0) {
                    object.put("quantity", String.valueOf(facility.getQty()));
                }
                else {
                    object.put("quantity", "");
                }
                array.put(object);
            }

            facilities = array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        String ladySlot = fieldDetail.getSlotList().get(selectedSlotIndex).getLadySlot();

        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing"); //fieldOfferId > empty string , valued integer issue
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.ownerBooking(club.getId(), fieldId, selectedDate, selectedStartTime, selectedEndTime, totalP, discount, fieldOfferId, ""
                ,facilities, "", "", countryId, phone, userName, schedule, toDate); //selectedShift, paymentType, , , selectedFacilityPrice, offerDis, "0", promoDiscount, promoId, days, fromDate, toDate, Functions.getIPAddress(), fieldType, padelPlayers, padelPlayersForPayment, ladySlot, isWaitingUser, registeredUserId
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            if (object.has("payload")){
                                Intent intent = new Intent(getContext(), BookingConflictActivity.class);
                                intent.putExtra("responseObject", object.toString());
                                bookingConflictResultLauncher.launch(intent);
//                                Intent intent = new Intent(getContext(), BookingConflictActivity.class);
//                                startActivity(intent);
                            }
                            else{
                                Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                                Intent intent = new Intent(getContext(), BookingConfirmedActivity.class);
                                intent.putExtra("date", day);
                                intent.putExtra("time", time);
                                bookingConfirmedResultLauncher.launch(intent);
                            }
                        }
                        else if (object.getInt(Constants.kStatus) == 409) {
                            Functions.showAlert(getContext(), getString(R.string.success), object.getString(Constants.kMsg), new OleCustomAlertDialog.OnDismiss() {
                                @Override
                                public void dismiss() {
                                    finish();
                                }
                            });
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



    private void scheduleBooking(String payload, String newDates) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.scheduleBooking(payload, newDates);
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


    ActivityResultLauncher<Intent> bookingConfirmedResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
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

    ActivityResultLauncher<Intent> bookingConflictResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        String payload = Optional.ofNullable(extras.getString("payload")).orElse("");
                        String newDates = Optional.ofNullable(extras.getString("dates")).orElse("");
                        if (!payload.isEmpty() || !newDates.isEmpty()) {
                            scheduleBooking(payload, newDates);
                        }
                    }
                }
            }
    );

//
//    private void cancelConfirmBookingAPI(boolean isLoader, String status, String bookingId, int pos, int fieldPos) {
//        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
//        Call<ResponseBody> call = AppManager.getInstance().apiInterface.cancelConfirmBooking(Functions.getAppLang(getContext()), bookingId, status, "", "", "", "", "", Functions.getPrefValue(getContext(), Constants.kUserID));
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Functions.hideLoader(hud);
//                if (response.body() != null) {
//                    try {
//                        JSONObject object = new JSONObject(response.body().string());
//                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
//                            if (status.equalsIgnoreCase("cancel")) {
//                                fieldList.get(fieldPos).getSlotList().get(pos).setStatus("available");
//                                fieldList.get(fieldPos).getSlotList().get(pos).setUserName("");
//                                OleFastBookingFieldAdapter.ViewHolder holder = (OleFastBookingFieldAdapter.ViewHolder) binding.fieldRecyclerVu.findViewHolderForAdapterPosition(fieldPos);
//                                if (holder != null) {
//                                    holder.slotAdapter.notifyItemChanged(pos);
//                                }
//                            }
//                        }
//                        else {
//                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
//                    }
//                }
//                else {
//                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Functions.hideLoader(hud);
//                if (t instanceof UnknownHostException) {
//                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
//                }
//                else {
//                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
//                }
//            }
//        });
//    }

//
    protected void showWaitingUsersList(BookingSlot slot) {


//        Intent intent = new Intent(getContext(), BookingWaitingUserListActivity.class);
//        intent.putExtra("booking_id", slot.getBookingId());
//        intent.putExtra("date", slot.getDate());
//        intent.putExtra("start", slot.getStart());
//        intent.putExtra("end", slot.getEnd());
//        intent.putExtra("club_id", club.getId());
//        intent.putExtra("field_id", fieldId);
//        intent.putExtra("booking_status", slot.getStatus());
//        bookingWaitingUserListResultLauncher.launch(intent);



        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment existingFragment = fragmentManager.findFragmentByTag("showWaitingUsersListDialogFragment");
        if (existingFragment != null) {
            fragmentTransaction.remove(existingFragment);
        }
        fragmentTransaction.addToBackStack(null);
        showWaitingUsersListDialogFragment dialogFragment = new showWaitingUsersListDialogFragment(club.getId(), fieldId, slot.getDate(), slot.getStart(), slot.getEnd(), slot.getStatus());
        dialogFragment.setDialogCallback((df,type, id, name, phone) -> {
            df.dismiss();
            if (type.equalsIgnoreCase("booking")){
                if (!phone.isEmpty()){
//                    isWaitingUser = "1";
                    bookClicked(name, phone);
                }
            }else{
                if (id !=null){
                    removeWaitingUser(id);
                }
            }
        });
        dialogFragment.show(fragmentTransaction, "showWaitingUsersListDialogFragment");
    }

    private void removeWaitingUser(String id) {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.removeWaitingUser(Integer.parseInt(id));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), getString(R.string.success), FancyToast.SUCCESS);
                            getAllSlots(true, selectedDate);
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

                            daysAdapter.setDataSource(arrDate.toArray());
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

    private void getClubFacilities(boolean isLoader, String clubId) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getClubFacilities(Integer.parseInt(clubId));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray arr = object.getJSONArray(Constants.kData);
                            facilityList.clear();
                            Gson gson = new Gson();
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject facilityObject = arr.getJSONObject(i);
                                int maxQuantity = facilityObject.getInt("max_quantity");
                                OleClubFacility facility = gson.fromJson(facilityObject.toString(), OleClubFacility.class);
                                facility.setMaxQuantity(String.valueOf(maxQuantity));
                                facilityList.add(facility);
                            }

                            for (OleClubFacility facility : facilityList) {
                                if (facility.getPrice() == 0) {
                                    oleFacilityAdapter.selectedFacility.add(facility);
                                }
                            }
                            binding.facRecyclerVu.setAdapter(oleFacilityAdapter);


//                            Iterator<OleClubFacility> iterator = facilityList.iterator();
//                            while (iterator.hasNext()) {
//                                OleClubFacility facility = iterator.next();
//                                if (facility.getPrice().equalsIgnoreCase("0")) {
//                                    oleFacilityAdapter.selectedFacility.add(facility);
//                                    iterator.remove();
//                                }
//                            }
//                            binding.facRecyclerVu.setAdapter(oleFacilityAdapter);
//                            oleFacilityAdapter.setSelectedDate(selectedDate);
                            oleFacilityAdapter.notifyDataSetChanged();

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

    protected void showConfirmCancelWaitingBookingDetails(BookingSlot slot) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("ConfirmCancelWaitingBookingSheetFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        ConfirmCancelWaitingBookingSheetFragment dialogFragment = new ConfirmCancelWaitingBookingSheetFragment(slot.getBookingId());
        dialogFragment.setDialogCallback(new ConfirmCancelWaitingBookingSheetFragment.ResultDialogCallback() {
            @Override
            public void didWaitingListClicked(DialogFragment df) {
                Intent intent = new Intent(getContext(), BookingWaitingUserActivity.class);
                intent.putExtra("booking_id", slot.getBookingId());
                intent.putExtra("date", slot.getDate());
                intent.putExtra("start", slot.getStart());
                intent.putExtra("end", slot.getEnd());
                intent.putExtra("club_id", club.getId());
                intent.putExtra("field_id", fieldId);
                intent.putExtra("booking_status", slot.getStatus());
                bookingWaitingUserResultLauncher.launch(intent);
            }

            @Override
            public void didConfirmClicked(DialogFragment df) {
                confirmBooking(Integer.parseInt(slot.getBookingId()));
            }

            @Override
            public void didCancelClicked(DialogFragment df) {
                showBookingCancellationDetails(slot.getBookingId());
            }

            @Override
            public void callOption(DialogFragment df) {
                showCallOptions(slot.getUserPhone());
            }
        });
        dialogFragment.show(fragmentTransaction, "ConfirmCancelWaitingBookingSheetFragment");

    }

    protected void showCallOptions(String phone) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("CallSheetFragment");
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        CallSheetFragment dialogFragment = new CallSheetFragment();
        dialogFragment.setDialogCallback(new CallSheetFragment.ResultDialogCallback() {
            @Override
            public void callClicked(DialogFragment df) {
                Intent callIntent = new Intent(Intent.ACTION_VIEW);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }

            @Override
            public void callAppClicked(DialogFragment df) {

            }

            @Override
            public void whatsappClicked(DialogFragment df) {
                Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
                whatsappIntent.setData(Uri.parse("https://wa.me/" + phone));
                startActivity(whatsappIntent);

            }
        });
        dialogFragment.show(fragmentTransaction, "CallSheetFragment");
    }

    ActivityResultLauncher<Intent> bookingWaitingUserResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
//                        String payload = Optional.ofNullable(extras.getString("payload")).orElse("");
//                        String newDates = Optional.ofNullable(extras.getString("dates")).orElse("");
//                        if (!payload.isEmpty() || !newDates.isEmpty()) {
//                            scheduleBooking(payload, newDates);
//                        }
                    }
                }
            }
    );


    ActivityResultLauncher<Intent> bookingWaitingUserListResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
//                        String payload = Optional.ofNullable(extras.getString("payload")).orElse("");
//                        String newDates = Optional.ofNullable(extras.getString("dates")).orElse("");
//                        if (!payload.isEmpty() || !newDates.isEmpty()) {
//                            scheduleBooking(payload, newDates);
//                        }
                    }
                }
            }
    );



    public void calculateOffer(boolean isOfferedSlot, BookingSlot slot){
        if (isOfferedSlot){
            if (slot.getFieldOfferType().isEmpty() || slot.getFieldOfferType().equalsIgnoreCase("FLAT_AMOUNT")){
//                price = String.valueOf(Integer.parseInt(price) - slot.getFieldOfferValue());
//                binding.tvPrice.setText(price);

                double p = Double.parseDouble(price) - slot.getFieldOfferValue();
                p = p + selectedFacilityPrice;
                price = String.valueOf(p);
                binding.tvPrice.setText(String.valueOf(p));

            }
            else{
                double p = Double.parseDouble(price);
                p = p - (((double) slot.getFieldOfferValue() * p) / 100);
                p = p + selectedFacilityPrice;
                price = String.valueOf(p);
                binding.tvPrice.setText(String.valueOf(p));
            }
        }
    }





}