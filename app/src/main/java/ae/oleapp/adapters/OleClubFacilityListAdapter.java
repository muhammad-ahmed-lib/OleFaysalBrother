package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.dialogs.SelectionListDialog;
import ae.oleapp.models.OleClubFacility;
import ae.oleapp.models.SelectionList;
import ae.oleapp.util.Functions;
import io.github.vejei.cupertinoswitch.CupertinoSwitch;

public class OleClubFacilityListAdapter extends RecyclerView.Adapter<OleClubFacilityListAdapter.ViewHolder> {

    private final Context context;
    private final List<OleClubFacility> list;
    private OnItemClickListener onItemClickListener;
    public List<OleClubFacility> selectedFacility = new ArrayList<>();
    private int currentSelectedPosition = -1;

    public OleClubFacilityListAdapter(Context context, List<OleClubFacility> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setCurrentSelectedPosition(int currentSelectedPosition) {
        this.currentSelectedPosition = currentSelectedPosition;
        notifyDataSetChanged();
    }

    public int isExistInSelected(int facId) {
        int index = -1;
        for (int i = 0; i < selectedFacility.size(); i++) {
            if (selectedFacility.get(i).getId() == facId) {
                index = i;
                break;
            }
        }
        return index;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.oleclub_facility_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleClubFacility facility = list.get(position);
        holder.tvTitle.setText(facility.getName());
//        if (facility.getPrice() == null || facility.getPrice().equalsIgnoreCase("")) {
//            holder.tvPrice.setText("");
//        } else {
//            holder.tvPrice.setText(String.format("%s %s", facility.getPrice(), facility.getCurrency()));
//        }

        int index = isExistInSelected(facility.getId());
        if (index == -1) {
            holder.mySwitch.setChecked(false);
//            Glide.with(context).load(facility.getIcon()).into(holder.imgVu);
            holder.tvPrice.setText("");
            if (currentSelectedPosition == position) {
                showBtmVu(holder);
            }
            else {
                holder.btmVu.setVisibility(View.GONE);
            }
        }
        else {
            holder.btmVu.setVisibility(View.GONE);
            facility = selectedFacility.get(index);
            holder.mySwitch.setChecked(true);
//            Glide.with(context).load(facility.getActiveIcon()).into(holder.imgVu);
            if (facility.getPrice() == 0) {
                holder.tvPrice.setText(R.string.free);
            }
            else {
                holder.tvPrice.setText(String.format("%s %s", facility.getPrice(), facility.getCurrency()));
            }
        }

        if (holder.tvPrice.getText().toString().isEmpty()) {
            holder.tvPrice.setVisibility(View.GONE);
        }
        else {
            holder.tvPrice.setVisibility(View.VISIBLE);
        }

        holder.relMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(v, holder.getBindingAdapterPosition());
                }
            }
        });
    }

    private void showBtmVu(ViewHolder holder) {
        holder.btmVu.setVisibility(View.VISIBLE);
        holder.freeVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.isFree = true;
//                holder.freeImgVu.setImageResource(R.drawable.check);
//                holder.paidImgVu.setImageResource(R.drawable.uncheck);
                holder.paidVu.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor, context.getTheme()));
                holder.freeVu.setCardBackgroundColor(context.getResources().getColor(R.color.v5greenColor, context.getTheme()));
                holder.paidVuTv.setTextColor(context.getResources().getColor(R.color.v5_text_color, context.getTheme()));
                holder.freeVuTv.setTextColor(context.getResources().getColor(R.color.whiteColor, context.getTheme()));
                holder.paidDetailVu.setVisibility(View.GONE);
            }
        });
        holder.paidVu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.isFree = false;
                holder.freeVu.setCardBackgroundColor(context.getResources().getColor(R.color.whiteColor, context.getTheme()));
                holder.paidVu.setCardBackgroundColor(context.getResources().getColor(R.color.v5greenColor, context.getTheme()));
                holder.paidVuTv.setTextColor(context.getResources().getColor(R.color.whiteColor, context.getTheme()));
                holder.freeVuTv.setTextColor(context.getResources().getColor(R.color.v5_text_color, context.getTheme()));
