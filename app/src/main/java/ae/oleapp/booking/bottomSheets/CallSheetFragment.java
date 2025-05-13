package ae.oleapp.booking.bottomSheets;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ae.oleapp.R;
import ae.oleapp.databinding.FragmentCallSheetBinding;
import ae.oleapp.databinding.FragmentConfirmCancelWaitingBookingSheetBinding;
import ae.oleapp.models.BasicBookingDetail;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallSheetFragment extends DialogFragment implements View.OnClickListener{

    private FragmentCallSheetBinding binding;
    private ResultDialogCallback dialogCallback;
    private String bookingId = "";

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public CallSheetFragment() {
        // Required empty public constructor
    }

    public CallSheetFragment(String bookingId) {
        this.bookingId = bookingId;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCallSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding.btnClose.setOnClickListener(this);
        binding.btnCall.setOnClickListener(this);
        binding.btnCallApp.setOnClickListener(this);
        binding.btnWhatsapp.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
            dismiss();
        }
        else if (v == binding.btnCall){
            dialogCallback.callClicked(this);
        }
        else if (v == binding.btnCallApp){
            dialogCallback.callAppClicked(this);
        }
        else if (v == binding.btnWhatsapp){
            dialogCallback.whatsappClicked(this);
        }

    }

    public interface ResultDialogCallback {
        void callClicked(DialogFragment df);
        void callAppClicked(DialogFragment df);
        void whatsappClicked(DialogFragment df);
    }
}