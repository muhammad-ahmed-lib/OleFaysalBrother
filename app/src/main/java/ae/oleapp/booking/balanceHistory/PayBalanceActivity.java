package ae.oleapp.booking.balanceHistory;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.adapters.BalanceAdapter;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.databinding.ActivityBalanceBinding;
import ae.oleapp.databinding.ActivityPayBalanceBinding;
import ae.oleapp.models.Balance;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;

public class PayBalanceActivity extends BaseActivity implements View.OnClickListener {

    private ActivityPayBalanceBinding binding;
    private String paymentType = "";
    private final String bookingId = "";
    private String id = "";
    private String type = "";
    private String userId = "";
    private String callUserId = "";
    private EasyImage easyImage;
    private File file = new File("");
    private String filePath = "";
    private Balance balance;
    private String discount = "0";
    private boolean payAll = false;
    private String clubId = "";
    private String finalPrice = "", currency = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayBalanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            balance = new Gson().fromJson(bundle.getString("balance"), Balance.class);
            clubId = bundle.getString("club_id");
            finalPrice = bundle.getString("finalPrice");
            currency = bundle.getString("curr");
            type = bundle.getString("type");
            payAll = bundle.getBoolean("payall");
            id = bundle.getString("id");
            if (type.equalsIgnoreCase("call")){
                callUserId = id;
            }
            else{
                userId = id;
            }
        }

        if (balance != null){
            binding.tvPrice.setText(String.valueOf(balance.getAmount()));
            binding.tvPrice2.setText(String.valueOf(balance.getAmount()));
            binding.tvCurrency.setText(balance.getCurrency());
            binding.tvCurrency2.setText(balance.getCurrency());
            binding.tvCurrency3.setText(balance.getCurrency());
            binding.tvCurrency4.setText(balance.getCurrency());

        }
        else{
            binding.tvPrice.setText(String.valueOf(finalPrice));
            binding.tvPrice2.setText(String.valueOf(finalPrice));
            binding.tvCurrency.setText(currency);
            binding.tvCurrency2.setText(currency);
            binding.tvCurrency3.setText(currency);
            binding.tvCurrency4.setText(currency);

        }


        binding.btnBack.setOnClickListener(this);
        binding.cashVu.setOnClickListener(this);
        binding.cardVu.setOnClickListener(this);
        binding.walletVu.setOnClickListener(this);
        binding.btnPay.setOnClickListener(this);
        binding.imgVuReceipt.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnBack){
            finish();
        }
        else if (v == binding.cashVu) {
            setPaymentType("cash");
        }
        else if (v == binding.cardVu) {
            setPaymentType("card");

        }
        else if (v == binding.walletVu) {
            setPaymentType("wallet");
        }
        else if (v == binding.btnPay) {
            if (!binding.etDiscount.getText().toString().isEmpty()){
                discount = binding.etDiscount.getText().toString();
            }
            if (paymentType.isEmpty()){
                Functions.showToast(getContext(), "Please Select Payment method", FancyToast.INFO);
                return;
            }
            if (paymentType.equalsIgnoreCase("CARD") && (filePath == null || filePath.isEmpty())){
                Functions.showToast(getContext(), "Please add invoice", FancyToast.INFO);
                return;
            }
            if (payAll){
                int totalAmt = Integer.parseInt(binding.etNote.getText().toString() + discount);
                if (!finalPrice.equalsIgnoreCase(String.valueOf(totalAmt))){
                    Functions.showToast(getContext(), "Sorry the total paid amount is not equal to full amount: "+finalPrice, FancyToast.INFO);
                    return;
                }
                collectBalance(true, "", clubId, paymentType, binding.etNote.getText().toString(), binding.etAmount.getText().toString(), discount);
            }
            else{
                collectBalance(true, String.valueOf(balance.getBooking().getId()), clubId, paymentType, binding.etNote.getText().toString(), binding.etAmount.getText().toString(), discount);
            }


        }
        else if (v == binding.imgVuReceipt) {
            pickImage();
        }
    }

    private void pickImage() {
        String[] permissions = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES};
        }else{
            permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        }
        Permissions.check(getContext(), permissions, null/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                // do your task.
                easyImage = new EasyImage.Builder(getContext())
                        .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                        .setCopyImagesToPublicGalleryFolder(false)
                        .allowMultiple(false).build();
                easyImage.openChooser(getContext());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                filePath = resultUri.getPath();
                file = new File(filePath);
                Glide.with(getApplicationContext()).load(file).into(binding.imgVuReceipt);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        else {
            easyImage.handleActivityResult(requestCode, resultCode, data, getContext(), new EasyImage.Callbacks() {
                @Override
                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
                    if (mediaFiles.length > 0) {
                        CropImage.activity(Uri.fromFile(mediaFiles[0].getFile()))
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(true).setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
                                .setAspectRatio(1,1)
                                .start(getContext());
                    }
                }

                @Override
                public void onImagePickerError(Throwable error, MediaSource source) {
                    Functions.showToast(getContext(), error.getLocalizedMessage(), FancyToast.ERROR);
                }

                @Override
                public void onCanceled(@NonNull MediaSource mediaSource) {

                }
            });
        }
    }

    private void setPaymentType(String val){
      if (val.equalsIgnoreCase("cash")) {
            paymentType = "CASH";
            filePath = null;
            binding.cashVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
            binding.tvCash.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

            binding.cardVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvCard.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));

            binding.walletVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvWallet.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));

        }
        else if (val.equalsIgnoreCase("card")) {
            paymentType = "CARD";
            binding.cardVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
            binding.tvCard.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

            binding.cashVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvCash.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));

            binding.walletVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvWallet.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));
        }
        else if (val.equalsIgnoreCase("wallet")) {
            paymentType = "OLE_WALLET";
            filePath = null;
            binding.walletVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.v5greenColor));
            binding.tvWallet.setTextColor(ContextCompat.getColor(getContext(), R.color.whiteColor));

            binding.cardVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvCard.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));

            binding.cashVu.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.whiteColor));
            binding.tvCash.setTextColor(ContextCompat.getColor(getContext(), R.color.v5_text_color));
        }

    }

    private void collectBalance(boolean isLoader, String bookingId, String clubId, String method, String notes, String amount, String discount) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext(), "Image processing"): null;
        MultipartBody.Part filePart = null;
        if (filePath != null) {
            File file = new File(filePath);
            String mimeTypeCover = getMimeType(file);
            RequestBody requestBody = RequestBody.create(file, MediaType.parse(mimeTypeCover));
            filePart = MultipartBody.Part.createFormData("receipt", file.getName(), requestBody);

        }
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.collectBalance(filePart,
                RequestBody.create(bookingId, MediaType.parse("text/plain")),
                RequestBody.create(clubId, MediaType.parse("text/plain")),
                RequestBody.create(userId, MediaType.parse("text/plain")),
                RequestBody.create(callUserId, MediaType.parse("text/plain")),
                RequestBody.create(method, MediaType.parse("text/plain")),
                RequestBody.create(notes, MediaType.parse("text/plain")),
                RequestBody.create(amount, MediaType.parse("text/plain")),
                RequestBody.create(discount, MediaType.parse("text/plain")));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                }
                else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                }
                else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }


}