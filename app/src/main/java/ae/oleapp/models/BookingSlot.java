package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookingSlot {
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("end")
    @Expose
    private String end;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("slot")
    @Expose
    private String slot;
    @SerializedName("booking_id")
    @Expose
    private String bookingId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_phone")
    @Expose
    private String userPhone;
    @SerializedName("booking_status")
    @Expose
    private String bookingStatus;
    @SerializedName("is_schedule")
    @Expose
    private String isSchedule;
    @SerializedName("lady_slot")
    @Expose
    private String ladySlot;
    @SerializedName("has_waiting_list")
    @Expose
    private String hasWaitingList;
    @SerializedName("is_offered_slot")
    @Expose
    private Boolean isOfferedSlot;
    @SerializedName("field_offer_id")
    @Expose
    private Integer fieldOfferId;
    @SerializedName("field_offer_type")
    @Expose
    private String fieldOfferType;
    @SerializedName("field_offer_value")
    @Expose
    private Integer fieldOfferValue;

    private String slotId = "";
    @SerializedName("is_selected")
    @Expose
    private String isSelected;
    @SerializedName("shift")
    @Expose
    private String shift;

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getIsSchedule() {
        return isSchedule;
    }

    public void setIsSchedule(String isSchedule) {
        this.isSchedule = isSchedule;
    }

    public String getLadySlot() {
        return ladySlot;
    }

    public void setLadySlot(String ladySlot) {
        this.ladySlot = ladySlot;
    }

    public String getHasWaitingList() {
        return hasWaitingList;
    }

    public void setHasWaitingList(String hasWaitingList) {
        this.hasWaitingList = hasWaitingList;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(String isSelected) {
        this.isSelected = isSelected;
    }

    public Boolean getIsOfferedSlot() {
        return isOfferedSlot;
    }

    public void setIsOfferedSlot(Boolean isOfferedSlot) {
        this.isOfferedSlot = isOfferedSlot;
    }

    public Integer getFieldOfferId() {
        return fieldOfferId;
    }

    public void setFieldOfferId(Integer fieldOfferId) {
        this.fieldOfferId = fieldOfferId;
    }

    public String getFieldOfferType() {
        return fieldOfferType;
    }

    public void setFieldOfferType(String fieldOfferType) {
        this.fieldOfferType = fieldOfferType;
    }

    public Integer getFieldOfferValue() {
        return fieldOfferValue;
    }

    public void setFieldOfferValue(Integer fieldOfferValue) {
        this.fieldOfferValue = fieldOfferValue;
    }

}