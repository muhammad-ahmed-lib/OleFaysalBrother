package ae.oleapp.partner.bottomSheets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentChangeCompanyBottomSheetBinding;
import ae.oleapp.databinding.FragmentPaymentBottomSheetBinding;
import ae.oleapp.models.AddedBy;


public class PaymentBottomSheetFragment extends DialogFragment implements View.OnClickListener{

    private FragmentPaymentBottomSheetBinding binding;
    private ResultDialogCallback dialogCallback;
    private int amount;
    private String month = "";

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }
    public PaymentBottomSheetFragment() {
        // Required empty public constructor
    }
    public PaymentBottomSheetFragment(int amount, String month) {
        this.amount = amount;
        this.month = month;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPaymentBottomSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.etMonth.setText(month);
        binding.etAmount.setText(String.valueOf(amount));


        binding.btnClose.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
            dismiss();
        }
        else if (v == binding.btnConfirm){
            dialogCallback.didSubmitResult(this, true, binding.etAmount.getText().toString());
        }
    }

    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, boolean isConfirmed, String Amount);
    }
}