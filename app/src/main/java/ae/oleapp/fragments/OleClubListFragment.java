package ae.oleapp.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ae.oleapp.R;
import ae.oleapp.activities.DocumentHomeActivity;
import ae.oleapp.activities.OleFinanceActivity;
import ae.oleapp.activities.StatisticsActivity;
import ae.oleapp.adapters.OleClubNameAdapter;
import ae.oleapp.base.BaseFragment;
import ae.oleapp.databinding.OlefragmentClubListBinding;
import ae.oleapp.employee.ui.EmployeeActivity;
import ae.oleapp.inventory.OleInventoryActivity;
import ae.oleapp.models.Club;
import ae.oleapp.models.OwnerClub;
import ae.oleapp.models.UserInfo;
import ae.oleapp.owner.OleAddClubActivity;
import ae.oleapp.owner.OleAddFieldActivity;
import ae.oleapp.owner.OleBookingCountActivity;
import ae.oleapp.owner.OleCashDepositActivity;
import ae.oleapp.owner.OleClubPricingActivity;
import ae.oleapp.owner.OleClubSettingsActivity;
import ae.oleapp.owner.OleFastBookingActivity;
import ae.oleapp.owner.OleOwnerMainTabsActivity;
import ae.oleapp.booking.schedule.OleScheduleListActivity;
import ae.oleapp.partner.PartnerHomeActivity;
import ae.oleapp.presentation.ui.inventory.InventoryActivity;
import ae.oleapp.promotions.PromotionActivity;
import ae.oleapp.util.AppManager;
import ae.oleapp.util.Constants;
import ae.oleapp.util.Functions;
import mumayank.com.airlocationlibrary.AirLocation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OleClubListFragment extends BaseFragment implements View.OnClickListener {

    private OlefragmentClubListBinding binding;
    private final List<Club> clubList = new ArrayList<>();
    private OleClubNameAdapter oleClubNameAdapter;

    public String selectedClubId = "";
    private int selectedIndex = 0;

    private final boolean isFootballAvailable = false;
    private final boolean isPadelAvailable = false;
    private CountDownTimer countDownTimer;

    public OleClubListFragment() {
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        binding = OlefragmentClubListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        LinearLayoutManager oleClubNameLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.clubNameRecyclerVu.setLayoutManager(oleClubNameLayoutManager);
        oleClubNameAdapter = new OleClubNameAdapter(getContext(), clubList);
        oleClubNameAdapter.setItemClickListener(clubNameClickListener);
        binding.clubNameRecyclerVu.setAdapter(oleClubNameAdapter);

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        binding.recyclerVu.setLayoutManager(layoutManager);
//        adapter = new OleClubListAdapter(getContext(), clubList,0);
//        //adapter.setCustomItemClickListener(customItemClickListener);
//        //adapter.setClubNameClicked(clubNameClickListner);
//        adapter.setItemClickListener(itemClickListener);
//        binding.recyclerVu.setAdapter(adapter);
//        binding.noStadiumVu.setVisibility(View.GONE);

        binding.relNotif.setOnClickListener(this);
        binding.relMenu.setOnClickListener(this);
        binding.btnNext.setOnClickListener(this);
        binding.btnBook.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLocation();
//        getOwnerClubList(clubList.isEmpty());
        setBadgeValue();
        //clubName.clear();  13/07/23
    }

    @Override
    public void onClick(View v) {
        if (v == binding.relNotif) {
            notifClicked();
        }
        else if (v == binding.relMenu) {
            menuClicked();
        }
        else if (v == binding.btnNext) {
            addClicked();
        }
        else if (v == binding.btnBook) {
            Intent intent = new Intent(getContext(), OleFastBookingActivity.class);
            intent.putExtra("is_padel", false);
            Gson gson = new Gson();
            intent.putExtra("club", gson.toJson(clubList.get(selectedIndex)));
            intent.putExtra("club_id", clubList.get(selectedIndex).getId());
            intent.putExtra("index", selectedIndex);
            startActivity(intent);
        }
    }

    private void addClicked() {
        if (!clubList.isEmpty()){
            if (clubList.get(selectedIndex).getOwnerClub().getTotalFields() == 0){

                Intent intent = new Intent(getContext(), OleAddFieldActivity.class);
                intent.putExtra("club_id", clubList.get(selectedIndex).getId());
                startActivity(intent);

            }
        }
        else{
            startActivity(new Intent(getContext(), OleAddClubActivity.class));
        }
    }

    private void menuClicked() {
        if (getActivity() instanceof OleOwnerMainTabsActivity) {
            ((OleOwnerMainTabsActivity) getActivity()).menuClicked();
        }
    }

    private void notifClicked() {
        if (getActivity() instanceof OleOwnerMainTabsActivity) {
            ((OleOwnerMainTabsActivity) getActivity()).notificationsClicked();
        }
    }

    public void setBadgeValue() {
        if (AppManager.getInstance().notificationCount > 0) {
            binding.toolbarBadge.setVisibility(View.VISIBLE);
            binding.toolbarBadge.setNumber(AppManager.getInstance().notificationCount);
        }
        else  {
            binding.toolbarBadge.setVisibility(View.GONE);
        }
    }


    OleClubNameAdapter.ItemClickListener clubNameClickListener = new OleClubNameAdapter.ItemClickListener() {
        @Override
        public void itemClicked(View view, int pos) {
            selectedIndex = pos;
            selectedClubId = String.valueOf(clubList.get(selectedIndex).getId()); //check this
            oleClubNameAdapter.setSelectedIndex(selectedIndex);
//            oleClubNameAdapter.setSelectedId(selectedClubId);
            getOwnerClubDetail(false, Integer.parseInt(selectedClubId), pos);
//            populateClubData(selectedIndex);
        }
    };

    private void populateClubData (int pos){

        UserInfo userInfo = Functions.getUserinfo(getContext());
        if (userInfo.getPicture().isEmpty()){
            Glide.with(getContext())
                    .load(R.drawable.partner_temp_img)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.profileImgVu);
        }else{
            Glide.with(getContext())
                    .load(userInfo.getPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.profileImgVu);
        }

        if (clubList.isEmpty()) {
            binding.disableSheet.setVisibility(View.VISIBLE);
            binding.disableSheet.setEnabled(false);
            binding.tvStartupTagline.setText("Add Company details");
            binding.progressBar.setProgress(0);
            binding.tvPercentage.setText("0%");
        }
        else {
            if (clubList.get(pos).getOwnerClub().getTotalFields() > 0){
                binding.disableSheet.setVisibility(View.GONE);
                binding.disableSheet.setEnabled(true);
                binding.stadiumDeActiveLay.setVisibility(View.GONE);
                binding.stadiumActiveLay.setVisibility(View.VISIBLE);
            }else{
                binding.disableSheet.setVisibility(View.VISIBLE);
                binding.disableSheet.setEnabled(false);
                binding.stadiumDeActiveLay.setVisibility(View.VISIBLE);
                binding.stadiumActiveLay.setVisibility(View.GONE);
                binding.tvStartupTagline.setText("Add Field Details");
                binding.progressBar.setProgress(50);
                binding.tvPercentage.setText("50%");
            }
        }


            // if (clubList.get(pos).getId().equalsIgnoreCase(selectedClubId)) {
            Club club = clubList.get(pos);
            if (!club.getBanner().isEmpty()) {
                Glide.with(requireActivity()).load(club.getBanner()).into(binding.imgVu);
            }
            // binding.tvName.setText(club.getName());
//            binding.tvLoc.setText(club.getCity());
            binding.tvLoc.setText(String.format("%s - %s", club.getOwnerClub().getDistance(), club.getOwnerClub().getCity()));

            if (club.getOwnerClub().getTotalReviews() == 0) {
                binding.tvRate.setText("0.0");
            }
            else {
                binding.tvRate.setText(String.valueOf(club.getOwnerClub().getTotalReviews()));
            }

        if (club.getOwnerClub().getOffer() != null && club.getOwnerClub().getOffer().getDiscountType() != null && club.getOwnerClub().getOffer().getDiscount() != null) {

            binding.offerVu.setVisibility(View.VISIBLE);

            if (club.getOwnerClub().getOffer().getDiscountType().equalsIgnoreCase("PERCENTAGE")) {
                binding.tvOffer.setText(String.format("%s%% off", club.getOwnerClub().getOffer().getDiscount()));
            } else {
                binding.tvOffer.setText(String.format("%s off", club.getOwnerClub().getOffer().getDiscount()));
            }
        } else {
            binding.offerVu.setVisibility(View.GONE);
        }

        if (club.getOwnerClub().getFeatured()) {
                binding.featureVu.setVisibility(View.VISIBLE);
            }
            else {
                binding.featureVu.setVisibility(View.GONE);
            }
            if (club.getOwnerClub().getTotalFavorites() == 0) {
                binding.tvFavCount.setText("0");
            }
            else {
                binding.tvFavCount.setText(String.valueOf(club.getOwnerClub().getTotalReviews()));
            }

//           if (clubList.get(pos).getClubType().equalsIgnoreCase(Constants.kPadelModule)){
//               binding.padelFastVu.setVisibility(View.VISIBLE);
//               binding.footballFastVu.setVisibility(View.GONE);
//           }else{
//               binding.padelFastVu.setVisibility(View.GONE);
//               binding.footballFastVu.setVisibility(View.VISIBLE);
//           }

//            if (isPadelAvailable) {
//                binding.btnPadelBooking.setVisibility(View.VISIBLE);
//            }
//            else {
//                binding.btnPadelBooking.setVisibility(View.GONE);
//            }
//            if (isFootballAvailable) {
//                binding.btnFootballBooking.setVisibility(View.VISIBLE);
//            }
//            else {
//                binding.btnFootballBooking.setVisibility(View.GONE);
//            }

            binding.tvEarning.setText(String.format("%s %s", club.getOwnerClub().getTotalEarning(), club.getOwnerClub().getCountry().getCurrency()));
            binding.tvBookings.setText(String.valueOf(club.getOwnerClub().getActiveBookings()));
            binding.tvHours.setText(String.valueOf(club.getOwnerClub().getTotalHours()));
            binding.tvConfirmed.setText(String.format("%s/%s", club.getOwnerClub().getConfirmedBookings(), club.getOwnerClub().getActiveBookings()));
            binding.tvPending.setText(String.valueOf(club.getOwnerClub().getWaitingUser()));
            binding.tvNew.setText(String.valueOf(club.getOwnerClub().getNewUsers()));

            binding.warningTag.setVisibility(View.GONE);
            binding.btnRenew.setVisibility(View.GONE);
            updateMembershipStatus(clubList.get(pos));

//            if (club.getOwnerClub().getSubscription().getStatus().equalsIgnoreCase("EXPIRED")) {
//                binding.warningTag.setVisibility(View.VISIBLE);
//                binding.btnRenew.setVisibility(View.VISIBLE);
//                binding.tvExpire.setText(getActivity().getString(R.string.membership_expired));
//                binding.tvExpire.setTextColor(ContextCompat.getColor(requireContext(), R.color.redColor));
//            }
//            else {
//                try {
//                    // Extract days from the "expiry_in" string
//                    String expiryIn = club.getOwnerClub().getSubscription().getExpiryIn();
//                    int days = Integer.parseInt(expiryIn.split(" ")[0]); // Extract the number before "DAYS"
//
//                    // Calculate the expiration date by adding days to the current date
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.add(Calendar.DAY_OF_YEAR, days);
//                    Date expiryDate = calendar.getTime();
//
//                    // Get the difference in milliseconds
//                    long timeRemainingInMillis = expiryDate.getTime() - System.currentTimeMillis();
//
//                    // Check if the subscription has expired
//                    if (timeRemainingInMillis <= 0) {
//                        binding.warningTag.setVisibility(View.VISIBLE);
//                        binding.btnRenew.setVisibility(View.VISIBLE);
//                        binding.tvExpire.setText(getActivity().getString(R.string.membership_expired));
//                        binding.tvExpire.setTextColor(ContextCompat.getColor(requireContext(), R.color.redColor));
//                    } else {
//                        // Calculate days left
//                        long daysLeft = timeRemainingInMillis / (1000 * 60 * 60 * 24);
//
//                        daysLeft = daysLeft -13;
//
//                        // Display only the days if more than 10
//                        if (daysLeft > 10) {
//                            binding.warningTag.setVisibility(View.GONE);
//                            binding.btnRenew.setVisibility(View.GONE);
//                            binding.tvExpire.setText(String.format("Expires in %d days", daysLeft));
//                            binding.tvExpire.setTextColor(ContextCompat.getColor(requireContext(), R.color.subTextColor));
//                        } else {
//                            // Show real-time countdown for 10 days or less
//                            binding.warningTag.setVisibility(View.VISIBLE);
//                            binding.btnRenew.setVisibility(View.VISIBLE);
//                            binding.tvExpire.setTextColor(ContextCompat.getColor(requireContext(), R.color.redColor));
//
//                            // Create a CountDownTimer to update every second
//                            new CountDownTimer(timeRemainingInMillis, 1000) {
//                                @Override
//                                public void onTick(long millisUntilFinished) {
//                                    long daysLeft = millisUntilFinished / (1000 * 60 * 60 * 24);
//                                    long hoursLeft = (millisUntilFinished / (1000 * 60 * 60)) % 24;
//                                    long minutesLeft = (millisUntilFinished / (1000 * 60)) % 60;
//                                    long secondsLeft = (millisUntilFinished / 1000) % 60;
//
//                                    // Format the remaining time
//                                    String timeRemaining = String.format("Expires in %d days %02dh %02dm %02ds", daysLeft, hoursLeft, minutesLeft, secondsLeft);
//                                    binding.tvExpire.setText(timeRemaining);
//                                }
//
//                                @Override
//                                public void onFinish() {
//                                    // Handle expiration
//                                    binding.tvExpire.setText(getActivity().getString(R.string.membership_expired));
//                                    binding.tvExpire.setTextColor(ContextCompat.getColor(requireContext(), R.color.redColor));
//                                    binding.warningTag.setVisibility(View.VISIBLE);
//                                    binding.btnRenew.setVisibility(View.VISIBLE);
//                                }
//                            }.start();
//                        }
//                    }
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                }
//
//            }

