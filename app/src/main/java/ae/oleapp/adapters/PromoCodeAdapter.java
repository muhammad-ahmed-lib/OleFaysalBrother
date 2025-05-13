package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
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
import ae.oleapp.models.Club;
import ae.oleapp.models.PromoCode;

public class PromoCodeAdapter extends RecyclerView.Adapter<PromoCodeAdapter.ViewHolder> {

    private final Context context;
    private final List<PromoCode> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";
    private int selectedIndex = -1;

    public PromoCodeAdapter(Context context, List<PromoCode> list) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PromoCode promo = list.get(position);
        holder.tvClubName.setText(promo.getClub().getName());

        if (promo.getPromoType().equalsIgnoreCase("NEW_USER")){
            holder.tvPromoType.setText("New User");
            holder.minImgVu.setImageResource(R.drawable.new_user_ic);
            holder.imgVu.setImageResource(R.drawable.new_user_ic);
        }
        else if (promo.getPromoType().equalsIgnoreCase("BIRTHDAY")) {
            holder.tvPromoType.setText("Birthday");
            holder.minImgVu.setImageResource(R.drawable.birday_ic);
            holder.imgVu.setImageResource(R.drawable.birday_ic);
        }
        else{
            holder.tvPromoType.setText("Normal");
            holder.minImgVu.setImageResource(R.drawable.promo_codes_ic);
            holder.imgVu.setImageResource(R.drawable.promo_codes_ic);
        }

        if (promo.getDiscountType().equalsIgnoreCase("FLAT_AMOUNT")){
            holder.tvDiscount.setText(String.format("%s %s", promo.getDiscount(), promo.getCurrency()));
        }
        else {
            holder.tvDiscount.setText(String.format("%s %s", promo.getDiscount(), "% off"));
        }

        holder.tvName.setText(promo.getName());
        holder.tvDate.setText(String.format("%s %s %s", promo.getStartDate(), context.getString(R.string.to), promo.getEndDate()));
        holder.tvTotalDiscount.setText(String.valueOf(promo.getTotalDiscount()));
        holder.tvApplied.setText(String.valueOf(promo.getTotalUsed()));
        holder.tvEligible.setText(String.valueOf(promo.getTotalEligible()));

        if (promo.getStatus().equalsIgnoreCase("Active")){
            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.v5greenColor));
        }
        else{
            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
        }

        holder.btnPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getBindingAdapterPosition());
            }
        });
        holder.discountLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.discountClicked(v, holder.getBindingAdapterPosition());
            }
        });
        holder.appliedLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.appliedClicked(v, holder.getBindingAdapterPosition());
            }
        });
        holder.eligibleLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.eligibleClicked(v, holder.getBindingAdapterPosition());
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvClubName, tvPromoType, tvDiscount, tvTotalDiscount, tvName, tvDate, tvCurrency, tvApplied, tvEligible;
        ImageView minImgVu, imgVu;
        MaterialCardView btnPromo;
        CardView discountLay, appliedLay, eligibleLay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvPromoType = itemView.findViewById(R.id.tv_promo_type);
            tvDiscount = itemView.findViewById(R.id.tv_discount);
            tvTotalDiscount = itemView.findViewById(R.id.tv_total_discount);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvCurrency = itemView.findViewById(R.id.tv_curr);
            tvApplied = itemView.findViewById(R.id.tv_applied);
            tvEligible = itemView.findViewById(R.id.tv_eligible);
            minImgVu = itemView.findViewById(R.id.mini_promo_img);
            imgVu = itemView.findViewById(R.id.promo_img);
            btnPromo = itemView.findViewById(R.id.btn_promo);
            discountLay = itemView.findViewById(R.id.discount_lay);
            appliedLay = itemView.findViewById(R.id.applied_lay);
            eligibleLay = itemView.findViewById(R.id.eligible_lay);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void discountClicked(View view, int pos);
        void appliedClicked(View view, int pos);
        void eligibleClicked(View view, int pos);
    }
}