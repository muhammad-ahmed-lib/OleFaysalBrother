package ae.oleapp.booking.bottomSheets;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.CountAdapter;
import ae.oleapp.databinding.FragmentFacilityQuantitySheetBinding;
import ae.oleapp.models.OleKeyValuePair;

public class FacilityQuantitySheetFragment extends DialogFragment implements View.OnClickListener{

    private FragmentFacilityQuantitySheetBinding binding;
    private ResultDialogCallback dialogCallback;
    private final List<OleKeyValuePair> list =  new ArrayList<>();
    private CountAdapter countAdapter;
    private String value = "";

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }

    public FacilityQuantitySheetFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFacilityQuantitySheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        list.clear();
        list.add(new OleKeyValuePair("0", "0"));
        list.add(new OleKeyValuePair("1", "1"));
        list.add(new OleKeyValuePair("2", "2"));
        list.add(new OleKeyValuePair("3", "3"));
        list.add(new OleKeyValuePair("4", "4"));
        list.add(new OleKeyValuePair("5", "5"));
        list.add(new OleKeyValuePair("6", "6"));
        list.add(new OleKeyValuePair("7", "7"));
        list.add(new OleKeyValuePair("8", "8"));


        //showDocDetails(fileId,clubId);

//        selectedDuration = durationList.get(0).getKey();
        LinearLayoutManager countLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.countRecyclerVu.setLayoutManager(countLayoutManager);
        countAdapter = new CountAdapter(getContext(), list);
        countAdapter.setOnItemClickListener(itemClickListener);
        binding.countRecyclerVu.setAdapter(countAdapter);

        binding.btnClose.setOnClickListener(this);
        binding.btnDone.setOnClickListener(this);


        return view;
    }

    CountAdapter.OnItemClickListener itemClickListener = (v, pos) -> {
        countAdapter.setSelectedIndex(pos);
        value = list.get(pos).getValue();
    };

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
            dismiss();
        }
        else if (v == binding.btnDone){
            dialogCallback.didSubmitResult(FacilityQuantitySheetFragment.this, value);
        }

    }

    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, String value);
    }
}