//            if (clubDetail.getFieldsCount() > 0) {
//                binding.infoVu.setVisibility(View.VISIBLE);
////                binding.noFieldVu.setVisibility(View.GONE);
//            }
//            else {
//                binding.infoVu.setVisibility(View.GONE);
////                binding.noFieldVu.setVisibility(View.VISIBLE);
//            }

//                    binding.relMain.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                                if (clubList.get(pos).getOwnerClub().getTotalFields() > 0) {
//                                    Intent intent = new Intent(getContext(), OleFieldListActivity.class);
//                                    Gson gson = new Gson();
//                                    intent.putExtra("club", gson.toJson(clubList.get(pos)));
//                                    startActivity(intent);
//                                }
//
//                        }
//                    });
                    binding.relFinance.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!clubList.get(pos).getId().isEmpty()) {
                                Intent intent = new Intent(getContext(), OleFinanceActivity.class);
                                intent.putExtra("club", clubList.get(pos).getId());
                                startActivity(intent);
                            }
                        }
                    });
                    binding.relInventory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          //  Intent inventoryIntent = new Intent(getContext(), OleInventoryActivity.class);
                            Intent inventoryIntent = new Intent(getContext(), InventoryActivity.class);
                            inventoryIntent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(inventoryIntent);
                        }
                    });
                    binding.relDeposit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent depositIntent = new Intent(getContext(), OleCashDepositActivity.class);
                            depositIntent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(depositIntent);
                        }
                    });
