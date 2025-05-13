package ae.oleapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.CancelledHistory;
import ae.oleapp.models.NotificationList;

public class CancellationHistoryAdapter extends RecyclerView.Adapter<CancellationHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<CancelledHistory> list;
    private ItemClickListener itemClickListener;

    public CancellationHistoryAdapter(Context context, List<CancelledHistory> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cancellation_history_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CancelledHistory cancelledHistory = list.get(position);
        holder.tvDate.setText(cancelledHistory.getCancelledDate());
        holder.tvNote.setText(cancelledHistory.getReason());

//        holder.layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                itemClickListener.itemClicked(view, holder.getAdapterPosition());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvDate, tvNote;
//        ImageView shirtImgVu, emojiImgVu;
//        CardView layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvNote = itemView.findViewById(R.id.tv_note);
//            shirtImgVu = itemView.findViewById(R.id.shirt_img_vu);
//            emojiImgVu = itemView.findViewById(R.id.emoji_img_vu);
//            layout = itemView.findViewById(R.id.main_layout);
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, int pos);
    }
}