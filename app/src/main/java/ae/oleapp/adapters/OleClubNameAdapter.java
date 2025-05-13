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
import ae.oleapp.models.Club;


public class OleClubNameAdapter extends RecyclerView.Adapter<OleClubNameAdapter.ViewHolder> {

    private final Context context;
    private final List<Club> clubList;
    private ItemClickListener itemClickListener;
    private String selectedId = "";
    private int selectedIndex = -1;

    public OleClubNameAdapter(Context context, List<Club> clubList) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.clubs_name_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Club club = clubList.get(position);
        holder.tvName.setText(club.getName());

      if (selectedIndex == position) {
          holder.cardView.setStrokeColor(context.getResources().getColor(R.color.whiteColor) );
          holder.cardView.setCardBackgroundColor(Color.parseColor("#33000000"));
          holder.tvName.setTextColor(context.getResources().getColor(R.color.whiteColor));
        }
        else {
          holder.cardView.setStrokeColor(context.getResources().getColor(R.color.club_selection_color) );
          holder.cardView.setCardBackgroundColor(Color.TRANSPARENT);
          holder.tvName.setTextColor(context.getResources().getColor(R.color.whiteColor));
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getBindingAdapterPosition());
            }
        });
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