//                    binding.relGift.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(getContext(), GiftsActivity.class);
//                            intent.putExtra("club_id", clubList.get(pos).getId());
//                            startActivity(intent);
//
//                        }
//                    });
                    binding.relPromotion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), PromotionActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(intent);
                        }
                    });
                    binding.relStatistics.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), StatisticsActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(intent);

                        }
                    });
                    binding.relEmployees.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), EmployeeActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(intent);
//                            Intent intent = new Intent(getContext(), OleEmployeeListActivity.class);
//                            intent.putExtra("club_id", clubList.get(pos).getId());
//                            startActivity(intent);
//                            startActivity(new Intent(getContext(), OleEmployeeListActivity.class));
                        }
                    });
                    binding.relSettings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), OleClubSettingsActivity.class);
                            intent.putExtra("club", new Gson().toJson(clubList.get(pos)));
                            startActivity(intent);

                        }
                    });
                    binding.relShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    binding.relContinuous.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), OleScheduleListActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(intent);
                        }
                    });
                    binding.relPlayers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getContext(), OleBookingCountActivity.class));
                        }
                    });
                    binding.relTournament.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    binding.relSms.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

//                    binding.btnAddField.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(getContext(), OleAddFieldActivity.class);
//                            intent.putExtra("club_id", clubList.get(pos).getId());
//                            startActivity(intent);
//                        }
//                    });
                    binding.btnRenew.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), OleClubPricingActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            intent.putExtra("can_choose", true);
                            startActivity(intent);
                        }
                    });
