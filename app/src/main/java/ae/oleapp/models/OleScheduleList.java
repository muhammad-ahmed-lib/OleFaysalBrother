package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OleScheduleList {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("is_scheduled")
    @Expose
    private Boolean isScheduled;
    @SerializedName("club")
    @Expose
    private Club club;
    @SerializedName("call_booking")
    @Expose
    private Boolean callBooking;
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
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("call_booking_id")
    @Expose
    private Integer callBookingId;
    @SerializedName("schedule_start_date")
    @Expose
    private String scheduleStartDate;
    @SerializedName("schedule_end_date")
    @Expose
    private String scheduleEndDate;
    @SerializedName("day_of_week")
    @Expose
    private String dayOfWeek;

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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(Boolean isScheduled) {
        this.isScheduled = isScheduled;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getCallBookingId() {
        return callBookingId;
    }

    public void setCallBookingId(Integer callBookingId) {
        this.callBookingId = callBookingId;
    }

    public String getScheduleStartDate() {
        return scheduleStartDate;
    }

    public void setScheduleStartDate(String scheduleStartDate) {
        this.scheduleStartDate = scheduleStartDate;
    }

    public String getScheduleEndDate() {
        return scheduleEndDate;
    }

    public void setScheduleEndDate(String scheduleEndDate) {
        this.scheduleEndDate = scheduleEndDate;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}
