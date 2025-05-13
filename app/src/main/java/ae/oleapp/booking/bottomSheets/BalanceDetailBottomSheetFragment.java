package ae.oleapp.booking.bottomSheets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ae.oleapp.R;
import ae.oleapp.adapters.BalanceDetailAdapter;
import ae.oleapp.adapters.BookingDetailAdapter;
import ae.oleapp.databinding.FragmentBalanceDetailBottomSheetBinding;
import ae.oleapp.databinding.FragmentBookingDetailSheetBinding;
import ae.oleapp.models.BookingLogs;
import ae.oleapp.models.Collection;

public class BalanceDetailBottomSheetFragment extends DialogFragment implements View.OnClickListener{

    private FragmentBalanceDetailBottomSheetBinding binding;
    private ResultDialogCallback dialogCallback;
    private List<Collection> list =  new ArrayList<>();
    private BalanceDetailAdapter adapter;

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public BalanceDetailBottomSheetFragment() {
        // Required empty public constructor
    }

    public BalanceDetailBottomSheetFragment(List<Collection> list) {
        this.list = list;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBalanceDetailBottomSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new BalanceDetailAdapter(getContext(), list);
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