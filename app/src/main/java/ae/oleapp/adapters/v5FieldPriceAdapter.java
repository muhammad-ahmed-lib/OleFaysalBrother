package ae.oleapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.models.Day;
import ae.oleapp.models.OleFieldPrice;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.OleShiftTime;
import io.github.vejei.cupertinoswitch.CupertinoSwitch;

public class v5FieldPriceAdapter extends RecyclerView.Adapter<v5FieldPriceAdapter.ViewHolder> {

    private final Context context;
    private final List<OleKeyValuePair> dayList;
    private static final List<OleFieldPrice> pricesList = new ArrayList<>();

    public v5FieldPriceAdapter(Context context, List<OleKeyValuePair> dayList) {
        this.context = context;
        this.dayList = dayList;

        // Initialize pricesList with empty prices for each day
        for (OleKeyValuePair day : dayList) {
            OleFieldPrice price = new OleFieldPrice();
            price.setDayId(day.getKey());  // Assuming day.getKey() gives you the day ID
            pricesList.add(price);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.v5_field_price_vu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OleKeyValuePair day = dayList.get(position);
        holder.tvDayName.setText(day.getValue());

        // Set previous input prices if available
        OleFieldPrice fieldPrice = pricesList.get(position);
        holder.tvPriceOneHour.setText(fieldPrice.getOneHour());
        holder.tvPriceOneHalfHour.setText(fieldPrice.getOneHalfHour());
        holder.tvPriceTwoHour.setText(fieldPrice.getTwoHour());

        holder.mySwitch.setOnClickListener(v -> {
            if (holder.mySwitch.isChecked()) {
                holder.priceVu.setVisibility(View.VISIBLE);
            } else {
                resetPrices(holder);
            }
        });

        holder.mySwitch.setOnStateChangeListener(new CupertinoSwitch.OnStateChangeListener() {
            @Override
            public void onChanged(CupertinoSwitch view, boolean checked) {
            }

            @Override
            public void onSwitchOn(CupertinoSwitch view) {
                holder.priceVu.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSwitchOff(CupertinoSwitch view) {
                resetPrices(holder);
            }
        });

//        holder.mySwitch.setOnClickListener(v -> {
//            boolean isChecked = holder.mySwitch.isChecked();
//            holder.priceVu.setVisibility(isChecked ? View.VISIBLE : View.GONE);
//            if (!isChecked) resetPrices(holder);
//        });

        holder.tvPriceOneHour.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                fieldPrice.setOneHour(holder.tvPriceOneHour.getText().toString());
            }
        });

        holder.tvPriceOneHalfHour.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                fieldPrice.setOneHalfHour(holder.tvPriceOneHalfHour.getText().toString());
            }
        });

        holder.tvPriceTwoHour.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                fieldPrice.setTwoHour(holder.tvPriceTwoHour.getText().toString());
            }
        });
    }

    private void resetPrices(ViewHolder holder) {
        holder.tvPriceOneHour.setText("");
        holder.tvPriceOneHalfHour.setText("");
        holder.tvPriceTwoHour.setText("");
        holder.priceVu.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName;
        EditText tvPriceOneHour, tvPriceOneHalfHour, tvPriceTwoHour;
        CupertinoSwitch mySwitch;
        LinearLayout priceVu;

        ViewHolder(View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tv_day);
            mySwitch = itemView.findViewById(R.id.my_switch);
            tvPriceOneHour = itemView.findViewById(R.id.tv_one_hour);
            tvPriceOneHalfHour = itemView.findViewById(R.id.tv_one_half_hour);
            tvPriceTwoHour = itemView.findViewById(R.id.tv_two_hour);
            priceVu = itemView.findViewById(R.id.price_vu);
        }
    }

    public static String getPricesAsJson() {
        try {
            JSONArray array = new JSONArray();

            for (OleFieldPrice price : pricesList) {
                if (priceHasPrices(price)) {
                    JSONObject priceObject = new JSONObject();
                    priceObject.put("day_id", Integer.valueOf(price.getDayId()));
                    priceObject.put("one_hour", parsePrice(price.getOneHour()));
                    priceObject.put("one_half", parsePrice(price.getOneHalfHour()));
                    priceObject.put("two_hour", parsePrice(price.getTwoHour()));
                    array.put(priceObject);
                }
            }

            if (array.length() > 7) {
                return array.toString();
            }

            return array.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static boolean priceHasPrices(OleFieldPrice price) {
        return price.getOneHour() != null && !price.getOneHour().isEmpty() &&
                price.getOneHalfHour() != null && !price.getOneHalfHour().isEmpty() &&
                price.getTwoHour() != null && !price.getTwoHour().isEmpty();
    }

    private static int parsePrice(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }



}
