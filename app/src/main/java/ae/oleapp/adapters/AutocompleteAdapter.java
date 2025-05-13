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
import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.NotificationList;

public class AutocompleteAdapter extends RecyclerView.Adapter<AutocompleteAdapter.ViewHolder> {

        private final Context context;
        private final List<AutocompletePrediction> predictions;
        private ItemClickListener itemClickListener;

        public AutocompleteAdapter(Context context, List<AutocompletePrediction> predictions) {
            this.context = context;
            this.predictions = predictions;
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @androidx.annotation.NonNull
        @Override
        public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_autocomplete_prediction, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position) {


            holder.predictionTv.setText(predictions.get(position).getFullText(null));

            holder.predictionTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.itemClicked(view, holder.getBindingAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return predictions.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            TextView predictionTv;
            CardView layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                predictionTv = itemView.findViewById(R.id.prediction_text);
            }
        }

        public interface ItemClickListener {
            void itemClicked(View view, int pos);
        }
    }