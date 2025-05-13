package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.Day;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.OleShiftTime;
import io.github.vejei.cupertinoswitch.CupertinoSwitch;

public class v5DayAdapter extends RecyclerView.Adapter<v5DayAdapter.ViewHolder> {

    private final Context context;
    private final List<OleKeyValuePair> dayList;
    private OnItemClickListener onItemClickListener;
    private static final List<Day> selectedDays = new ArrayList<>();
    private String currentDayId = "";

    public v5DayAdapter(Context context, List<OleKeyValuePair> dayList) {
        this.context = context;
        this.dayList = dayList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setCurrentDayId(String currentDayId) {
        this.currentDayId = currentDayId;
    }

    public int checkDayExist(String dayId) {
        for (int i = 0; i < selectedDays.size(); i++) {
            if (selectedDays.get(i).getDayId().equalsIgnoreCase(dayId)) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.v5_club_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleKeyValuePair day = dayList.get(position);
        holder.tvDayName.setText(day.getValue());

        holder.mySwitch.setOnClickListener(v -> {
            if (holder.mySwitch.isChecked()) {
                holder.vuShift1.setVisibility(View.VISIBLE);
            } else {
                resetShiftViews(holder);
            }
            onItemClickListener.OnItemClick(v, holder.getBindingAdapterPosition());
        });

        holder.mySwitch.setOnStateChangeListener(new CupertinoSwitch.OnStateChangeListener() {
            @Override
            public void onChanged(CupertinoSwitch view, boolean checked) {
                int position = holder.getBindingAdapterPosition();
                onItemClickListener.OnItemClick(view, position);
            }

            @Override
            public void onSwitchOn(CupertinoSwitch view) {
                holder.vuShift1.setVisibility(View.VISIBLE);
                onItemClickListener.OnItemClick(view, holder.getBindingAdapterPosition());
            }

            @Override
            public void onSwitchOff(CupertinoSwitch view) {
                resetShiftViews(holder);
                onItemClickListener.OnItemClick(view, holder.getBindingAdapterPosition());
            }
        });

        setupShiftTimeClicks(holder);
        setupExtraShiftButtons(holder);
    }

    private void setupShiftTimeClicks(ViewHolder holder) {
        holder.tvOpenShift1.setOnClickListener(v -> timeClicked(v, holder, 1, true));
        holder.tvCloseShift1.setOnClickListener(v -> timeClicked(v, holder, 1, false));
        holder.tvOpenShift2.setOnClickListener(v -> timeClicked(v, holder, 2, true));
        holder.tvCloseShift2.setOnClickListener(v -> timeClicked(v, holder, 2, false));
    }

    private void setupExtraShiftButtons(ViewHolder holder) {
        holder.btnAddExtraShift.setOnClickListener(v -> holder.vuShift2.setVisibility(View.VISIBLE));
        holder.btnRemoveExtraShift.setOnClickListener(v -> resetShift2(holder));
    }

    private void resetShiftViews(ViewHolder holder) {
        holder.tvOpenShift1.setText("");
        holder.tvCloseShift1.setText("");
        holder.tvOpenShift2.setText("");
        holder.tvCloseShift2.setText("");
        holder.vuShift1.setVisibility(View.GONE);
        holder.vuShift2.setVisibility(View.GONE);
        removeDayFromSelected(holder.getBindingAdapterPosition());
    }

    private void resetShift2(ViewHolder holder) {
        holder.tvOpenShift2.setText("");
        holder.tvCloseShift2.setText("");
        holder.vuShift2.setVisibility(View.GONE);
    }

    private void timeClicked(View clickedView, ViewHolder holder, int shift, boolean isOpenTime) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        com.wdullaer.materialdatetimepicker.time.TimePickerDialog timePickerDialog =
                com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                        (view, hourOfDay, minute1, second) -> {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute1);
                            String formattedTime = new SimpleDateFormat("hh:mma", Locale.ENGLISH).format(calendar.getTime());

                            if (shift == 1) {
                                if (isOpenTime) {
                                    holder.tvOpenShift1.setText(formattedTime);
                                    updateShiftTime(holder.getBindingAdapterPosition(), shift, formattedTime, true);
                                } else {
                                    holder.tvCloseShift1.setText(formattedTime);
                                    updateShiftTime(holder.getBindingAdapterPosition(), shift, formattedTime, false);
                                }
                            } else if (shift == 2) {
                                if (isOpenTime) {
                                    holder.tvOpenShift2.setText(formattedTime);
                                    updateShiftTime(holder.getBindingAdapterPosition(), shift, formattedTime, true);
                                } else {
                                    holder.tvCloseShift2.setText(formattedTime);
                                    updateShiftTime(holder.getBindingAdapterPosition(), shift, formattedTime, false);
                                }
                            }
                        }, hour, minute, false);

