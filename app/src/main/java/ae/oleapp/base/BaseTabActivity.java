package ae.oleapp.base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.special.ResideMenu.ResideMenu;

import org.json.JSONObject;

import java.net.UnknownHostException;

import ae.oleapp.R;
import ae.oleapp.activities.LoginActivity;
import ae.oleapp.activities.OleSavedCardsActivity;
import ae.oleapp.activities.OleSettingsActivity;
import ae.oleapp.activities.OleCreditActivity;
import ae.oleapp.activities.OleWebVuActivity;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleBookingCountActivity;
import ae.oleapp.owner.OleClubPricingActivity;
import ae.oleapp.booking.schedule.OleScheduleListActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseTabActivity extends BaseActivity {

    protected ResideMenu resideMenu;
    protected RelativeLayout ownerCardView,tcVu, ppVu, deleteUserAccount, rankVu, globalRankVu, shopOrderVu, wishlistVu, paymentVu, savedCardVu, oleCreditVu, addPriceVu, membershipPlansVu, scheduleVu, settingVu, shareVu, playerSearchVu, footballVu, padelVu;
    protected ImageView userImageVu, imgVuFootball, imgVuPadel, imgVuSide, emojiImgVu, shirtImgVu;
    protected TextView tvName, tvFootball, tvPadel, tvRank;
    protected LinearLayout switchVu;
    protected MaterialCardView switchCard;
    protected CardView playerCardView;
    UserInfo info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }

                // Get new FCM registration token
                String token = task.getResult();
                if (Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                    sendFcmTokenApi(token);
                }
            }
        });
        info = Functions.getUserinfo(getContext());
    }

    protected void setupMenu() {
        resideMenu = new ResideMenu(getContext(), R.layout.oleside_menu, R.layout.oleside_menu);
        resideMenu.setBackgroundColor(getResources().getColor(R.color.whiteColor));
        resideMenu.attachToActivity(getContext());
        resideMenu.setScaleValue(0.6f);
        resideMenu.setShadowVisible(false);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        View menuVu;
        if (Functions.getAppLangStr(getContext()).equalsIgnoreCase("ar")) {
            menuVu = resideMenu.getRightMenuView();
        }
        else {
            menuVu = resideMenu.getLeftMenuView();
        }
        rankVu = menuVu.findViewById(R.id.rank_vu);
        rankVu.setOnClickListener(clickListener);
        globalRankVu = menuVu.findViewById(R.id.global_rank_vu);
        globalRankVu.setOnClickListener(clickListener);
        shopOrderVu = menuVu.findViewById(R.id.shop_order_vu);
        shopOrderVu.setOnClickListener(clickListener);
        wishlistVu = menuVu.findViewById(R.id.wishlist_vu);
        wishlistVu.setOnClickListener(clickListener);
        paymentVu = menuVu.findViewById(R.id.payment_vu);
        paymentVu.setOnClickListener(clickListener);
        savedCardVu = menuVu.findViewById(R.id.card_vu);
        savedCardVu.setOnClickListener(clickListener);
        oleCreditVu = menuVu.findViewById(R.id.ole_credit_vu);
        oleCreditVu.setOnClickListener(clickListener);
        addPriceVu = menuVu.findViewById(R.id.add_price_vu);
        scheduleVu = menuVu.findViewById(R.id.schedule_vu);
        scheduleVu.setOnClickListener(clickListener);
        playerSearchVu = menuVu.findViewById(R.id.player_search_vu);
        playerSearchVu.setOnClickListener(clickListener);
        membershipPlansVu = menuVu.findViewById(R.id.membership_plan_vu);
        membershipPlansVu.setOnClickListener(clickListener);
        settingVu = menuVu.findViewById(R.id.settings_vu);
        settingVu.setOnClickListener(clickListener);
        shareVu = menuVu.findViewById(R.id.share_vu);
        shareVu.setOnClickListener(clickListener);
        RelativeLayout chatVu = menuVu.findViewById(R.id.chat_vu);
        chatVu.setOnClickListener(clickListener);
        RelativeLayout signOutVu = menuVu.findViewById(R.id.sign_out_vu);
        signOutVu.setOnClickListener(clickListener);
        deleteUserAccount = menuVu.findViewById(R.id.delete_user_acc);
        deleteUserAccount.setOnClickListener(clickListener);
        CardView signInVu = menuVu.findViewById(R.id.sign_in_vu);
        signInVu.setOnClickListener(clickListener);
        RelativeLayout relProfile = menuVu.findViewById(R.id.rel_profile);
        relProfile.setOnClickListener(clickListener);
        //PlayercardView.findViewById(R.id.cardvu);
        //OwnercardView.findViewById(R.id.ownercardvu);
         playerCardView = menuVu.findViewById(R.id.cardvu);
         ownerCardView = menuVu.findViewById(R.id.ownercardvu);

        userImageVu = menuVu.findViewById(R.id.img_vu);
        emojiImgVu = menuVu.findViewById(R.id.emoji_img_vu);
        shirtImgVu = menuVu.findViewById(R.id.shirt_img_vu);
        tvName = menuVu.findViewById(R.id.tv_name);
        //switchVu = menuVu.findViewById(R.id.switch_vu);
        //switchCard = menuVu.findViewById(R.id.switch_card);
        imgVuSide = menuVu.findViewById(R.id.img_vu_side);
        tvFootball = menuVu.findViewById(R.id.tv_football);
        tvPadel = menuVu.findViewById(R.id.tv_padel);
        imgVuFootball = menuVu.findViewById(R.id.img_football);
        tvRank = menuVu.findViewById(R.id.tv_rank);
        imgVuPadel = menuVu.findViewById(R.id.img_padel);
        //footballVu = menuVu.findViewById(R.id.football_vu);
        //footballVu.setOnClickListener(clickListener);
        //padelVu = menuVu.findViewById(R.id.padel_vu);
        //padelVu.setOnClickListener(clickListener);
        tcVu = menuVu.findViewById(R.id.tc_vu);
        tcVu.setOnClickListener(clickListener);
        ppVu = menuVu.findViewById(R.id.pp_vu);
        ppVu.setOnClickListener(clickListener);



        if (Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            signInVu.setVisibility(View.GONE);
            signOutVu.setVisibility(View.VISIBLE);
            deleteUserAccount.setVisibility(View.VISIBLE);
                Glide.with(getContext()).load(info.getBibUrl()).placeholder(R.drawable.bibl).into(shirtImgVu);  //(ImageView) menuVu.findViewById(R.id.shirt_img_vu));
                Glide.with(getContext()).load(info.getEmojiUrl()).into(emojiImgVu);
        }
        else {
            signInVu.setVisibility(View.VISIBLE);
            signOutVu.setVisibility(View.GONE);
            deleteUserAccount.setVisibility(View.GONE);
        }

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resideMenu.closeMenu();
            if (v.getId() == R.id.rank_vu) {
                if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {

                }

            }
            else if (v.getId() == R.id.global_rank_vu) {

            }
            else if (v.getId() == R.id.shop_order_vu) {
                if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                    Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                    return;
                }
            }
            else if (v.getId() == R.id.wishlist_vu) {
                if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                    Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                    return;
                }
            }
            else if (v.getId() == R.id.schedule_vu) {
                startActivity(new Intent(getContext(), OleScheduleListActivity.class));
            }
            else if (v.getId() == R.id.player_search_vu) {
                startActivity(new Intent(getContext(), OleBookingCountActivity.class));
            }
            else if (v.getId() == R.id.membership_plan_vu) {
                Intent intent = new Intent(getContext(), OleClubPricingActivity.class);
                intent.putExtra("can_choose", false);
                startActivity(intent);
            }
            else if (v.getId() == R.id.settings_vu) {
                startActivity(new Intent(getContext(), OleSettingsActivity.class));
            }
            else if (v.getId() == R.id.share_vu) {

            }
            else if (v.getId() == R.id.sign_out_vu) {
                signoutClicked();
            }
            else if (v.getId() == R.id.sign_in_vu) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                editor.remove(Constants.kAppModule);
                editor.apply();
                finish();
            }
            else if (v.getId() == R.id.rel_profile) {

            }
            else if (v.getId() == R.id.chat_vu) {
                String url = "https://api.whatsapp.com/send?phone=+971547215551";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
            else if (v.getId() == R.id.payment_vu) {
                if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                    Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                    return;
                }
            }
            else if (v.getId() == R.id.card_vu) {
                if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                    Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                    return;
                }
                Intent intent = new Intent(getContext(), OleSavedCardsActivity.class);
                startActivity(intent);
            }
            else if (v.getId() == R.id.ole_credit_vu) {
                if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                    Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                    return;
                }
                Intent intent = new Intent(getContext(), OleCreditActivity.class);
                startActivity(intent);
            }
