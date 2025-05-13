package ae.oleapp.owner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jpeng.jptabbar.OnTabSelectListener;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.special.ResideMenu.ResideMenu;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.activities.OleNotificationsActivity;
import ae.oleapp.base.BaseActivity;
import ae.oleapp.base.BaseTabActivity;
import ae.oleapp.databinding.OleactivityOwnerMainTabsBinding;
import ae.oleapp.fragments.OleBookingListFragment;
import ae.oleapp.fragments.OleClubListFragment;
import ae.oleapp.fragments.OleOwnerProfileFragment;
import ae.oleapp.models.Country;
import ae.oleapp.models.OleFieldData;
import ae.oleapp.models.UserInfo;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;

public class OleOwnerMainTabsActivity extends BaseTabActivity {

    private OleactivityOwnerMainTabsBinding binding;
    private final List<Fragment> list = new ArrayList<>();
    private final OleClubListFragment oleClubListFragment = new OleClubListFragment();
    private final OleBookingListFragment oleBookingListFragment = new OleBookingListFragment();
    private final OleOwnerProfileFragment oleOwnerProfileFragment = new OleOwnerProfileFragment();
    private int selectedTabIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OleactivityOwnerMainTabsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        makeStatusbarTransperant();

        setupTabLayout();
        binding.contentMainLay.contentMain.tabBar.setSelectTab(0);
        binding.contentMainLay.contentMain.viewPager.setCurrentItem(0);
        setupMenu();

        getCountriesAPI(new CountriesCallback() {
            @Override
            public void getCountries(List<Country> countries) {
                AppManager.getInstance().countries = countries;
            }
        });

        if (Functions.getPrefValue(getContext(), Constants.kUserType).equalsIgnoreCase(Constants.kOwnerType)) {
            paymentVu.setVisibility(View.GONE);
            addPriceVu.setVisibility(View.GONE);
            oleCreditVu.setVisibility(View.GONE);
            savedCardVu.setVisibility(View.GONE);
            wishlistVu.setVisibility(View.GONE);
            shopOrderVu.setVisibility(View.GONE);
            //switchVu.setVisibility(View.GONE);
            playerCardView.setVisibility(View.GONE);
            deleteUserAccount.setVisibility(View.GONE);
            ownerCardView.setVisibility(View.VISIBLE);

        }
        globalRankVu.setVisibility(View.GONE);

