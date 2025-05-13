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

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.DocAdapter;
import ae.oleapp.models.DocModel;
import ae.oleapp.models.Partner;

public class PartnerAdapter extends RecyclerView.Adapter<PartnerAdapter.ViewHolder>{

    private final Context context;
    private final List<Partner> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";


    public PartnerAdapter(Context context, List<Partner> list) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.partner_vu, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Partner partner = list.get(position);

        holder.tvShares.setText(String.format("%s %s", partner.getShares(), partner.getSharesType()));
        holder.tvNetProfit.setText(String.format("%s %s", partner.getCurrency(), partner.getNetProfit().getNetProfit()));

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
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvShares, tvNetProfit;
        CardView relMain;
        ImageView imgVu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvShares = itemView.findViewById(R.id.tv_shares);
            tvNetProfit = itemView.findViewById(R.id.tv_net_profit);
            imgVu = itemView.findViewById(R.id.img_vu);
            relMain = itemView.findViewById(R.id.rel_main_data);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}
