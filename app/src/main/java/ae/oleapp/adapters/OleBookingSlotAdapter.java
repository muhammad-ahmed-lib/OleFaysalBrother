package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.BookingSlot;
import ae.oleapp.owner.OleBookingActivity;
import ae.oleapp.owner.OleFastBookingActivity;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import pl.droidsonroids.gif.GifImageView;

public class OleBookingSlotAdapter extends RecyclerView.Adapter<OleBookingSlotAdapter.ViewHolder> {

    private final Context context;
    private final List<BookingSlot> list;
    private int selectedSlotIndex = -1;
    private String selectedDate = "";
    private OnItemClickListener onItemClickListener;
    private boolean isPadel = false;
    private int fieldPos = -1;
    private boolean setManualWidth = false;
    public List<BookingSlot> selectedSlots = new ArrayList<>();

    public OleBookingSlotAdapter(Context context, List<BookingSlot> list, boolean isPadel) {
        this.context = context;
        this.list = list;
        this.isPadel = isPadel;
    }

    public void setFieldPos(int fieldPos) {
        this.fieldPos = fieldPos;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void setSetManualWidth(boolean setManualWidth) {
        this.setManualWidth = setManualWidth;
    }

    public void setSelectedSlots(BookingSlot slot) {
        int index = isExistInSelected(slot.getSlotId());
        if (index == -1) {
            selectedSlots.add(slot);
            notifyDataSetChanged();
        }
        else {
            selectedSlots.remove(index);
            notifyDataSetChanged();
        }
    }

    public int isExistInSelected(String slotId) {
        int index = -1;
        for (int i = 0; i < selectedSlots.size(); i++) {
            if (selectedSlots.get(i).getSlotId().equalsIgnoreCase(slotId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void setSelectedSlotIndex(int selectedIndex, String selectedDate) {
        this.selectedSlotIndex = selectedIndex;
        if (!selectedDate.equalsIgnoreCase("")) {
            this.selectedDate = selectedDate;
        }
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olebooking_slot, parent, false);
        if (setManualWidth) {
            setItemWidth(v, parent);
        }
        return new ViewHolder(v);
    }

    private void setItemWidth(View view, ViewGroup parent) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        params.width = (parent.getMeasuredWidth() / 3);
        view.setLayoutParams(params);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingSlot slot = list.get(position);

        String[] times = slot.getSlot().split("-");
        if (times.length != 2) {
            System.out.println("Invalid time slot format");
            return;
        }

        // Extract start time and AM/PM part
        String startTimeFull = times[0];
        String startTime = startTimeFull.substring(0, 5);
        String startTimeAmPm = startTimeFull.substring(5);

        // Extract end time and AM/PM part
        String endTimeFull = times[1];
        String endTime = endTimeFull.substring(0, 5);
        String endTimeAmPm = endTimeFull.substring(5);

        populateData(slot, holder);

        if (!slot.getStatus().equalsIgnoreCase("booked") && !slot.getStatus().equalsIgnoreCase("hidden")) {
            if (selectedSlotIndex == position) {
                holder.imgVuBg.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.slot_selected_gradient));
                holder.tvDuration.setTextColor(ContextCompat.getColor(context, R.color.yellowColor));
                holder.startTime.setText(startTime);
                holder.startTimeAmPm.setText(startTimeAmPm);
                holder.endTime.setText(endTime);
                holder.endTimeAmPm.setText(endTimeAmPm);

//
                holder.startTime.setTextColor(ContextCompat.getColor(context, R.color.whiteColor));
                holder.endTime.setTextColor(ContextCompat.getColor(context, R.color.whiteColor));
                holder.startTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.whiteColor));
                holder.endTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.whiteColor));
//                if (startTimeAmPm.equalsIgnoreCase("AM")){
//                    holder.startTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.v5greenColor));
//                }else{
//                    holder.startTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
//                }
//                if (endTimeAmPm.equalsIgnoreCase("AM")){
//                    holder.endTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.v5greenColor));
//                }else{
//                    holder.endTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
//                }

                holder.emptySlotImg.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.booking_active_ic));
                holder.lineVu.setBackgroundColor(ContextCompat.getColor(context, R.color.whiteColor));


