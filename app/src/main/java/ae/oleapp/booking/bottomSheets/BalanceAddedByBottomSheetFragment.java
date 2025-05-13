package ae.oleapp.booking.bottomSheets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Objects;
import ae.oleapp.R;
import ae.oleapp.databinding.FragmentBalanceAddedByBottomSheetBinding;
import ae.oleapp.models.AddedBy;

public class BalanceAddedByBottomSheetFragment extends DialogFragment implements View.OnClickListener{

    private FragmentBalanceAddedByBottomSheetBinding binding;
    private ResultDialogCallback dialogCallback;
    private AddedBy addedBy;

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }
    public BalanceAddedByBottomSheetFragment() {
        // Required empty public constructor
    }
    public BalanceAddedByBottomSheetFragment(AddedBy addedBy) {
        this.addedBy = addedBy;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBalanceAddedByBottomSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.tvName.setText(addedBy.getName());
        binding.tvDate.setText(addedBy.getAddedAt());
        binding.tvAmt.setText(String.format("%s %s", addedBy.getAddedAmount(), addedBy.getCurrency()));

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