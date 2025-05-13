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

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Gift;
import ae.oleapp.models.PromoCode;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.ViewHolder> {

    private final Context context;
    private final List<Gift> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";
    private int selectedIndex = -1;

    public GiftAdapter(Context context, List<Gift> list) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.gift_list_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Gift gift = list.get(position);
        holder.tvClubName.setText(gift.getClub().getName());
        if (gift.getStatus().equalsIgnoreCase("Active")){
            holder.monthCardVu.setCardBackgroundColor(ContextCompat.getColor(context, R.color.v5greenColor));
            holder.tvMonth.setTextColor(ContextCompat.getColor(context, R.color.whiteColor));
            holder.tvMonth.setText(String.format("Active: %s - %s", gift.getStartDate(), gift.getEndDate()));
        }
        else{
            holder.monthCardVu.setCardBackgroundColor(ContextCompat.getColor(context, R.color.v5grayColor));
            holder.tvMonth.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
            holder.tvMonth.setText(String.format("Expired: %s - %s", gift.getStartDate(), gift.getEndDate()));
        }
        holder.tvTitle.setText(gift.getTitle());
        holder.tvTarget.setText(gift.getGiftTarget().getName());

        Glide.with(context).load(gift.getPhotoUrl()).into(holder.imgVu);

        holder.btnGift.setOnClickListener(new View.OnClickListener() {
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

        TextView tvClubName, tvMonth, tvTitle, tvTarget;
        ImageView imgVu;
        MaterialCardView btnGift;
        CardView monthCardVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvTarget = itemView.findViewById(R.id.tv_target);
            imgVu = itemView.findViewById(R.id.img_vu);
            btnGift = itemView.findViewById(R.id.btn_gift);
            monthCardVu = itemView.findViewById(R.id.month_card_vu);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}