//                holder.tvSlot.setTextColor(Color.WHITE);
//                holder.tvDate.setTextColor(Color.WHITE);

//                if (isPadel) {
//                    if (slot.getLadySlot() != null && slot.getLadySlot().equalsIgnoreCase("1")) {
//                        holder.imgVu.setImageResource(R.drawable.padel_slot_pink);
//                    }
//                    else {
//                        holder.imgVu.setImageResource(R.drawable.padel_slot_white);
//                    }
//                }
//                else {
//                    holder.imgVu.setImageResource(R.drawable.slot_white);
//                }
            }
            else {
                holder.imgVuBg.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.settings_field_bg));
                holder.tvDuration.setTextColor(ContextCompat.getColor(context, R.color.v5_text_hint_color_grey));
                holder.startTime.setText(startTime);
                holder.startTimeAmPm.setText(startTimeAmPm);
                holder.endTime.setText(endTime);
                holder.endTimeAmPm.setText(endTimeAmPm);


                holder.startTime.setTextColor(ContextCompat.getColor(context, R.color.v5_text_color));
                holder.endTime.setTextColor(ContextCompat.getColor(context, R.color.v5_text_color));
                holder.startTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.v5greenColor));
                holder.endTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));

                if (startTimeAmPm.equalsIgnoreCase("AM")){
                    holder.startTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.v5greenColor));
                }else{
                    holder.startTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
                }
                if (endTimeAmPm.equalsIgnoreCase("AM")){
                    holder.endTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.v5greenColor));
                }else{
                    holder.endTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
                }

                holder.emptySlotImg.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.booking_deactive_ic));


//                holder.imgVuBg.setVisibility(View.INVISIBLE);
//                holder.slotVu.setBackgroundResource(R.drawable.slot_bg_border);
//                holder.tvDuration.setTextColor(context.getResources().getColor(R.color.darkTextColor));
//                if (isPadel) {
//                    if (slot.getLadySlot() != null && slot.getLadySlot().equalsIgnoreCase("1")) {
//                        holder.imgVu.setImageResource(R.drawable.padel_slot_pink);
//                    }
//                    else {
//                        holder.imgVu.setImageResource(R.drawable.padel_slot_gray);
//                    }
//                }
//                else {
//                    holder.imgVu.setImageResource(R.drawable.slot_gray);
//                }
            }
        }

        if (context instanceof OleBookingActivity || context instanceof OleFastBookingActivity) {
            try {
                double discount = 0;
                if (context instanceof OleBookingActivity) {
                    discount = ((OleBookingActivity) context).checkOfferForSlot(slot.getStart(), slot.getEnd());
                }
                else {
//                    discount = ((OleFastBookingActivity) context).checkOfferForSlot(fieldPos, slot.getStart(), slot.getEnd());
                }
                if (discount > 0) {
                    holder.imgVuOffer.setVisibility(View.VISIBLE);
                }
                else {
                    holder.imgVuOffer.setVisibility(View.INVISIBLE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                holder.imgVuOffer.setVisibility(View.INVISIBLE);
            }
        }
        else {
            holder.imgVuOffer.setVisibility(View.INVISIBLE);
        }

        if (slot.getIsOfferedSlot()){
            holder.imgVuOffer.setVisibility(View.VISIBLE);
        }
        else{
            holder.imgVuOffer.setVisibility(View.INVISIBLE);
        }

        holder.slotVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(holder, holder.getBindingAdapterPosition());
            }
        });

        holder.slotVu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.OnItemLongClick(holder, holder.getBindingAdapterPosition());
                return true;
            }
        });
    }

    private void populateData(BookingSlot slot, ViewHolder holder) {

        String[] times = slot.getSlot().split("-");
        if (times.length != 2) {
            System.out.println("Invalid time slot format");
            return;
        }

        // Extract start time and AM/PM part
        String startTimeFull = times[0];
        String startTime = startTimeFull.substring(0, 5);
        String startTimeAmPm = startTimeFull.substring(5);

        // Extract end time and AM/PM part
        String endTimeFull = times[1];
        String endTime = endTimeFull.substring(0, 5);
        String endTimeAmPm = endTimeFull.substring(5);
//        holder.tvSlot.setText(slot.getSlot());
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//        try {
//            holder.tvDate.setVisibility(View.VISIBLE);
//            Date startDate = df.parse(slot.getStart().split(" ")[0]);
//            Date date = df.parse(selectedDate);
//            if (startDate != null && date != null) {
//                if (startDate.compareTo(date) == 0) {
//                    holder.tvDate.setVisibility(View.GONE);
//                } else {
//                    df.applyPattern("dd/MM/yyyy");
//                    holder.tvDate.setText(df.format(startDate));
//                }
//            }
//            else {
//                holder.tvDate.setVisibility(View.GONE);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            holder.tvDate.setVisibility(View.GONE);
//        }
//        if (Functions.getPrefValue(context, Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
//            holder.tvPlayerName.setText(slot.getUserName());
//            holder.nameVu.setVisibility(View.VISIBLE);
//            if (slot.getSchedule() != null && slot.getSchedule().equalsIgnoreCase("1")) {
//                holder.imgVuSchedule.setVisibility(View.VISIBLE);
//            }
//            else {
//                holder.imgVuSchedule.setVisibility(View.GONE);
//            }
//        }
//        else {
//            holder.nameVu.setVisibility(View.INVISIBLE);
//        }
        if (slot.getStatus().equalsIgnoreCase("booked") || slot.getStatus().equalsIgnoreCase("hidden")) {
//            if (isPadel) {
//                holder.imgVu.setImageResource(R.drawable.padel_booked_slots);
//            }
//            else {
//                holder.imgVu.setImageResource(R.drawable.booked_slots);
//            }

            holder.imgVuBg.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.slot_booked_gradient));
            holder.slotVu.setStrokeColor(ContextCompat.getColor(context, R.color.redBookingColor));

            holder.labelBooked.setVisibility(View.VISIBLE);
            holder.detailsHead.setVisibility(View.GONE);
            holder.btnBookingDetails.setVisibility(View.VISIBLE);
            holder.emptySlotImg.setVisibility(View.GONE);


