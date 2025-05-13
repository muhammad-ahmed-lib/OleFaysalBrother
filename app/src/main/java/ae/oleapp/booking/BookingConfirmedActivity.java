package ae.oleapp.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityBookingConfirmedBinding;

public class BookingConfirmedActivity extends BaseActivity implements View.OnClickListener {

    private ActivityBookingConfirmedBinding binding;
    private String date = "", time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingConfirmedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            date = bundle.getString("date", "");
            time = bundle.getString("time", "");
        }

        binding.taglineText.setText(String.format("Your booking has been confirmed for %s from %s", date, time));
        binding.btnBack.setOnClickListener(this);
        binding.btnHome.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack || v == binding.btnHome){
            Intent intent = new Intent();
            intent.putExtra("home_clicked", true);
            setResult(RESULT_OK, intent);
            finish();
        }
        else if (v == binding.btnGotoBooking){

        }

    }
}