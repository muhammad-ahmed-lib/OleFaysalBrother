package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.GeneralField;
import ae.oleapp.models.OleKeyValuePair;

public class UniversalSlotPatternAdapter extends RecyclerView.Adapter<UniversalSlotPatternAdapter.ViewHolder> {

    private final Context context;
    private final List<OleKeyValuePair> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";
    private int selectedIndex = -1;
    private final List<OleKeyValuePair> selectedDurationIds =  new ArrayList<>();

    public UniversalSlotPatternAdapter(Context context, List<OleKeyValuePair> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.slot_pattern_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleKeyValuePair duration = list.get(position);
        holder.tvName.setText(duration.getValue());

        if (isExist(duration.getKey()) != -1) {
            holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.v5greenColor));
            holder.clockImg.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.v5_slot_clock_ic_green));
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.v5greenColor));
        }
        else {
            holder.cardView.setStrokeColor(ContextCompat.getColor(context, R.color.transparent));
            holder.clockImg.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.v5_slot_clock_ic));
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.v5_text_color));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getBindingAdapterPosition());
            }
        });
    }

    public List<OleKeyValuePair> getSelectedDurationIds() {
        return selectedDurationIds;
    }

    public void selectedDurationIds(OleKeyValuePair field) {
        int index = isExist(field.getKey());
        if (index == -1) {
            selectedDurationIds.add(field);
        }
        else {
            selectedDurationIds.remove(index);
        }
        notifyDataSetChanged();
    }

    public int isExist(String fieldId) {
        int index = -1;
        for (int i = 0; i < selectedDurationIds.size(); i++) {
            if (selectedDurationIds.get(i).getKey().equalsIgnoreCase(fieldId)) {
                index = i;
                break;
            }
        }
        return index;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        ImageView clockImg;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_slot);
            clockImg = itemView.findViewById(R.id.clock_img);
            cardView = itemView.findViewById(R.id.slot_vu);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}