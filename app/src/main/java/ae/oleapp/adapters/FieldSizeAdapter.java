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
import ae.oleapp.models.ClubBankLists;
import ae.oleapp.models.FieldSizeList;
import ae.oleapp.models.OleFieldDataChild;

public class FieldSizeAdapter extends RecyclerView.Adapter<FieldSizeAdapter.ViewHolder>{

    private final Context context;
    private final List<FieldSizeList> list;
    private ItemClickListener itemClickListener;
    private final String selectedId = "";
    private int selectedIndex = -1;


    public FieldSizeAdapter(Context context, List<FieldSizeList> list, int selectedIndex) {
        this.context = context;
        this.list = list;
        this.selectedIndex = selectedIndex;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.field_size_vu, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(list.get(position).getName());
        if (selectedIndex == position) {
            holder.relMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.v5greenColor));
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.whiteColor));
        }
        else {
            holder.relMain.setCardBackgroundColor(ContextCompat.getColor(context, R.color.whiteColor));
            holder.tvName.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
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

        TextView tvName;
        MaterialCardView relMain;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            relMain = itemView.findViewById(R.id.card_vu);

        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}