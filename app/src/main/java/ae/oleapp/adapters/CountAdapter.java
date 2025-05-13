package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleKeyValuePair;

public class CountAdapter extends RecyclerView.Adapter<CountAdapter.ViewHolder> {

    private final Context context;
    private final List<OleKeyValuePair> list;
    private OnItemClickListener onItemClickListener;
    private int selectedIndex = -1;

    public CountAdapter(Context context, List<OleKeyValuePair> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.count_vu, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleKeyValuePair count = list.get(position);
        holder.tvCount.setText(count.getValue());

        if (selectedIndex == position) {
            holder.tvCount.setTextColor(ContextCompat.getColor(context, R.color.whiteColor));
            holder.countVu.setStrokeColor(ContextCompat.getColor(context, R.color.club_selection_color));
            holder.countVu.setCardBackgroundColor(ContextCompat.getColor(context, R.color.v5greenColor));
        }
        else{
            holder.tvCount.setTextColor(ContextCompat.getColor(context, R.color.v5_text_color));
            holder.countVu.setStrokeColor(ContextCompat.getColor(context, R.color.v5_text_color));
            holder.countVu.setCardBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        }

        holder.countVu.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.OnItemClick(v, holder.getBindingAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvCount;
        MaterialCardView countVu;

        ViewHolder(View itemView) {
            super(itemView);

            tvCount = itemView.findViewById(R.id.tv_count);
            countVu = itemView.findViewById(R.id.count_vu);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}