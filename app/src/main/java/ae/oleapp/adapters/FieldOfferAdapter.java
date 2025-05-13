package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.FieldOffer;
import ae.oleapp.models.Loyalty;

public class FieldOfferAdapter extends RecyclerView.Adapter<FieldOfferAdapter.ViewHolder> {

    private final Context context;
    private final List<FieldOffer> list;
    private ItemClickListener itemClickListener;

    public FieldOfferAdapter(Context context, List<FieldOffer> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.field_offer_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FieldOffer offer = list.get(position);
        holder.tvClubName.setText(offer.getClub().getName());

        if (offer.getDiscountType().equalsIgnoreCase("FLAT_AMOUNT")){
            holder.tvDiscount.setText(String.format("%s %s", offer.getDiscount(), offer.getCurrency()));
        }
        else {
            holder.tvDiscount.setText(String.format("%s %s", offer.getDiscount(), "% off"));
        }

        holder.tvName.setText(offer.getTitle());
        holder.tvDate.setText(String.format("%s %s %s", offer.getStartDate(), context.getString(R.string.to), offer.getEndDate()));
        holder.tvTotalDiscount.setText(String.valueOf(offer.getTotalDiscount()));
        holder.tvTotalBooking.setText(String.valueOf(offer.getTotalBookings()));
        holder.tvNewUserCount.setText(String.valueOf(offer.getNewUsers()));


        holder.btnPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getBindingAdapterPosition());
            }
        });

        if (offer.getStatus().equalsIgnoreCase("Active")){
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

        TextView tvClubName, tvDiscount, tvName, tvDate, tvTotalDiscount, tvTotalBooking, tvNewUserCount;
        MaterialCardView btnPromo;
        CardView valueCardVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTotalBooking = itemView.findViewById(R.id.tv_total_bookings);
            tvTotalDiscount = itemView.findViewById(R.id.tv_total_discount);
            tvNewUserCount = itemView.findViewById(R.id.tv_new_user_count);
            btnPromo = itemView.findViewById(R.id.btn_promo);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}