//            holder.tvDuration.setTextColor(ContextCompat.getColor(context, R.color.yellowColor));
            holder.startTime.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
            holder.startTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
            holder.endTime.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
            holder.endTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
            holder.lineVu.setBackgroundColor(ContextCompat.getColor(context, R.color.redBookingColor));




            holder.startTime.setText(startTime);
            holder.startTimeAmPm.setText(startTimeAmPm);
            holder.endTime.setText(endTime);
            holder.endTimeAmPm.setText(endTimeAmPm);

//
//            holder.slotVu.setBackgroundResource(R.drawable.slot_bg_border_red);
//            holder.tvDuration.setTextColor(context.getResources().getColor(R.color.redColor));
//            holder.tvSlot.setTextColor(context.getResources().getColor(R.color.redColor));
//            holder.tvDate.setTextColor(context.getResources().getColor(R.color.redColor));
//            holder.imgVuBg.setVisibility(View.INVISIBLE);
            if (slot.getStatus().equalsIgnoreCase("booked") && slot.getHasWaitingList().equalsIgnoreCase("1")) {
                holder.gif.setVisibility(View.VISIBLE);
            }else{
                holder.gif.setVisibility(View.GONE);
            }
        }
        else {
//            if (isPadel) {
//                if (slot.getLadySlot() != null && slot.getLadySlot().equalsIgnoreCase("1")) {
//                    holder.imgVu.setImageResource(R.drawable.padel_slot_pink);
//                }
//                else {
//                    holder.imgVu.setImageResource(R.drawable.padel_slot_gray);
//                }
//            }
//            else {
//                holder.imgVu.setImageResource(R.drawable.slot_gray);
//            }
            holder.imgVuBg.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.settings_field_bg));
            holder.slotVu.setStrokeColor(ContextCompat.getColor(context, R.color.transparent));
            holder.labelBooked.setVisibility(View.GONE);
            holder.detailsHead.setVisibility(View.VISIBLE);
            holder.btnBookingDetails.setVisibility(View.GONE);
            holder.emptySlotImg.setVisibility(View.VISIBLE);
            holder.lineVu.setBackgroundColor(ContextCompat.getColor(context, R.color.v5_text_color));



