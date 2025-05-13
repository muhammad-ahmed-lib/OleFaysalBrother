package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.Payments;
import ae.oleapp.models.Price;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {

    private final Context context;
    private final List<Price> list;
    private List<Payments> paymentList =  new ArrayList<>();
    private final int selectedIndex = -1;
    private final Boolean totalAmount;

    public PriceAdapter(Context context, List<Price> list, List<Payments> paymentList, Boolean totalAmount) {
        this.context = context;
        this.list = list;
        this.paymentList = paymentList;
        this.totalAmount = totalAmount;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.price_vu, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (totalAmount){
            Price price = list.get(position);
            holder.tvTitle.setText(price.getPriceType().toLowerCase());
            holder.tvAmount.setText(String.format("%s %s", price.getAmount(), price.getCurrency()));
        }
        else {
            Payments payment = paymentList.get(position);
            holder.tvTitle.setText(payment.getMethod().toLowerCase());
            holder.tvAmount.setText(String.format("%s %s", payment.getAmount(), payment.getCurrency()));
        }


    }

    @Override
    public int getItemCount() {
        if (totalAmount){
            return list.size();

        }
        else {
            return paymentList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle, tvAmount;

        ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAmount = itemView.findViewById(R.id.tv_amt);

        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}