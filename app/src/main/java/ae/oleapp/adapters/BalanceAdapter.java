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

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Balance;
import ae.oleapp.models.OlePlayerMatch;
import ae.oleapp.models.PlayerSkill;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.ViewHolder>{

    private final Context context;
    private List<Balance> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";
    private boolean ispending = true;


    public BalanceAdapter(Context context, List<Balance> list) {
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

    public void setDataSource(List<Balance> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.balance_vu, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Balance balance = list.get(position);
        holder.fieldCardVu.setCardBackgroundColor(Color.parseColor(balance.getBooking().getField().getBorderColor()));
        holder.tvFieldNameSize.setText(String.format("%s (%s)", balance.getBooking().getField().getName(), balance.getBooking().getField().getSize()));
        holder.tvTimeDur.setText(String.format("%s (%s)", balance.getBooking().getTime(), balance.getBooking().getDuration()));
        holder.tvDate.setText(balance.getBooking().getDate());
        holder.tvAmt.setText(String.format("%s (%s)", balance.getAmount(), balance.getCurrency()));
        holder.tvClubName.setText(balance.getBooking().getClub().getName());
        holder.tvBookedBy.setText(balance.getBooking().getBy());
        if (balance.getBooking().getBy().equalsIgnoreCase("OWNER")){
            holder.tvBookedBy.setText("Owner Booked");
        }
        else if (balance.getBooking().getBy().equalsIgnoreCase("PLAYER")) {
            holder.tvBookedBy.setText("Player Booked");
        }
        else {
            holder.tvBookedBy.setText("Employee Booked");
        }
        if (!balance.getIsCleared()){
            holder.tvAmt.setTextColor(ContextCompat.getColor(context, R.color.redBookingColor));
            holder.pay.setVisibility(View.VISIBLE);
            ispending = true;
        }
        else{
            holder.tvAmt.setTextColor(ContextCompat.getColor(context, R.color.v5greenColor));
            holder.pay.setVisibility(View.GONE);
            ispending = false;
        }

        holder.pay.setOnClickListener(v -> itemClickListener.payClicked(v, holder.getBindingAdapterPosition()));
        holder.btnBalInfo.setOnClickListener(v -> itemClickListener.infoClicked(v, holder.getBindingAdapterPosition(), ispending));

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvFieldNameSize, tvBookedBy, tvDate, tvAmt, tvTimeDur, tvClubName;

        ImageView btnBalInfo;
        CardView fieldCardVu, infoBookedCardVu, pay;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFieldNameSize = itemView.findViewById(R.id.tv_field_name_size);
            tvBookedBy = itemView.findViewById(R.id.tv_booked_by);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmt = itemView.findViewById(R.id.tv_amt);
            tvTimeDur = itemView.findViewById(R.id.tv_time_duration);
            tvClubName = itemView.findViewById(R.id.tv_club_name);

            fieldCardVu = itemView.findViewById(R.id.field_card_vu);
            infoBookedCardVu = itemView.findViewById(R.id.info_booked_card_vu);


            btnBalInfo = itemView.findViewById(R.id.btn_bal_info);
            pay = itemView.findViewById(R.id.btn_pay);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void payClicked(View view, int pos);
        void infoClicked(View view, int pos, boolean ispending);
    }
}
