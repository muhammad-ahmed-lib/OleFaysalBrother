package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FullBookingDetail {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("is_scheduled")
    @Expose
    private Boolean isScheduled;
    @SerializedName("is_loyalty_discount")
    @Expose
    private Boolean isLoyaltyDiscount;
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("payments")
    @Expose
    private List<Payments> payments;
    @SerializedName("player_payments")
    @Expose
    private List<PlayerPayments> playerPayments;
    @SerializedName("receipts")
    @Expose
    private String receipts;
    @SerializedName("facilities")
    @Expose
    private List<OleClubFacility> facilities;
    @SerializedName("club")
    @Expose
    private Club club;
    @SerializedName("call_booking")
    @Expose
    private Boolean callBooking;
    @SerializedName("match")
    @Expose
    private Match match;
    @SerializedName("hirings")
    @Expose
    private List<Hiring> hirings;
    @SerializedName("cancellation")
    @Expose
    private Cancellation cancellation;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("user")
    @Expose
    private UserInfo user;
    @SerializedName("field")
    @Expose
    private Field field;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("by")
    @Expose
    private String by;
    @SerializedName("total_price")
    @Expose
    private Integer totalPrice;
    @SerializedName("total_paid")
    @Expose
    private Integer totalPaid;
    @SerializedName("total_discount")
    @Expose
    private Integer totalDiscount;
    @SerializedName("total_balance")
    @Expose
    private Integer totalBalance;
    @SerializedName("collected_balance")
    @Expose
    private Integer collectedBalance;
    @SerializedName("remaining_balance")
    @Expose
    private Integer remainingBalance;
    @SerializedName("prices")
    @Expose
    private List<Price> prices;
    @SerializedName("discounts")
    @Expose
    private List<Discount> discounts;
    @SerializedName("balance")
    @Expose
    private List<Balance> balance;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("loyalty_details")
    @Expose
    private LoyaltyDetails loyaltyDetails;
    @SerializedName("bookings_details")
    @Expose
    private BookingsCountDetails bookingsDetails;
    @SerializedName("cancelled_history")
    @Expose
    private List<CancelledHistory> cancelledHistory;
    @SerializedName("logs")
    @Expose
    private List<BookingLogs> logs;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(Boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    public Boolean getIsLoyaltyDiscount() {
        return isLoyaltyDiscount;
    }

    public void setIsLoyaltyDiscount(Boolean isLoyaltyDiscount) {
        this.isLoyaltyDiscount = isLoyaltyDiscount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<Payments> getPayments() {
        return payments;
    }

    public void setPayments(List<Payments> payments) {
        this.payments = payments;
    }

    public List<PlayerPayments> getPlayerPayments() {
        return playerPayments;
    }

    public void setPlayerPayments(List<PlayerPayments> playerPayments) {
        this.playerPayments = playerPayments;
    }

    public String getReceipts() {
        return receipts;
    }

    public void setReceipts(String receipts) {
        this.receipts = receipts;
    }

    public List<OleClubFacility> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<OleClubFacility> facilities) {
        this.facilities = facilities;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Boolean getCallBooking() {
        return callBooking;
    }

    public void setCallBooking(Boolean callBooking) {
        this.callBooking = callBooking;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public List<Hiring> getHirings() {
        return hirings;
    }

    public void setHirings(List<Hiring> hirings) {
        this.hirings = hirings;
    }

    public Cancellation getCancellation() {
        return cancellation;
    }

    public void setCancellation(Cancellation cancellation) {
        this.cancellation = cancellation;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(Integer totalPaid) {
        this.totalPaid = totalPaid;
    }

    public Integer getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(Integer totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public Integer getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Integer totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Integer getCollectedBalance() {
        return collectedBalance;
    }

    public void setCollectedBalance(Integer collectedBalance) {
        this.collectedBalance = collectedBalance;
    }

    public Integer getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(Integer remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public List<Price> getPrices() {
        return prices;
    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

    public List<Discount> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<Discount> discounts) {
        this.discounts = discounts;
    }

    public List<Balance> getBalance() {
        return balance;
    }

    public void setBalance(List<Balance> balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LoyaltyDetails getLoyaltyDetails() {
        return loyaltyDetails;
    }

    public void setLoyaltyDetails(LoyaltyDetails loyaltyDetails) {
        this.loyaltyDetails = loyaltyDetails;
    }

    public BookingsCountDetails getBookingsDetails() {
        return bookingsDetails;
    }

    public void setBookingsDetails(BookingsCountDetails bookingsDetails) {
        this.bookingsDetails = bookingsDetails;
    }

    public List<CancelledHistory> getCancelledHistory() {
        return cancelledHistory;
    }

    public void setCancelledHistory(List<CancelledHistory> cancelledHistory) {
        this.cancelledHistory = cancelledHistory;
    }

    public List<BookingLogs> getLogs() {
        return logs;
    }

    public void setLogs(List<BookingLogs> logs) {
        this.logs = logs;
    }
}
