package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.BookingSlot;
import ae.oleapp.models.Field;
import ae.oleapp.models.OleClubFacility;

public class OleFastBookingFieldAdapter extends RecyclerView.Adapter<OleFastBookingFieldAdapter.ViewHolder> {

    private final Context context;
    private final List<Field> list;
    private boolean isPadel = false;
    private OnItemClickListener onItemClickListener;
    private String selectedDate = "";
    private boolean conflict = false;
    public String newDates = "";
    public ViewHolder newDatesHolder;
    public List<BookingSlot> slotsList = new ArrayList<>();
    public List<BookingSlot> selectedSlotsGlobal = new ArrayList<>();





    public OleFastBookingFieldAdapter(Context context, List<Field> list, String selectedDate, boolean conflict) {
        this.context = context;
        this.list = list;
        this.selectedDate = selectedDate;
        this.conflict = conflict;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setPadel(boolean padel) {
        isPadel = padel;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olefast_booking_field, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Field field = list.get(position);
        if (!conflict){
            if (field.getSize() != null && !field.getSize().isEmpty()) {
                holder.tvName.setText(String.format("%s (%s)", field.getName(), field.getSize()));
            }
            else {
                holder.tvName.setText(field.getName());
            }

            LinearLayoutManager slotLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(slotLayoutManager);
            holder.slotAdapter = new OleBookingSlotAdapter(context, field.getSlotList(), isPadel);
            holder.slotAdapter.setFieldPos(position);
            holder.slotAdapter.setSelectedDate(selectedDate);
            holder.slotAdapter.setOnItemClickListener(new OleBookingSlotAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(OleBookingSlotAdapter.ViewHolder v, int pos) {
                    onItemClickListener.OnItemClick(holder, v, pos, holder.getBindingAdapterPosition());
                }

                @Override
                public void OnItemLongClick(OleBookingSlotAdapter.ViewHolder v, int pos) {
                    onItemClickListener.OnItemLongClick(holder, v, pos, holder.getBindingAdapterPosition());
                }
            });
            holder.recyclerView.setAdapter(holder.slotAdapter);
        }else{

            slotsList.clear();
            for (int i = 0; i < field.getSlotList().size(); i++) {
                BookingSlot slot = field.getSlotList().get(i);
                slot.setSlotId(String.valueOf(i+1));
                slotsList.add(slot);
            }

            holder.tvName.setText(field.getDate());
            LinearLayoutManager slotLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(slotLayoutManager);
            holder.slotAdapter = new OleBookingSlotAdapter(context, slotsList, isPadel);
            holder.slotAdapter.setFieldPos(position);
            holder.slotAdapter.setSelectedDate(selectedDate);
            holder.slotAdapter.setOnItemClickListener(new OleBookingSlotAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(OleBookingSlotAdapter.ViewHolder v, int pos) {
//                    holder.slotAdapter.setSelectedSlots(slotsList.get(pos));
                    if (selectedSlotsGlobal.contains(slotsList.get(pos))) {
                        selectedSlotsGlobal.remove(slotsList.get(pos));
                    } else {
                        selectedSlotsGlobal.add(slotsList.get(pos));
                    }
                    onItemClickListener.OnItemClick(holder, v, pos, holder.getBindingAdapterPosition());
                }

                @Override
                public void OnItemLongClick(OleBookingSlotAdapter.ViewHolder v, int pos) {
                    onItemClickListener.OnItemLongClick(holder, v, pos, holder.getBindingAdapterPosition());
                }
            });

            holder.recyclerView.setAdapter(holder.slotAdapter);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        RecyclerView recyclerView;
        TextView tvName;
        public OleBookingSlotAdapter slotAdapter;

        ViewHolder(View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.slots_recycler_vu);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(ViewHolder fieldVu, OleBookingSlotAdapter.ViewHolder v, int slotPos, int fieldPos);
        void OnItemLongClick(ViewHolder fieldVu, OleBookingSlotAdapter.ViewHolder v, int slotPos, int fieldPos);
    }
}