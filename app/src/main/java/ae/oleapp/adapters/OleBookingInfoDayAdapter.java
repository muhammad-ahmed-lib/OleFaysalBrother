package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ae.oleapp.R;
import ae.oleapp.models.OleKeyValuePair;

public class OleBookingInfoDayAdapter extends RecyclerView.Adapter<OleBookingInfoDayAdapter.ViewHolder> {

    private final Context context;
    private final List<OleKeyValuePair> list;
    private OnItemClickListener onItemClickListener;
    private final List<OleKeyValuePair> selectedDays = new ArrayList<>();

    public OleBookingInfoDayAdapter(Context context, List<OleKeyValuePair> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<OleKeyValuePair> getSelectedDays() {
        return selectedDays;
    }

    public void selectDay(OleKeyValuePair day) {
        int index = isExist(day.getKey());
        if (index == -1) {
            selectedDays.add(day);
        }
        else {
            selectedDays.remove(index);
        }
        notifyDataSetChanged();
    }

    public int isExist(String dayId) {
        int index = -1;
        for (int i = 0; i < selectedDays.size(); i++) {
            if (selectedDays.get(i).getKey().equalsIgnoreCase(dayId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.olerank_date, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvDate.setText(list.get(position).getValue());

        if (isExist(list.get(position).getKey()) != -1) {


            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.whiteColor));
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.v5greenColor));
//
//            holder.tvDate.setTextColor(context.getResources().getColor(R.color.whiteColor));
//            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.blueColorNew));
//            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.transparent));
        }
        else {
            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.darkTextColor));
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.whiteColor));

//            holder.tvDate.setTextColor(context.getResources().getColor(R.color.darkTextColor));
//            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
//            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.separatorColor));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClick(v, holder.getBindingAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView cardView;
        TextView tvDate;

        ViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_vu);
            tvDate = itemView.findViewById(R.id.tv_date);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}