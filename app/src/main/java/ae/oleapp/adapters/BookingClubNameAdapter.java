package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Color;
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
import ae.oleapp.models.Club;

public class BookingClubNameAdapter extends RecyclerView.Adapter<BookingClubNameAdapter.ViewHolder> {

    private final Context context;
    private final List<Club> list;
    private int selectedIndex = -1;
    //private String selectedId="";
    private OnItemClickListener onItemClickListener;
    private boolean isPadel = false;

    public BookingClubNameAdapter(Context context, List<Club> list, int selectedIndex, boolean isPadel) {
        this.context = context;
        this.list = list;
        this.selectedIndex = selectedIndex;
        this.isPadel = isPadel;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }
//    public void setSelectedId(String selectedId) {
//        this.selectedId = selectedId;
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_club_name_vu, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Club club = list.get(position);
        holder.tvName.setText(club.getName());

        if (!club.getId().isEmpty()){
            holder.tvBookingCount.setText(String.valueOf(club.getBookingCount()));
        }else{
            holder.clubImg.setVisibility(View.GONE);
            holder.horVu.setVisibility(View.GONE);
            holder.tvBooking.setVisibility(View.GONE);

            int sum = 0;
            for (Club clubs : list) {
                if (clubs.getId() != null && !clubs.getId().isEmpty()) {
                    sum += clubs.getBookingCount();
                }
            }
            holder.tvBookingCount.setText(String.valueOf(sum));

        }


        if (selectedIndex == position) {
            holder.mainLay.setStrokeColor(ContextCompat.getColor(context, R.color.v5greenColor));
            holder.clubImg.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.v5_club_ic_new_selected));
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.v5greenColor));
        }
        else {
            holder.mainLay.setStrokeColor(ContextCompat.getColor(context, R.color.transparent));
            holder.clubImg.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.v5_club_ic_new));
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.v5_text_color));
        }

        holder.mainLay.setOnClickListener(new View.OnClickListener() {
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


        TextView tvName, tvBooking, tvBookingCount;
        ImageView clubImg;
        MaterialCardView mainLay;
        View horVu;

        ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            clubImg = itemView.findViewById(R.id.club_img);
            mainLay = itemView.findViewById(R.id.club_vu);
            horVu = itemView.findViewById(R.id.hor_vu);
            tvBooking = itemView.findViewById(R.id.tv_booking);
            tvBookingCount = itemView.findViewById(R.id.tv_booking_count);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}