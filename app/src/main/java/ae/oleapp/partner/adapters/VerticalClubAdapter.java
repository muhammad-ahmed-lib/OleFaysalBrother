package ae.oleapp.partner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Club;
import ae.oleapp.models.Transaction;

public class VerticalClubAdapter extends RecyclerView.Adapter<VerticalClubAdapter.ViewHolder>{

    private final Context context;
    private final List<Club> list;
    private ItemClickListener itemClickListener;
    private String clubId = "";
    private final String neglectedClubId = "";


    public VerticalClubAdapter(Context context, List<Club> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedId(String clubId){
        this.clubId = clubId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vertical_club_vu, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Club club = list.get(position);

        holder.tvClubName.setText(club.getName());
        holder.tvLoc.setText(String.format("%s %s", club.getCity(), club.getDistance()));

        if (clubId.equalsIgnoreCase(club.getId())){
            holder.tickImgVu.setImageResource(R.drawable.club_selected);
            holder.stadiumVu.setStrokeColor(ContextCompat.getColor(context, R.color.v5greenColor));
        }
        else{
            holder.tickImgVu.setImageResource(R.drawable.club_deselect);
            holder.stadiumVu.setStrokeColor(ContextCompat.getColor(context, R.color.separatorColor));
        }

        holder.stadiumVu.setOnClickListener(new View.OnClickListener() {
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

        TextView tvClubName, tvLoc;
        ImageView tickImgVu;
        MaterialCardView stadiumVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvLoc = itemView.findViewById(R.id.tv_loc);
            tickImgVu = itemView.findViewById(R.id.img_vu_tick);
            stadiumVu = itemView.findViewById(R.id.stadium_vu);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}
