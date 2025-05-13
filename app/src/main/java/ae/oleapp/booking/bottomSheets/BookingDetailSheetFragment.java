package ae.oleapp.booking.bottomSheets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ae.oleapp.R;
import ae.oleapp.adapters.BookingDetailAdapter;
import ae.oleapp.adapters.CountAdapter;
import ae.oleapp.adapters.WaitingUserAdapter;
import ae.oleapp.databinding.FragmentBookingConfirmationSheetBinding;
import ae.oleapp.databinding.FragmentBookingDetailSheetBinding;
import ae.oleapp.models.BookingLogs;
import ae.oleapp.models.OleKeyValuePair;

public class BookingDetailSheetFragment extends DialogFragment implements View.OnClickListener{

    private FragmentBookingDetailSheetBinding binding;
    private ResultDialogCallback dialogCallback;
    private List<BookingLogs> list =  new ArrayList<>();
    private BookingDetailAdapter adapter;

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public BookingDetailSheetFragment() {
        // Required empty public constructor
    }

    public BookingDetailSheetFragment(List<BookingLogs> list) {
        this.list = list;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookingDetailSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(layoutManager);
        adapter = new BookingDetailAdapter(getContext(), list);
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