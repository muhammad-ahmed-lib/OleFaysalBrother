package ae.oleapp.presentation.ui.inventory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ae.oleapp.R
import ae.oleapp.databinding.ActivityInventoryBinding
import ae.oleapp.databinding.ActivityInventoryPaymentBinding

class InventoryPaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInventoryPaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}