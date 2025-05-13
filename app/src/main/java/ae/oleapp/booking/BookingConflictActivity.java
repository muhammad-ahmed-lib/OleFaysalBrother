package ae.oleapp.booking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
import ae.oleapp.databinding.ActivityBookingConflictBinding;
import ae.oleapp.databinding.OleactivityFastBookingBinding;
import ae.oleapp.models.BookingSlot;
import ae.oleapp.models.Club;
import ae.oleapp.models.Field;
import ae.oleapp.models.OleBookingListDate;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;


public class BookingConflictActivity extends BaseActivity implements View.OnClickListener {

    private ActivityBookingConflictBinding binding;
    private final List<Field> fieldList = new ArrayList<>();
    private OleFastBookingFieldAdapter fieldAdapter;
    private int selectedFieldIndex = -1;
    private final String selectedDate = "";
    private int selectedSlotIndex = -1;
    private Field fieldDetail;
    private final List<OleClubFacility> facilityList = new ArrayList<>();
    private final String selectedDuration = "";
    private String selectedSlotDuration = "";
    private final List<OleBookingListDate> arrDate = new ArrayList<>();
    private double offerDiscount = 0;
    private final int clubIndex = -1;
    private String fieldId = "";
    private String selectedStartTime = "";
    private String selectedEndTime = "";
    private String selectedSlotTime = "";
    private String selectedShift = "";
    private String payload = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingConflictBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            String objectString = bundle.getString("responseObject");
            if (objectString != null) {
                try {
                    JSONObject receivedObject = new JSONObject(objectString);
                    JSONArray data = receivedObject.getJSONArray(Constants.kData);
                    if (receivedObject.has("payload")){
                        payload = receivedObject.getString("payload");
                    }
                    if (data.length() > 0) {
                        Gson gson = new Gson();
                        fieldList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            fieldList.add(gson.fromJson(data.get(i).toString(), Field.class));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        LinearLayoutManager fieldLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.fieldRecyclerVu.setLayoutManager(fieldLayoutManager);
        fieldAdapter = new OleFastBookingFieldAdapter(getContext(), fieldList, selectedDate, true);
        fieldAdapter.setPadel(false);
        fieldAdapter.setOnItemClickListener(fieldClickListener);
        binding.fieldRecyclerVu.setAdapter(fieldAdapter);

        binding.btnContinue.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.btnSkip.setOnClickListener(this);

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
//                confirmCancelBookingAlert(slot.getBookingId(), slotPos, fieldPos, slot.getBookingStatus());
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
//            OleBookingSlot slot = fieldList.get(fieldPos).getSlotList().get(slotPos);
//            if (!slot.getStatus().equalsIgnoreCase("available")) {
//                showBookingAlert(slot.getStart(), slot.getEnd());
//            }

//            if (!slot.getStatus().equalsIgnoreCase("available")) {
//                showWaitingUsersList(slot);
//            } else if (slot.getStatus().equalsIgnoreCase("available") && !slot.getWaitingList().isEmpty()) {
//                showWaitingUsersList(slot);
//                selectedFieldIndex = fieldPos;
//                selectedSlotIndex = slotPos;
//                fieldDetail = fieldList.get(fieldPos);
//                fieldId = fieldDetail.getId();
//                slotClicked(fieldHolder, v, slot);
//            }
        }
    };


    private void slotClicked(OleFastBookingFieldAdapter.ViewHolder fieldHolder, OleBookingSlotAdapter.ViewHolder v, BookingSlot slot) {
            selectedSlotDuration = v.slotDuration;
            selectedStartTime = slot.getStart();
            selectedEndTime = slot.getEnd();
            selectedSlotTime = slot.getSlot();
            selectedShift = slot.getShift();
            offerDiscount = 0;
            fieldHolder.slotAdapter.setSelectedSlotIndex(selectedSlotIndex, selectedDate);

    }




    @Override
    public void onClick(View v) {
        if (v == binding.btnBack || v == binding.btnSkip){
            finish();
        }
        else if (v == binding.btnContinue) {
            if (selectedSlotIndex == -1 || payload.isEmpty()){
                Functions.showToast(getContext(), "Please select slots or click skip", FancyToast.ERROR);
                return;
            }
                String newDates = "";
                try {
                    JSONArray array = new JSONArray();
                    for (BookingSlot slot : fieldAdapter.selectedSlotsGlobal) {
                        JSONObject object = new JSONObject();
                        object.put("date", slot.getDate());
                        object.put("start", slot.getStart());
                        object.put("end", slot.getEnd());
                        array.put(object);
                    }
                    newDates = array.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (newDates.isEmpty()){
                    Functions.showToast(getContext(), "Please select slots", FancyToast.ERROR);
                    return;
                }

            Intent intent = new Intent();
            intent.putExtra("payload", payload);
            intent.putExtra("dates", newDates);
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}