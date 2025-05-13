package ae.oleapp.booking.bottomSheets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

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
import ae.oleapp.adapters.CountAdapter;
import ae.oleapp.databinding.FragmentBookingConfirmationSheetBinding;
import ae.oleapp.models.OleKeyValuePair;


public class BookingConfirmationSheetFragment extends DialogFragment implements View.OnClickListener{

    private FragmentBookingConfirmationSheetBinding binding;
    private ResultDialogCallback dialogCallback;
    private final List<OleKeyValuePair> list =  new ArrayList<>();
    private CountAdapter countAdapter;
    private final String value = "";
    private String clubName = "";
    private String fieldName = "";
    private String size = "";
    private String date = "";
    private String time = "";

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public BookingConfirmationSheetFragment() {
        // Required empty public constructor
    }

    public BookingConfirmationSheetFragment(String clubName, String fieldName, String size, String date, String time) {
        this.clubName = clubName;
        this.fieldName = fieldName;
        this.size = size;
        this.date = date;
        this.time = time;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookingConfirmationSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        populateData();

        binding.btnClose.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);


        return view;
    }

    private void populateData() {

        if (size.isEmpty()) {
            binding.tvFieldName.setText(fieldName);
        }
        else {
            binding.tvFieldName.setText(String.format("%s (%s)", fieldName, size));
        }
        binding.tvClubName.setText(clubName);
        binding.tvTime.setText(time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date dt = dateFormat.parse(date);
            dateFormat.applyPattern("EEEE, dd/MM/yyyy");
            binding.tvDate.setText(dateFormat.format(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
            dismiss();
        }
        else if (v == binding.btnConfirm){
            dialogCallback.didSubmitResult(BookingConfirmationSheetFragment.this, true);
        }

    }

    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, boolean confirmed);
    }
}