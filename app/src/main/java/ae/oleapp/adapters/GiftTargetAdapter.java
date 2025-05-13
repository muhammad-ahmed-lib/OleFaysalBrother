package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.GiftTarget;

public class GiftTargetAdapter extends RecyclerView.Adapter<GiftTargetAdapter.ViewHolder> {

    private final Context context;
    private final List<GiftTarget> list;
    private ItemClickListener itemClickListener;
    private int selectedId;
    private int selectedIndex = -1;

    public GiftTargetAdapter(Context context, List<GiftTarget> list ) {
        this.context = context;
        this.list = list;

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedId(int selectedId) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.universal_round_green_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GiftTarget giftTarget =  list.get(position);
        holder.tvName.setText(giftTarget.getName());

        if (selectedIndex == position) {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.v5greenColor) );
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.v5greenColor));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.whiteColor));
        }
        else {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.whiteColor) );
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.v5_text_color));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            cardView = itemView.findViewById(R.id.card_vu);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}