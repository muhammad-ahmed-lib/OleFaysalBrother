package ae.oleapp.signup;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.OleactivityUserTypeBinding;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

public class UserTypeActivity extends BaseActivity implements View.OnClickListener {

    private OleactivityUserTypeBinding binding;
    private final String userType = "";
    private String phone = "", countryCode = "", source = "";
    private int countryId;
    private String userModule = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityUserTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            countryId = bundle.getInt("country_id");
            countryCode = bundle.getString("country_code", "");
            phone = bundle.getString("phone", "");
            source = bundle.getString("source", "");

        }

        userModule = Functions.getPrefValue(getContext(), Constants.kAllowModule);

        binding.btnBack.setOnClickListener(this);
//        binding.btnNext.setOnClickListener(this);
        binding.relPlayer.setOnClickListener(this);
        binding.relOwner.setOnClickListener(this);
        binding.btnPlayer.setOnClickListener(this);
        binding.btnOwner.setOnClickListener(this);
        //binding.relReferee.setOnClickListener(this);
        if (!userModule.equalsIgnoreCase("ALL")){
            binding.relOwner.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack) {
            finish();
        }
        else if (v == binding.relPlayer || v == binding.btnPlayer) {
        }
        else if (v == binding.relOwner || v == binding.btnOwner) {
            nextClicked(Constants.kOwnerType);

        }
    }

    private void nextClicked(String userType) {
   if (userType.equalsIgnoreCase(Constants.kOwnerType)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getResources().getString(R.string.club_owner))
                    .setMessage(getResources().getString(R.string.owner_signup_desc))
                    .setPositiveButton(getResources().getString(R.string.continue_), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (source.equalsIgnoreCase("otp")){
                                Intent intent = new Intent(getContext(), OwnerSignupActivity.class);
                                intent.putExtra("country_id", countryId);
                                intent.putExtra("country_code", countryCode);
                                intent.putExtra("phone", phone);
                                intent.putExtra("source", source);
                                startActivity(intent);
                                finish();
                            }else{
                                Intent intent = new Intent(getContext(), OwnerSignupActivity.class);
                                intent.putExtra("source", source);
                                startActivity(intent);
                                finish();
                            }


                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
            builder.show();
        }
    }

}