//                holder.freeImgVu.setImageResource(R.drawable.uncheck);
//                holder.paidImgVu.setImageResource(R.drawable.check);
                holder.paidDetailVu.setVisibility(View.VISIBLE);
            }
        });
        holder.etType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeClicked(holder, list.get(holder.getBindingAdapterPosition()));
            }
        });
        holder.etUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unitClicked(holder, list.get(holder.getBindingAdapterPosition()));
            }
        });
        holder.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.isFree) {
                    list.get(holder.getBindingAdapterPosition()).setPrice(0);
                    selectedFacility.add(list.get(holder.getBindingAdapterPosition()));
                    currentSelectedPosition = -1;
                    notifyDataSetChanged();
                }
                else {
                    if (holder.etPrice.getText().toString().equalsIgnoreCase("")) {
                        Functions.showToast(context, context.getString(R.string.enter_price), FancyToast.ERROR);
                        return;
                    }
                    if (holder.etType.getText().toString().equalsIgnoreCase("")) {
                        Functions.showToast(context, context.getString(R.string.select_type), FancyToast.ERROR);
                        return;
                    }
                    if (list.get(holder.getBindingAdapterPosition()).getType().equalsIgnoreCase("SELECTABLE")) {
                        if (holder.etUnit.getText().toString().equalsIgnoreCase("")) {
                            Functions.showToast(context, context.getString(R.string.select_unit), FancyToast.ERROR);
                            return;
                        }
                        if (holder.etQty.getText().toString().equalsIgnoreCase("")) {
                            Functions.showToast(context, context.getString(R.string.enter_max_qty), FancyToast.ERROR);
                            return;
                        }
                    }
                    list.get(holder.getBindingAdapterPosition()).setPrice(Integer.valueOf(holder.etPrice.getText().toString()));
                    list.get(holder.getBindingAdapterPosition()).setMaxQuantity(holder.etQty.getText().toString());
                    selectedFacility.add(list.get(holder.getBindingAdapterPosition()));
                    currentSelectedPosition = -1;
                    notifyDataSetChanged();
                }
            }
        });
    }

    private void typeClicked(ViewHolder holder, OleClubFacility facility) {
        List<SelectionList> oleSelectionList = Arrays.asList(new SelectionList("FIXED", context.getString(R.string.fixed)), new SelectionList("SELECTABLE", context.getString(R.string.selectable)));
        SelectionListDialog dialog = new SelectionListDialog(context, context.getString(R.string.select_type), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new SelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<SelectionList> selectedItems) {
                SelectionList selectedItem = selectedItems.get(0);
                facility.setType(selectedItem.getId());
                holder.etType.setText(selectedItem.getValue());
                if (selectedItem.getId().equalsIgnoreCase("FIXED")) {
                    holder.relQty.setVisibility(View.GONE);
                    holder.relUnit.setVisibility(View.GONE);
                }
                else {
                    holder.relQty.setVisibility(View.VISIBLE);
                    holder.relUnit.setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.show();
    }

    private void unitClicked(ViewHolder holder, OleClubFacility facility) {
        List<SelectionList> oleSelectionList = Arrays.asList(new SelectionList("QUANTITY", context.getString(R.string.per_item)), new SelectionList("BOX", context.getString(R.string.box)));
        SelectionListDialog dialog = new SelectionListDialog(context, context.getString(R.string.select_unit), false);
        dialog.setLists(oleSelectionList);
        dialog.setOnItemSelected(new SelectionListDialog.OnItemSelected() {
            @Override
            public void selectedItem(List<SelectionList> selectedItems) {
                SelectionList selectedItem = selectedItems.get(0);
                facility.setUnit(selectedItem.getId());
                holder.etUnit.setText(selectedItem.getValue());
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgVu, freeImgVu, paidImgVu;
        TextView tvTitle, tvPrice, paidVuTv, freeVuTv;
        LinearLayout relMain, paidDetailVu;
        CupertinoSwitch mySwitch;
        LinearLayout btmVu;
        MaterialCardView freeVu, paidVu, btnSave;
        EditText etPrice, etType, etUnit, etQty;
        TextView tvCurrency;
        RelativeLayout relUnit, relQty;
        boolean isFree = false;

        ViewHolder(View itemView) {
            super(itemView);

            imgVu = itemView.findViewById(R.id.img_vu);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            relMain = itemView.findViewById(R.id.main);
            mySwitch = itemView.findViewById(R.id.my_switch);
            paidVuTv = itemView.findViewById(R.id.paid_vu_tv);
            freeVuTv = itemView.findViewById(R.id.free_vu_tv);
            mySwitch.setClickable(false);

            btmVu = itemView.findViewById(R.id.btm_vu);
            freeVu = itemView.findViewById(R.id.free_vu);
            paidVu = itemView.findViewById(R.id.paid_vu);
            freeImgVu = itemView.findViewById(R.id.img_free);
            paidImgVu = itemView.findViewById(R.id.img_paid);
            paidDetailVu = itemView.findViewById(R.id.paid_detail);
            btnSave = itemView.findViewById(R.id.btn_save);
            etPrice = itemView.findViewById(R.id.et_price);
            etType = itemView.findViewById(R.id.et_type);
            etUnit = itemView.findViewById(R.id.et_unit);
            etQty = itemView.findViewById(R.id.et_qty);
            tvCurrency = itemView.findViewById(R.id.tv_currency);
            relUnit = itemView.findViewById(R.id.rel_unit);
            relQty = itemView.findViewById(R.id.rel_qty);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(View v, int pos);
    }
}