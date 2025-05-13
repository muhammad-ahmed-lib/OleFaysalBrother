package ae.oleapp.partner.bottomSheets;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ae.oleapp.R;
import ae.oleapp.databinding.FragmentChangeCompanyBottomSheetBinding;
import ae.oleapp.models.AddedBy;
import ae.oleapp.models.Club;
import ae.oleapp.partner.adapters.VerticalClubAdapter;
import ae.oleapp.partner.adapters.VerticalPartnerAdapter;
import ae.oleapp.util.AppManager;

public class ChangeCompanyBottomSheet extends DialogFragment implements View.OnClickListener{

    private FragmentChangeCompanyBottomSheetBinding binding;
    private final List<Club> clubList = new ArrayList<>();
    private VerticalClubAdapter adapter;
    private ResultDialogCallback dialogCallback;
    private String clubId = "";

    public void setDialogCallback(ResultDialogCallback dialogCallback) {
        this.dialogCallback = dialogCallback;
    }
    public ChangeCompanyBottomSheet() {
        // Required empty public constructor
    }
    public ChangeCompanyBottomSheet(String clubId) {
        this.clubId = clubId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTransparentStyle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChangeCompanyBottomSheetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        if (AppManager.getInstance().clubs != null) {
            clubList.addAll(AppManager.getInstance().clubs);
        }

        if (clubId != null) {
            clubList.removeIf(club -> club.getId() != null && club.getId().equalsIgnoreCase(clubId));
        }

        LinearLayoutManager VerClubLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerVu.setLayoutManager(VerClubLayoutManager);
        adapter = new VerticalClubAdapter(getContext(), clubList);
        adapter.setItemClickListener(itemClickListener);
        binding.recyclerVu.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        binding.btnClose.setOnClickListener(this);
        binding.btnConfirm.setOnClickListener(this);

        return view;
    }

    VerticalClubAdapter.ItemClickListener itemClickListener = new VerticalClubAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            adapter.setSelectedId(clubList.get(pos).getId());
            clubId = clubList.get(pos).getId();
        }

    };

    @Override
    public void onClick(View v) {
        if (v == binding.btnClose){
            dismiss();
        }
        else if (v == binding.btnConfirm) {
            dialogCallback.didSubmitResult(this, true, clubId);
        }
    }

    public interface ResultDialogCallback {
        void didSubmitResult(DialogFragment df, boolean isConfirmed, String clubId);
    }
}