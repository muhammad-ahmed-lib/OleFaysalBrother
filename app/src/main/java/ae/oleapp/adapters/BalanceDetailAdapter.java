package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.BookingLogs;
import ae.oleapp.models.Collection;

public class BalanceDetailAdapter extends RecyclerView.Adapter<BalanceDetailAdapter.ViewHolder>{

    private final Context context;
    private final List<Collection> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";


    public BalanceDetailAdapter(Context context, List<Collection> list) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.balance_detail_vu, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Collection collection = list.get(position);
        if (!collection.getReceipt().isEmpty()){
            holder.relInvoice.setVisibility(View.VISIBLE);
            Glide.with(context).load(collection.getReceipt()).into(holder.imgVu);
        }
        holder.tvName.setText(collection.getCollectedBy().getName());
        holder.tvDate.setText(collection.getCollectedAt());
        holder.tvAmt.setText(String.format("%s %s", collection.getAmount(), collection.getCurrency()));
        holder.tvMethod.setText(collection.getMethod());


    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvDate, tvAmt, tvMethod;
        ImageView imgVu;
        RelativeLayout relInvoice;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmt = itemView.findViewById(R.id.tv_amt);
            tvMethod = itemView.findViewById(R.id.tv_method);
            imgVu = itemView.findViewById(R.id.img_vu);
            relInvoice = itemView.findViewById(R.id.rel_invoice);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}
