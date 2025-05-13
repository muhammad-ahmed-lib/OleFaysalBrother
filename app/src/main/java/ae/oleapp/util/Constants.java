package ae.oleapp.util;

public class Constants {


    public static String BASE_URL_NEW = "https://owner.ole-app.ae/api/";  //Test

    public static String BASE_URL = "https://api.ole-sports.com/";    // Test
//    public static String BASE_URL = "https://www.ole-sports.com/";  // production
    public static String NODE_BASE_URL = "https://api.ole-app.ae/api/";  // Node Test
//    public static String NODE_BASE_URL = "https://node.ole-sports.com/api/";  // Node production

    public static String PARTNER_BASE_URL = "https://finance.ole-app.ae/api/";  // Partner Test


    public static String kStatus = "status";
    public static String kMsg = "message";
    public static String kData = "data";
    public static int kSuccessCode = 200;
    public static int kVerifyPhoneCode = 401;
    public static String kArLang = "1";
    public static String kEnLang = "0";
    public static String kOwnerType = "OWNER";
    public static String kNormalBooking = "normal_booking";
    public static String kFriendlyGame = "friendly_game";
    public static String kPublicChallenge = "public_challenge";
    public static String kPrivateChallenge = "private_challenge";
    public static String kManualPlayer = "manual";
    public static String kRegisteredPlayer = "registered";
    public static String kFootballModule = "football";
    public static String kPadelModule = "padel";
    public static String kLineupModule = "lineup";
    public static String kaccessToken = "session_token";
    public static String kRefreshToken = "refresh_token";
    public static String kPendingBooking = "PENDING";
    public static String kConfirmedBooking = "CONFIRMED";
    public static String kCancelledBooking = "CANCELLED";
    public static String kCompletedBooking = "COMPLETED";

    public static String kBlockedBooking = "5";
    public static String kExpiredBooking = "8";

    // preference values
    public static String PREF_NAME = "ole_pref";
    public static String kUserID = "user_id";
    public static String kUserName = "user_name";
    public static String kIsEmp = "is_emp";
    public static String kEncPass = "enc_pass";
    public static String kIsSignIn = "is_signin";
    public static String kUserType = "user_type";
    public static String kFCMToken = "fcm_token";
    public static String kUserInfo = "user_info";
    public static String kAppLang = "app_lang";
    public static String kAppLangAr = "app_langar";
    public static String kCurrency = "currency";
    public static String kLoginType = "login_type";
    public static String firstTimeLineup = "exit_lineup";
    public static String firstLineup = "first_lineup";
    public static String kAllowModule = "allow_module";
    public static String kBookingDateIndex = "booking_date_index";
    public static String kShopCoupon = "shop_coupon";
    public static String kOwnerBookingSelectedClub = "owner_booking_selected_club";
    public static String kOwnerEarningSelectedClub = "owner_earning_selected_club";
    public static String kOwnerEarningFromDate = "owner_earning_from_date";
    public static String kOwnerEarningToDate = "owner_earning_to_date";
    public static String kAppModule = "current_app_module";
    public static String groupCallStatus = "groups";
    public static String kDeviceUniqueId = "device_unique_id";
    public static String kInventoryOrderFromDate = "inventory_order_from_date";
    public static String kInventoryOrderToDate = "inventory_order_to_date";
    public static String kInventorySaleReportFromDate = "inventory_sale_report_from_date";
    public static String kInventorySaleReportToDate = "inventory_sale_report_to_date";
    public static String kInventoryProfitReportFromDate = "inventory_profit_report_from_date";
    public static String kInventoryProfitReportToDate = "inventory_profit_report_to_date";
    public static final String kAppInstallDate = "android_rate_install_date";
    public static final String kAppLaunchTimes = "android_rate_launch_times";
    public static final String kAppRemindInterval = "android_rate_remind_interval";
    public static String kCurrentPage = "current_page";
    public static String kSelectedCountry = "selected_country";
    public static String kSelectedTeam = "selected_team";
    public static String kFaceHide = "face_hide";
    public static String kNameHide = "name_change";

    // notification type
    public static String kNewBookingNotification = "booking";
    public static String kCancelBookingByOwnerNotification = "cancelByOwner";
    public static String kConfirmBookingByOwnerNotification = "confirmByOwner";
    public static String kCancelBookingByPlayerNotification = "cancelByPlayer";
    public static String kConfirmBookingByPlayerNotification = "confirmByPlayer";
    public static String kNewInvitationNotification = "newInvitation";
    public static String kRejectInvitationNotification = "rejectedInvitation";
    public static String kAcceptInvitationNotification = "acceptedInvitation";
    public static String kCancelInvitationBySenderNotification = "canceledInvitationSender";
    public static String kCancelInvitationByReceiverNotification = "canceledInvitationReceiver";
    public static String kNewOfferNotification = "newOffer";
    public static String kBookingAvailableNotification = "bookingAvailable";
    public static String kBookingReminderNotification = "bookingReminder";
    public static String kBookingCompleteNotification = "completeBooking";
    public static String kMatchRequestNotification = "matchRequest";
    public static String kChallengeAcceptedNotification = "challengeAccepted";
    public static String kAppUpdateNotification = "appUpdate";
    public static String kNewGameRequestNotification = "newGameRequest";
    public static String kInviteMorePlayersNotification = "inviteMorePlayers";
    public static String kCreatorCanceledAcceptedMatchNotification = "creatorCanceledAcceptedMatch";
    public static String kPlayerCanceledAcceptedMatchNotification = "playerCanceledAcceptedMatch";
    public static String kNewChallengeRequestNotification = "newChallengeRequest";
    public static String kPrivateChallengeAcceptedNotification = "privateChallengeAccepted";
    public static String kPrivateChallengeCanceledNotification = "privateChallengeCanceled";
    public static String kPublicChallengeAcceptedNotification = "publicChallengeAccepted";
    public static String kPublicChallengeCanceledNotification = "publicChallengeCanceled";
    public static String kPublicChallengeCanceledByPlayerNotification = "publicChallengeCanceledByPlayer";
    public static String kGameRequestAcceptedNotification = "gameRequestAccepted";
    public static String kGameRequestCanceledNotification = "gameRequestCanceled";
    public static String kNewInvitationRequestNotification = "newInvitationRequest";
    public static String kJoinFriendlyGameNotification = "joinFriendlyGame";
    public static String kNewCaptainGameNotification = "newCaptainGame";
    public static String kShoppingNotification = "shopping";
    public static String kInvitationPadel = "invitationPadel";
    public static String kInvitationRejectedByPlayer = "invitationRejectedByPlayer";
    public static String kInvitationAcceptedByPlayer = "invitationAcceptedByPlayer";
    public static String kChallengePadel = "challengePadel";
    public static String kPadelChallengeCanceled = "padelChallengeCanceled";
    public static String kPadelChallengeRejected = "padelChallengeRejected";
    public static String kAcceptedChallengeCanceledByPlayer = "acceptedChallengeCanceledByPlayer";
    public static String kAcceptedChallengeCanceledByCreator = "acceptedChallengeCanceledByCreator";
    public static String kPadelChallengeAccepted = "padelChallengeAccepted";
    public static String kPartnerForChallenge = "partnerForChallenge";
    public static String kLevelCompleted = "levelCompleted";
    public static String kCompletePayment = "completePayment";
    public static String kYouWonTheMatch = "youWonTheMatch";
    public static String kYouWonTheMatchPadel = "youWonTheMatchPadel";

}
