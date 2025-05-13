package ae.oleapp.booking.bottomSheets;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.CountAdapter;
import ae.oleapp.adapters.PriceAdapter;
import ae.oleapp.databinding.FragmentFacilityQuantitySheetBinding;
import ae.oleapp.databinding.FragmentTotalAmountSheetBinding;
import ae.oleapp.models.OleKeyValuePair;
import ae.oleapp.models.Payments;
import ae.oleapp.models.Price;


public class TotalAmountSheetFragment extends DialogFragment implements View.OnClickListener{

    private FragmentTotalAmountSheetBinding binding;
    private ResultDialogCallback dialogCallback;
    private List<Price> list =  new ArrayList<>();
    private List<Payments> paymentList =  new ArrayList<>();
    private PriceAdapter adapter;
    private final String value = "";
    private Boolean totalAmount;

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public TotalAmountSheetFragment() {
        // Required empty public constructor
    }

    public TotalAmountSheetFragment(List<Price> list, List<Payments> paymentList, Boolean totalAmount) {
        this.list = list;
        this.paymentList = paymentList;
        this.totalAmount = totalAmount;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTotalAmountSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (totalAmount){
            binding.title.setText("Payment Details");
        }
        else{
            binding.title.setText("Paid Amount");
        }



        LinearLayoutManager countLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerVu.setLayoutManager(countLayoutManager);
        adapter = new PriceAdapter(getContext(), list, paymentList, totalAmount);
        binding.recyclerVu.setAdapter(adapter);



        binding.btnClose.setOnClickListener(this);


        return view;
    }



    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
            dismiss();
        }
    }

    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df);
    }
}