        KeyboardVisibilityEvent.setEventListener(getContext(), new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen) {
                    binding.contentMainLay.contentMain.tabBar.setVisibility(View.GONE);
                    binding.contentMainLay.contentMain.tabBar.getMiddleView().setVisibility(View.GONE);
                }
                else {
                    binding.contentMainLay.contentMain.tabBar.setVisibility(View.VISIBLE);
                    binding.contentMainLay.contentMain.tabBar.getMiddleView().setVisibility(View.VISIBLE);
                }
            }
        });

        if (AppManager.getInstance().oleFieldData == null) {
            getFieldDataAPI(new BaseActivity.FieldDataCallback() {
                @Override
                public void getFieldData(OleFieldData oleFieldData) {
                    AppManager.getInstance().oleFieldData = oleFieldData;
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")){
            callUnreadNotifAPI();
        }

        populateSideMenuData();

    }


    @Override
    public void onBackPressed() {
        if (resideMenu.isOpened()) {
            resideMenu.closeMenu();
        }
        else {
            super.onBackPressed();
        }
    }

    private void callUnreadNotifAPI() {
        getUnreadNotificationAPI(new UnreadCountCallback() {
            @Override
            public void unreadNotificationCount(int count) {
                AppManager.getInstance().notificationCount = count;
                setBadgeValue();
            }
        });
    }

    public void populateSideMenuData() {
        UserInfo userInfo = Functions.getUserinfo(getContext());
        if (userInfo != null) {
            tvName.setText(userInfo.getName());
//            if (userInfo.getPhotoUrl().isEmpty()) {
//                userImageVu.setImageResource(R.drawable.owner_active);
//            }
//            else {
//                //Glide.with(getContext()).load(userInfo.getPhotoUrl()).placeholder(R.drawable.owner_active).into(userImageVu);
//                Glide.with(getContext())
//                        .load(userInfo.getPhotoUrl()).placeholder(R.drawable.owner_active)
//                        .apply(RequestOptions.circleCropTransform())
//                        .into(userImageVu);
//            }
            if (userInfo.getLevel() != null && !userInfo.getLevel().isEmpty() && !userInfo.getLevel().getValue().equalsIgnoreCase("")) {
                tvRank.setVisibility(View.VISIBLE);
                tvRank.setText(String.format("LV: %s", userInfo.getLevel().getValue()));
            }
            else {
                tvRank.setVisibility(View.INVISIBLE);
            }
        }
        else {
            tvRank.setVisibility(View.INVISIBLE);
        }
        setBadgeValue();
    }

    private void setBadgeValue() {
        if (oleClubListFragment.isVisible()) {
            oleClubListFragment.setBadgeValue();
        }
        else if (oleBookingListFragment.isVisible()) {
            oleBookingListFragment.setBadgeValue();
        }
        else if (oleOwnerProfileFragment.isVisible()) {
            oleOwnerProfileFragment.setBadgeValue();
        }
    }

    private void setupTabLayout() {
        binding.contentMainLay.contentMain.tabBar.setTitles(getString(R.string.clubs), getString(R.string.bookings), getString(R.string.earnings), getString(R.string.profile))
                .setNormalIcons(R.drawable.home_inactive, R.drawable.booking_inactive, R.drawable.earning_tab, R.drawable.profile_tab).generate();
        binding.contentMainLay.contentMain.tabBar.setNormalColor(getResources().getColor(R.color.separatorColor));
        binding.contentMainLay.contentMain.tabBar.setSelectedColor(getResources().getColor(R.color.v5greenColor));
        binding.contentMainLay.contentMain.tabBar.setIconSize(25);
        binding.contentMainLay.contentMain.tabBar.setTabTextSize(12);
        binding.contentMainLay.contentMain.tabBar.setPadding(5, 0, 5, 0);
        list.add(oleClubListFragment);
        list.add(oleBookingListFragment);
        list.add(oleOwnerProfileFragment);
        binding.contentMainLay.contentMain.tabBar.setGradientEnable(false);
        binding.contentMainLay.contentMain.tabBar.setPageAnimateEnable(true);

        binding.contentMainLay.contentMain.viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), list));
        binding.contentMainLay.contentMain.tabBar.setContainer(binding.contentMainLay.contentMain.viewPager);

        binding.contentMainLay.contentMain.tabBar.setTabListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int index) {
                selectedTabIndex = index;
//               devicesLoginLimit();
            }

            @Override
            public boolean onInterruptSelect(int index) {
                return false;
            }
        });

        if(binding.contentMainLay.contentMain.tabBar.getMiddleView() != null)
            binding.contentMainLay.contentMain.tabBar.getMiddleView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), OleAddFieldActivity.class);
                    intent.putExtra("club_id",  oleClubListFragment.selectedClubId);
                    startActivity(intent);
                }
            });
    }

    public void notificationsClicked() {
        if (!Functions.getPrefValue(getContext(), Constants.kIsSignIn).equalsIgnoreCase("1")) {
            Functions.showToast(getContext(), getString(R.string.please_login_first), FancyToast.ERROR, FancyToast.LENGTH_SHORT);
            return;
        }
        startActivity(new Intent(getContext(), OleNotificationsActivity.class));
    }

    public void menuClicked() {
        if (Functions.getAppLangStr(getContext()).equalsIgnoreCase("ar")) {
            resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
        }
        else {
            resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
        }
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> list;

        public PagerAdapter(FragmentManager supportFragmentManager, List<Fragment> list) {
            super(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }


        @Override
        public int getCount() {
            return list.size();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (oleOwnerProfileFragment.isVisible()) {
            oleOwnerProfileFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}