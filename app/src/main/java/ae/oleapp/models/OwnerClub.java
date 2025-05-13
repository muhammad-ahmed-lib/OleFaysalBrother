package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OwnerClub {
    @SerializedName("new_users")
    @Expose
    private Integer newUsers;
    @SerializedName("total_bookings")
    @Expose
    private Integer totalBookings;
    @SerializedName("active_bookings")
    @Expose
    private Integer activeBookings;
    @SerializedName("confirmed_bookings")
    @Expose
    private Integer confirmedBookings;
    @SerializedName("completed_bookings")
    @Expose
    private Integer completedBookings;
    @SerializedName("canceled_bookings")
    @Expose
    private Integer canceledBookings;
    @SerializedName("total_earning")
    @Expose
    private Integer totalEarning;
    @SerializedName("waiting_users")
    @Expose
    private Integer waitingUser;
    @SerializedName("is_featured")
    @Expose
    private Boolean isFeatured;
    @SerializedName("total_hours")
    @Expose
    private Integer totalHours;
    @SerializedName("total_favorites")
    @Expose
    private Integer totalFavorites;
    @SerializedName("total_reviews")
    @Expose
    private Integer totalReviews;
    @SerializedName("total_fields")
    @Expose
    private Integer totalFields;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("country")
    @Expose
    private Country country;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("subscription")
    @Expose
    private SubscriptionOwnerClub subscription;
    @SerializedName("offer")
    @Expose
    private Offer offer;

    public Integer getNewUsers() {
        return newUsers;
    }

    public void setNewUsers(Integer newUsers) {
        this.newUsers = newUsers;
    }

    public Integer getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Integer totalBookings) {
        this.totalBookings = totalBookings;
    }

    public Integer getActiveBookings() {
        return activeBookings;
    }

    public void setActiveBookings(Integer activeBookings) {
        this.activeBookings = activeBookings;
    }

    public Integer getConfirmedBookings() {
        return confirmedBookings;
    }

    public void setConfirmedBookings(Integer confirmedBookings) {
        this.confirmedBookings = confirmedBookings;
    }

    public Integer getCompletedBookings() {
        return completedBookings;
    }

    public void setCompletedBookings(Integer completedBookings) {
        this.completedBookings = completedBookings;
    }

    public Integer getCanceledBookings() {
        return canceledBookings;
    }

    public void setCanceledBookings(Integer canceledBookings) {
        this.canceledBookings = canceledBookings;
    }

    public Integer getTotalEarning() {
        return totalEarning;
    }

    public void setTotalEarning(Integer totalEarning) {
        this.totalEarning = totalEarning;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public Integer getTotalFavorites() {
        return totalFavorites;
    }

    public void setTotalFavorites(Integer totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Integer getTotalFields() {
        return totalFields;
    }

    public void setTotalFields(Integer totalFields) {
        this.totalFields = totalFields;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public SubscriptionOwnerClub getSubscription() {
        return subscription;
    }

    public void setSubscription(SubscriptionOwnerClub subscription) {
        this.subscription = subscription;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Integer getWaitingUser() {
        return waitingUser;
    }

    public void setWaitingUser(Integer waitingUser) {
        this.waitingUser = waitingUser;
    }

    public Boolean getFeatured() {
        return isFeatured;
    }

    public void setFeatured(Boolean featured) {
        isFeatured = featured;
    }
}
