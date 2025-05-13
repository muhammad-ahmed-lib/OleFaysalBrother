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
import java.util.List;
import ae.oleapp.R;
import ae.oleapp.models.OleKeyValuePair;

public class SlotPatternAdapter extends RecyclerView.Adapter<SlotPatternAdapter.ViewHolder> {

    private final Context context;
    private final List<OleKeyValuePair> list;
    private ItemClickListener itemClickListener;
    private String selectedDuration = "";
    private int selectedIndex = -1;

    public SlotPatternAdapter(Context context, List<OleKeyValuePair> list, String duration) {
        this.context = context;
        this.list = list;
        this.selectedDuration = duration;

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedDuration(String selectedDuration) {
        this.selectedDuration = selectedDuration;
        this.notifyDataSetChanged();
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
        if (!list.isEmpty()){
            holder.tvName.setText(list.get(position).getValue());

            if (list.get(position).getKey().equalsIgnoreCase(selectedDuration)) {
                holder.mainLay.setStrokeColor(ContextCompat.getColor(context, R.color.v5greenColor));
                holder.clockImg.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.v5_slot_clock_ic_green));
                holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.v5greenColor));
            }
            else {
                holder.mainLay.setStrokeColor(ContextCompat.getColor(context, R.color.transparent));
                holder.clockImg.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.v5_slot_clock_ic));
                holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.v5_text_color));

            }
        }
        else {
            holder.mainLay.setVisibility(View.GONE);
        }


        holder.mainLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getBindingAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        ImageView clockImg;
        MaterialCardView mainLay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_slot);
            clockImg = itemView.findViewById(R.id.clock_img);
            mainLay = itemView.findViewById(R.id.slot_vu);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}