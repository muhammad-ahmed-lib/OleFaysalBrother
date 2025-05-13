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
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.BookingLogs;
import ae.oleapp.models.DocModel;

public class BookingDetailAdapter extends RecyclerView.Adapter<BookingDetailAdapter.ViewHolder>{

    private final Context context;
    private final List<BookingLogs> list;
    private ItemClickListener itemClickListener;
    private String selectedId = "";


    public BookingDetailAdapter(Context context, List<BookingLogs> list) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_detail_vu, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingLogs logs = list.get(position);

        if (logs.getAction().equalsIgnoreCase("BOOKED")){
            holder.title.setText("Booked By");
            holder.tvName.setText(logs.getActionBy());
            holder.tvDate.setText(logs.getCreatedAt());

        }
        else if (logs.getAction().equalsIgnoreCase("CONFIRMED")) {
            holder.title.setText("Confirmed  By");
            holder.tvName.setText(logs.getActionBy());
            holder.tvDate.setText(logs.getCreatedAt());
        }
        else {
            holder.title.setText("Completed  By");
            holder.tvName.setText(logs.getActionBy());
            holder.tvDate.setText(logs.getCreatedAt());
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView title, tvName, tvDate;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);


        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}
