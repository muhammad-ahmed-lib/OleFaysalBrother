package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.PlayerSkill;

public class HirePlayerAdapter extends RecyclerView.Adapter<HirePlayerAdapter.ViewHolder>{

    private final Context context;
    private final List<PlayerSkill> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";


    public HirePlayerAdapter(Context context, List<PlayerSkill> list) {
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


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hire_player_vu, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlayerSkill player = list.get(position);

        holder.tvName.setText(player.getName());
        holder.tvPrice.setText(String.format("%s %s", player.getAmount(), player.getCurrency()));

        if (!player.getShirt().isEmpty()){
            Glide.with(context).load(player.getShirt()).into(holder.shirt);
        }

        if (!player.getPicture().isEmpty()){
            Glide.with(context).load(player.getPicture()).into(holder.picture);
        }

        holder.relPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getBindingAdapterPosition());
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.phoneClicked(v, holder.getBindingAdapterPosition());
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvPrice, tvRate;
        ImageView shirt, picture;
        MaterialCardView call;
        CardView relPlayer;
        ScaleRatingBar ratingBar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvRate = itemView.findViewById(R.id.tv_rate);
            shirt = itemView.findViewById(R.id.shirt_img_vu);
            picture = itemView.findViewById(R.id.emoji_img_vu);
            call = itemView.findViewById(R.id.btn_call);
            relPlayer = itemView.findViewById(R.id.rel_player);
            ratingBar = itemView.findViewById(R.id.rating_bar);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void phoneClicked(View view, int pos);
    }
}