        timePickerDialog.enableSeconds(false);
        timePickerDialog.setTimeInterval(1, 30);
        timePickerDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "Timepickerdialog");
    }

    private void updateShiftTime(int position, int shift, String time, boolean isOpenTime) {
        String dayId = dayList.get(position).getKey();
        int dayIndex = checkDayExist(dayId);

        Day day;
        if (dayIndex >= 0) {
            day = selectedDays.get(dayIndex);
        } else {
            day = new Day(); // Use the default constructor
            day.setDayId(dayId); // Set the dayId
            day.setShifting(new ArrayList<>()); // Initialize the shifting list
            selectedDays.add(day);
        }

        List<OleShiftTime> shifts = day.getShifting();
        OleShiftTime shiftTime;
        if (shift <= shifts.size()) {
            shiftTime = shifts.get(shift - 1);
        } else {
            shiftTime = new OleShiftTime();
            shifts.add(shiftTime);
        }

        if (isOpenTime) {
            shiftTime.setOpening(time);
        } else {
            shiftTime.setClosing(time);
        }
    }

    private void removeDayFromSelected(int position) {
        String dayId = dayList.get(position).getKey();
        int index = checkDayExist(dayId);
        if (index >= 0) {
            selectedDays.remove(index);
        }
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView btnAddExtraShift, btnRemoveExtraShift;
        TextView tvDayName, tvOpenShift1, tvCloseShift1, tvOpenShift2, tvCloseShift2;
        ImageView bgImg;
        CupertinoSwitch mySwitch;
        LinearLayout vuShift1, vuShift2;

        ViewHolder(View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tv_day);
            btnAddExtraShift = itemView.findViewById(R.id.btn_add_extra_shift);
            btnRemoveExtraShift = itemView.findViewById(R.id.btn_remove_extra_shift);
            bgImg = itemView.findViewById(R.id.bg_img);
            vuShift1 = itemView.findViewById(R.id.vu_shift_1);
            vuShift2 = itemView.findViewById(R.id.vu_shift_2);
            mySwitch = itemView.findViewById(R.id.my_switch);
            tvOpenShift1 = itemView.findViewById(R.id.tv_open_shift_1);
            tvCloseShift1 = itemView.findViewById(R.id.tv_close_shift_1);
            tvOpenShift2 = itemView.findViewById(R.id.tv_open_shift_2);
            tvCloseShift2 = itemView.findViewById(R.id.tv_close_shift_2);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }

    public static String getTimeJson() {
        try {
            JSONArray array = new JSONArray();
            for (Day day : selectedDays) {
                if (!day.getShifting().isEmpty()) {
                    JSONObject dayObject = new JSONObject();
                    dayObject.put("day_id", Integer.valueOf(day.getDayId()));

                    JSONArray shiftsArray = new JSONArray();
                    for (OleShiftTime shiftTime : day.getShifting()) {
                        JSONObject shiftObject = new JSONObject();
                        shiftObject.put("start_time", convertTo24HourFormat(shiftTime.getOpening()));
                        shiftObject.put("end_time", convertTo24HourFormat(shiftTime.getClosing()));
                        shiftsArray.put(shiftObject);
                    }

                    dayObject.put("shifts", shiftsArray);
                    array.put(dayObject);
                }
            }
            return array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String convertTo24HourFormat(String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mma", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
            return outputFormat.format(inputFormat.parse(time));
        } catch (Exception e) {
            e.printStackTrace();
            return time;
        }
    }
}
