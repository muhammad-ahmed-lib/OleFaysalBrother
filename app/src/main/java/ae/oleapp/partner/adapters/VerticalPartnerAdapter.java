package ae.oleapp.partner.adapters;

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
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Partner;

public class VerticalPartnerAdapter extends RecyclerView.Adapter<VerticalPartnerAdapter.ViewHolder>{

    private final Context context;
    private final List<Partner> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";


    public VerticalPartnerAdapter(Context context, List<Partner> list) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.partner_vertical_vu, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Partner partner = list.get(position);

        holder.tvName.setText(partner.getName());
        holder.tvShares.setText(String.format("%s %s %s", "Shares:", partner.getShares(), partner.getSharesType()));

        if (!partner.getProfilePhoto().isEmpty()){
            Glide.with(context).load(partner.getProfilePhoto()).apply(RequestOptions.circleCropTransform()).into(holder.imgVu);
        }
        else{
            Glide.with(context).load(R.drawable.blue_ole_logo).apply(RequestOptions.circleCropTransform()).into(holder.imgVu);

        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getBindingAdapterPosition());
            }
        });

        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.callClicked(v, holder.getBindingAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvShares,  tvName;
        CardView relMain;
        ImageView imgVu;
        MaterialCardView btnCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvShares = itemView.findViewById(R.id.tv_shares);
            imgVu = itemView.findViewById(R.id.img_vu);
            relMain = itemView.findViewById(R.id.rel_main);
            btnCall = itemView.findViewById(R.id.btn_call);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
        void callClicked(View view, int pos);
    }
}