//            holder.tvDuration.setTextColor(ContextCompat.getColor(context, R.color.yellowColor));
            holder.startTime.setTextColor(ContextCompat.getColor(context, R.color.v5_text_color));
            holder.endTime.setTextColor(ContextCompat.getColor(context, R.color.v5_text_color));

            holder.startTime.setText(startTime);
            holder.startTimeAmPm.setText(startTimeAmPm);
            holder.endTime.setText(endTime);
            holder.endTimeAmPm.setText(endTimeAmPm);

            if (startTimeAmPm.equalsIgnoreCase("AM")){
                holder.startTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.v5greenColor));
            }else{
                holder.startTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
            }
            if (endTimeAmPm.equalsIgnoreCase("AM")){
                holder.endTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.v5greenColor));
            }else{
                holder.endTimeAmPm.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
            }

            if (slot.getStatus().equalsIgnoreCase("available") && slot.getHasWaitingList().equalsIgnoreCase("1")) {
                holder.gif.setVisibility(View.VISIBLE);
            }else{
                holder.gif.setVisibility(View.GONE);
            }


//            String green = "AM";
//            String red = "PM";
//            String htmlText = slot.getSlot().replace(green,"<font color='#49D483'>"+green +"</font>");
//            htmlText = htmlText.replace(red,"<font color='#F02301'>"+red +"</font>");
//            holder.tvSlot.setText(Html.fromHtml(htmlText));
        }

        String[] arr = slot.getSlot().split("-");
        if (arr.length == 2) {
            String dur = "";
            try {
                dur = Functions.getTimeDifference(arr[0], arr[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvDuration.setText(String.format("%s %s", dur, context.getString(R.string.min)));
            if (dur.equalsIgnoreCase("60")) {
                holder.slotDuration = "1";
            }
            else if (dur.equalsIgnoreCase("90")) {
                holder.slotDuration = "1.5";
            }
            else if (dur.equalsIgnoreCase("120")) {
                holder.slotDuration = "2";
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma", Locale.ENGLISH);
            try {
                Date date = dateFormat.parse(arr[0]);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                if (hour > 6 && hour < 17) {
                    holder.imgVuNight.setImageResource(R.drawable.sun_ic_on);
                }
                else {
                    holder.imgVuNight.setImageResource(R.drawable.moon_ic_off);
                }

            } catch (ParseException e) {
                e.printStackTrace();
                holder.imgVuNight.setImageResource(0);
            }

        }
        else {
            holder.tvDuration.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgVuNight, imgVuOffer, emptySlotImg, imgVuBg;
        public RelativeLayout detailsHead, nameVu;
        TextView tvDuration, startTime, startTimeAmPm, endTime, endTimeAmPm;
        public String slotDuration = "";
        public MaterialCardView slotVu;
        CardView labelBooked, btnBookingDetails;
        GifImageView gif;
        View lineVu;

        ViewHolder(View itemView) {
            super(itemView);

            imgVuNight = itemView.findViewById(R.id.img_vu_night);
            imgVuBg = itemView.findViewById(R.id.img_vu_bg);
            imgVuOffer = itemView.findViewById(R.id.img_vu_offer);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            emptySlotImg = itemView.findViewById(R.id.empty_slot_img);
            detailsHead = itemView.findViewById(R.id.details_head);
            startTime= itemView.findViewById(R.id.start_time);
            startTimeAmPm = itemView.findViewById(R.id.start_time_ampm);
            endTime = itemView.findViewById(R.id.end_time);
            endTimeAmPm = itemView.findViewById(R.id.end_time_ampm);
            slotVu = itemView.findViewById(R.id.slot_vu);
            labelBooked = itemView.findViewById(R.id.label_booked);
            btnBookingDetails = itemView.findViewById(R.id.btn_booking_details);
            gif = itemView.findViewById(R.id.attention_gif);
            lineVu = itemView.findViewById(R.id.line_vu);

        }
    }


    public interface OnItemClickListener {
        void OnItemClick(ViewHolder v, int pos);
        void OnItemLongClick(ViewHolder v, int pos);
    }
}