package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Loyalty;
import ae.oleapp.models.PromoCode;

public class LoyaltyAdapter extends RecyclerView.Adapter<LoyaltyAdapter.ViewHolder> {

    private final Context context;
    private final List<Loyalty> list;
    private ItemClickListener itemClickListener;

    public LoyaltyAdapter(Context context, List<Loyalty> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.loyalty_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Loyalty loyalty = list.get(position);
        holder.tvClubName.setText(loyalty.getClub().getName());

        if (loyalty.getDiscountType().equalsIgnoreCase("FLAT_AMOUNT")){
            holder.tvDiscount.setText(String.format("%s %s", loyalty.getDiscount(), loyalty.getCurrency()));
        }
        else {
            holder.tvDiscount.setText(String.format("%s %s", loyalty.getDiscount(), "% off"));
        }

        holder.tvName.setText(loyalty.getTitle());
        holder.tvDate.setText(String.format("%s %s %s", loyalty.getStartDate(), context.getString(R.string.to), loyalty.getEndDate()));
        holder.tvTotalAmount.setText(String.valueOf(loyalty.getTotalAmount()));
        holder.tvTotalPlayer.setText(String.valueOf(loyalty.getTotalPlayers()));
        holder.tvNewPlayer.setText(String.valueOf(loyalty.getNewPlayers()));

        holder.btnPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getBindingAdapterPosition());
            }
        });

        if (loyalty.getStatus().equalsIgnoreCase("Active")){
            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.v5greenColor));
        }
        else{
            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvClubName, tvTotalAmount, tvDiscount, tvTotalDiscount, tvName, tvDate, tvTotalPlayer, tvNewPlayer;
        MaterialCardView btnPromo;
        CardView valueCardVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTotalAmount = itemView.findViewById(R.id.tv_total_amount);
            tvTotalPlayer = itemView.findViewById(R.id.tv_total_player);
            tvNewPlayer = itemView.findViewById(R.id.tv_pp_count);
            valueCardVu = itemView.findViewById(R.id.value_card_vu);
            btnPromo = itemView.findViewById(R.id.btn_promo);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}