//                    binding.btnPadelBooking.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(getContext(), OleFastBookingActivity.class);
//                            intent.putExtra("is_padel", true);
//                            Gson gson = new Gson();
//                            intent.putExtra("club", gson.toJson(clubList.get(selectedIndex)));
//                            startActivity(intent);
//                        }
//                    });
//                    binding.btnBook.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(getContext(), OleFastBookingActivity.class);
//                            intent.putExtra("is_padel", false); // Padel????
//                            Gson gson = new Gson();
//                            intent.putExtra("club", gson.toJson(clubList.get(selectedIndex)));
//                            startActivity(intent);
//                        }
//                    });
                    binding.relDocuments.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), DocumentHomeActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(intent);
                        }
                    });

                    binding.relPartner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), PartnerHomeActivity.class);
                            intent.putExtra("club_id", clubList.get(pos).getId());
                            startActivity(intent);
                        }
                    });
       // }
    }
    // Declare a CountDownTimer as a class-level variable

    private void updateMembershipStatus(Club club) {
        // Stop any existing timer when switching clubs
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (club.getOwnerClub().getSubscription().getStatus().equalsIgnoreCase("EXPIRED")) {
            binding.warningTag.setVisibility(View.VISIBLE);
            binding.btnRenew.setVisibility(View.VISIBLE);
            binding.tvExpire.setText(getString(R.string.membership_expired));
            binding.tvExpire.setTextColor(ContextCompat.getColor(requireContext(), R.color.redColor));
        } else {
            try {
                // Extract days from the "expiry_in" string
                String expiryIn = club.getOwnerClub().getSubscription().getExpiryIn();
                int days = Integer.parseInt(expiryIn.split(" ")[0]); // Extract the number before "DAYS"

                // Calculate the expiration date by adding days to the current date
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, days); // Calculate expiry date based on current date
                Date expiryDate = calendar.getTime();

                // Calculate the difference in milliseconds
                long timeRemainingInMillis = expiryDate.getTime() - System.currentTimeMillis();

                // Check if the subscription has expired
                if (timeRemainingInMillis <= 0) {
                    binding.warningTag.setVisibility(View.VISIBLE);
                    binding.btnRenew.setVisibility(View.VISIBLE);
                    binding.tvExpire.setText(getString(R.string.membership_expired));
                    binding.tvExpire.setTextColor(ContextCompat.getColor(requireContext(), R.color.redColor));
                } else {
                    // Calculate days left
                    long daysLeft = timeRemainingInMillis / (1000 * 60 * 60 * 24);

                    // Display only the days if more than 10
                    if (daysLeft > 10) {
                        binding.warningTag.setVisibility(View.GONE);
                        binding.btnRenew.setVisibility(View.GONE);
                        binding.tvExpire.setText(String.format("Expires in %d days", daysLeft));
                        binding.tvExpire.setTextColor(ContextCompat.getColor(requireContext(), R.color.subTextColor));
                    } else {
                        // Show real-time countdown for 10 days or less
                        binding.warningTag.setVisibility(View.VISIBLE);
                        binding.btnRenew.setVisibility(View.VISIBLE);
                        binding.tvExpire.setTextColor(ContextCompat.getColor(requireContext(), R.color.redColor));

                        // Initialize the CountDownTimer with the correct remaining time
                        countDownTimer = new CountDownTimer(timeRemainingInMillis, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                long daysLeft = millisUntilFinished / (1000 * 60 * 60 * 24);
                                long hoursLeft = (millisUntilFinished / (1000 * 60 * 60)) % 24;
                                long minutesLeft = (millisUntilFinished / (1000 * 60)) % 60;
                                long secondsLeft = (millisUntilFinished / 1000) % 60;

                                // Format the remaining time
                                String timeRemaining = String.format("Expires in %d days %02dh %02dm %02ds", daysLeft, hoursLeft, minutesLeft, secondsLeft);
                                binding.tvExpire.setText(timeRemaining);
                            }

                            @Override
                            public void onFinish() {
                                // Handle expiration
                                binding.tvExpire.setText(getString(R.string.membership_expired));
                                binding.tvExpire.setTextColor(ContextCompat.getColor(requireContext(), R.color.redColor));
                                binding.warningTag.setVisibility(View.VISIBLE);
                                binding.btnRenew.setVisibility(View.VISIBLE);
                            }
                        }.start();
                    }
                }
            } catch (NumberFormatException e) {
                // Optionally handle the exception or show a user-friendly message
                binding.tvExpire.setText("Invalid Expiry Info");
                binding.tvExpire.setTextColor(ContextCompat.getColor(requireContext(), R.color.redColor));
            }
        }
    }

    private void getOwnerClubList(boolean isLoader, Double latitude, Double longitude, String date) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getOwnerClubList(latitude,longitude,date);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONArray data = object.getJSONArray(Constants.kData);
                            if (data.length() > 0){
                                Gson gson = new Gson();
                                clubList.clear();

                                AppManager.getInstance().clubs.clear();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject clubJsonObject = data.getJSONObject(i);
                                    Club club = gson.fromJson(clubJsonObject.toString(), Club.class);
                                    int clubId = clubJsonObject.getInt("id");
                                    club.setId(String.valueOf(clubId));
                                    clubList.add(club);
                                    AppManager.getInstance().clubs.add(club);
                                }

                                oleClubNameAdapter.setSelectedIndex(selectedIndex);
                                selectedClubId = String.valueOf(clubList.get(selectedIndex).getId());
                                oleClubNameAdapter.setSelectedId(selectedClubId);
                                getOwnerClubDetail(false, Integer.parseInt(selectedClubId), selectedIndex);

                            }
                            else{
                                if (clubList.isEmpty()) {
                                    binding.stadiumDeActiveLay.setVisibility(View.VISIBLE);
                                    binding.stadiumActiveLay.setVisibility(View.GONE);
                                    binding.disableSheet.setVisibility(View.VISIBLE);
                                    binding.disableSheet.setEnabled(false);
                                    binding.tvStartupTagline.setText("Add Company details");
                                    binding.progressBar.setProgress(0);
                                    binding.tvPercentage.setText("0%");
                                }
                            }



                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void getOwnerClubDetail(boolean isLoader, int clubId, int pos) {
        KProgressHUD hud = isLoader ? Functions.showLoader(getContext()) : null;
        Call<ResponseBody> call = AppManager.getInstance().apiInterfaceNew.getOwnerClubDetail(clubId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Functions.hideLoader(hud);
                if (response.body() != null) {
                    try {
                        JSONObject object = new JSONObject(response.body().string());
                        if (object.getInt(Constants.kStatus) == Constants.kSuccessCode) {
                            JSONObject data = object.getJSONObject(Constants.kData);
                            OwnerClub ownerClub = new Gson().fromJson(data.toString(), OwnerClub.class);
                            clubList.get(pos).setOwnerClub(ownerClub);
                            populateClubData(selectedIndex);


                        } else {
                            Functions.showToast(getContext(), object.getString(Constants.kMsg), FancyToast.ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Functions.showToast(getContext(), e.getLocalizedMessage(), FancyToast.ERROR);
                    }
                } else {
                    Functions.showToast(getContext(), getString(R.string.error_occured), FancyToast.ERROR);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Functions.hideLoader(hud);
                if (t instanceof UnknownHostException) {
                    Functions.showToast(getContext(), getString(R.string.check_internet_connection), FancyToast.ERROR);
                } else {
                    Functions.showToast(getContext(), t.getLocalizedMessage(), FancyToast.ERROR);
                }
            }
        });
    }

    private void getLocation(){
        new AirLocation(getActivity(), true, false, new AirLocation.Callbacks() {
            @Override
            public void onSuccess(Location loc) {
                getOwnerClubList(clubList.isEmpty(), loc.getLatitude(), loc.getLongitude(), "");
            }

            @Override
            public void onFailed(AirLocation.LocationFailedEnum locationFailedEnum) {
                getOwnerClubList(clubList.isEmpty(), 0.0,0.0,"");
            }
        });
    }



}