//            else if (v.getId() == R.id.football_vu) {
//                imgVuFootball.setVisibility(View.VISIBLE);
//                imgVuPadel.setVisibility(View.GONE);
//                tvFootball.setTextColor(getResources().getColor(R.color.whiteColor));
//                tvPadel.setTextColor(getResources().getColor(R.color.greenColor));
//                if (!Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kFootballModule)) {
//                    SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
//                    editor.putString(Constants.kAppModule, Constants.kFootballModule);
//                    editor.apply();
//                    Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//            else if (v.getId() == R.id.padel_vu) {
//                imgVuFootball.setVisibility(View.GONE);
//                imgVuPadel.setVisibility(View.VISIBLE);
//                tvFootball.setTextColor(getResources().getColor(R.color.greenColor));
//                tvPadel.setTextColor(getResources().getColor(R.color.whiteColor));
//                if (!Functions.getPrefValue(getContext(), Constants.kAppModule).equalsIgnoreCase(Constants.kPadelModule)) {
//                    SharedPreferences.Editor editor = getContext().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
//                    editor.putString(Constants.kAppModule, Constants.kPadelModule);
//                    editor.apply();
//                    Intent intent = new Intent(getContext(), OlePlayerMainTabsActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
//                }
//            }
            else if (v.getId() == R.id.delete_user_acc){
                if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
                    Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
                }else{
                    DeleteUserAccountClicked();
                }
            }
            else if (v.getId() == R.id.tc_vu) {
                Intent intent = new Intent(getContext(), OleWebVuActivity.class);
                intent.putExtra("url","tc");
                startActivity(intent);

            }
            else if (v.getId() == R.id.pp_vu) {
                Intent intent = new Intent(getContext(), OleWebVuActivity.class);
                intent.putExtra("url","pp");
                startActivity(intent);

            }
        }
    };

    private void signoutClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.sign_out))
                .setMessage(getResources().getString(R.string.do_you_want_signout))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logout();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }


    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
        editor.remove(Constants.kIsSignIn);
        editor.remove(Constants.kUserInfo);
        editor.remove(Constants.kUserType);
        editor.remove(Constants.kCurrency);
        editor.remove(Constants.kAppModule);
        editor.apply();

        AppManager.getInstance().countries.clear();
        AppManager.getInstance().countryPhoneLists.clear();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Glide.get(getContext()).clearDiskCache();
            }
        });

        logoutApi();

        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    private void logoutApi() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        String uniqueID = Functions.getPrefValue(this, Constants.kDeviceUniqueId);
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.logout(Functions.getAppLang(getContext()), Functions.getPrefValue(getContext(), Constants.kUserID), uniqueID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
                            SharedPreferences.Editor editor = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE).edit();
                            editor.remove(Constants.kUserID);
                            editor.remove(Constants.kaccessToken);
                            editor.apply();
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

    private void DeleteUserAccountClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.delete_account))
                .setMessage(getResources().getString(R.string.confirm_delete_account))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        disableUserAccountApi();
                        logout();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        builder.show();
    }

    private void disableUserAccountApi() {
        KProgressHUD hud = Functions.showLoader(getContext(), "Image processing");
        Call<ResponseBody> call = AppManager.getInstance().apiInterface.disableUserAccountApi(Functions.getAppLang(getContext()),Functions.getPrefValue(getContext(), Constants.kUserID));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.SUCCESS);
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
