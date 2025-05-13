package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;
import ae.oleapp.R;
import ae.oleapp.models.Club;

public class UniversalAdapter extends RecyclerView.Adapter<UniversalAdapter.ViewHolder> {

    private final Context context;
    private final List<Club> clubList;
    private ItemClickListener itemClickListener;
    private String selectedId = "";
    private int selectedIndex = -1;
    private final List<Club> selectedClubIds =  new ArrayList<>();

    public UniversalAdapter(Context context, List<Club> clubList) {
        this.context = context;
        this.clubList = clubList;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.universal_round_green_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Club club = clubList.get(position);
        holder.tvName.setText(club.getName());

//        if (selectedIndex == position) {
        if (isExist(clubList.get(position).getId()) != -1) {
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
                itemClickListener.itemClicked(v, holder.getBindingAdapterPosition());
            }
        });
    }

    public List<Club> getSelectedClubIds() {
        return selectedClubIds;
    }


    public void selectClubs(Club club) {
        int index = isExist(club.getId());
        if (index == -1) {
            selectedClubIds.add(club);
        }
        else {
            selectedClubIds.remove(index);
        }
        notifyDataSetChanged();
    }

    public int isExist(String dayId) {
        int index = -1;
        for (int i = 0; i < selectedClubIds.size(); i++) {
            if (selectedClubIds.get(i).getId().equalsIgnoreCase(dayId)) {
                index = i;
                break;
            }
        }
        return index;
    }



    @Override
    public int getItemCount() {
        return clubList.size();
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