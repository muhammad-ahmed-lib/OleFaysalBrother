package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PromoCode {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("discount")
    @Expose
    private Integer discount;
    @SerializedName("discount_type")
    @Expose
    private String discountType;
    @SerializedName("total_times_used")
    @Expose
    private Integer totalTimesUsed;
    @SerializedName("total_used")
    @Expose
    private Integer totalUsed;
    @SerializedName("usage_limit")
    @Expose
    private Integer usageLimit;
    @SerializedName("each_player_limit")
    @Expose
    private Integer eachPlayerLimit;
    @SerializedName("promo_type")
    @Expose
    private String promoType;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("photo_url")
    @Expose
    private String photoUrl;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("added_by")
    @Expose
    private AddedBy addedBy;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("club")
    @Expose
    private Club club;
    @SerializedName("total_discount")
    @Expose
    private Integer totalDiscount;
    @SerializedName("discounted_bookings")
    @Expose
    private List<Object> discountedBookings;
    @SerializedName("usage_details")
    @Expose
    private List<Object> usageDetails;
    @SerializedName("total_allowed_players")
    @Expose
    private Integer totalAllowedPlayers;
    @SerializedName("allowed_players")
    @Expose
    private List<Object> allowedPlayers;
    @SerializedName("allowed_field")
    @Expose
    private List<AllowedField> allowedField;
    @SerializedName("allowed_durations")
    @Expose
    private List<Object> allowedDurations;
    @SerializedName("total_eligible")
    @Expose
    private Integer totalEligible;
    @SerializedName("total_new_users")
    @Expose
    private Integer totalNewUsers;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Integer getTotalTimesUsed() {
        return totalTimesUsed;
    }

    public void setTotalTimesUsed(Integer totalTimesUsed) {
        this.totalTimesUsed = totalTimesUsed;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getEachPlayerLimit() {
        return eachPlayerLimit;
    }

    public void setEachPlayerLimit(Integer eachPlayerLimit) {
        this.eachPlayerLimit = eachPlayerLimit;
    }

    public String getPromoType() {
        return promoType;
    }

    public void setPromoType(String promoType) {
        this.promoType = promoType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AddedBy getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(AddedBy addedBy) {
        this.addedBy = addedBy;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Integer getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(Integer totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public List<Object> getDiscountedBookings() {
        return discountedBookings;
    }

    public void setDiscountedBookings(List<Object> discountedBookings) {
        this.discountedBookings = discountedBookings;
    }

    public List<Object> getUsageDetails() {
        return usageDetails;
    }

    public void setUsageDetails(List<Object> usageDetails) {
        this.usageDetails = usageDetails;
    }

    public Integer getTotalAllowedPlayers() {
        return totalAllowedPlayers;
    }

    public void setTotalAllowedPlayers(Integer totalAllowedPlayers) {
        this.totalAllowedPlayers = totalAllowedPlayers;
    }

    public List<Object> getAllowedPlayers() {
        return allowedPlayers;
    }

    public void setAllowedPlayers(List<Object> allowedPlayers) {
        this.allowedPlayers = allowedPlayers;
    }

    public List<AllowedField> getAllowedField() {
        return allowedField;
    }

    public void setAllowedField(List<AllowedField> allowedField) {
        this.allowedField = allowedField;
    }

    public List<Object> getAllowedDurations() {
        return allowedDurations;
    }

    public void setAllowedDurations(List<Object> allowedDurations) {
        this.allowedDurations = allowedDurations;
    }

    public Integer getTotalUsed() {
        return totalUsed;
    }

    public void setTotalUsed(Integer totalUsed) {
        this.totalUsed = totalUsed;
    }

    public Integer getTotalEligible() {
        return totalEligible;
    }

    public void setTotalEligible(Integer totalEligible) {
        this.totalEligible = totalEligible;
    }

    public Integer getTotalNewUsers() {
        return totalNewUsers;
    }

    public void setTotalNewUsers(Integer totalNewUsers) {
        this.totalNewUsers = totalNewUsers;
    }

}
