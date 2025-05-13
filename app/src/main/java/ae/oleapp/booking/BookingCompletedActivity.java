package ae.oleapp.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ae.oleapp.R;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityBookingCompletedBinding;
import ae.oleapp.databinding.ActivityBookingConfirmedBinding;
import ae.oleapp.databinding.ActivityCompleteBookingBinding;

public class BookingCompletedActivity extends BaseActivity implements View.OnClickListener {

    private ActivityBookingCompletedBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingCompletedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

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