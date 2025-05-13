package ae.oleapp.booking.bottomSheets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentCancelBookingSheetBinding;

public class CancelBookingSheetFragment extends DialogFragment implements View.OnClickListener{

    private FragmentCancelBookingSheetBinding binding;
    private ResultDialogCallback dialogCallback;
    private final String note = "";

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public CancelBookingSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCancelBookingSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.btnClose.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
            dismiss();
        }
        else if (v == binding.btnCancel){
            dialogCallback.didSubmitResult(CancelBookingSheetFragment.this, true,  binding.etNote.getText().toString());
        }
    }

    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, boolean isCancelled , String note);
    }
}