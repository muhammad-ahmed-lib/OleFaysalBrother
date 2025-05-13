package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Club;
import ae.oleapp.models.Field;
import ae.oleapp.models.GeneralField;

public class UniversalFieldAdapter extends RecyclerView.Adapter<UniversalFieldAdapter.ViewHolder> {

    private final Context context;
    private final List<GeneralField> fieldList;
    private ItemClickListener itemClickListener;
    private String selectedId = "";
    private int selectedIndex = -1;
    private final List<GeneralField> selectedFieldIds =  new ArrayList<>();

    public UniversalFieldAdapter(Context context, List<GeneralField> fieldList) {
        this.context = context;
        this.fieldList = fieldList;
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.universal_round_green_vu, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GeneralField field = fieldList.get(position);
        holder.tvName.setText(field.getName());

        if (isExist(fieldList.get(position).getId()) != -1) {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.v5greenColor) );
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.v5greenColor));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.whiteColor));
        }
        else {
            holder.cardView.setStrokeColor(context.getResources().getColor(R.color.whiteColor) );
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor));
            holder.tvName.setTextColor(context.getResources().getColor(R.color.v5_text_color));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClicked(v, holder.getBindingAdapterPosition());
            }
        });
    }

    public List<GeneralField> getSelectedFieldIds() {
        return selectedFieldIds;
    }


    public void selectedFields(GeneralField field) {
        int index = isExist(field.getId());
        if (index == -1) {
            selectedFieldIds.add(field);
        }
        else {
            selectedFieldIds.remove(index);
        }
        notifyDataSetChanged();
    }

    public int isExist(int fieldId) {
        int index = -1;
        for (int i = 0; i < selectedFieldIds.size(); i++) {
            if (selectedFieldIds.get(i).getId() == fieldId) {
                index = i;
                break;
            }
        }
        return index;
    }



    @Override
    public int getItemCount() {
        return fieldList.size();
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