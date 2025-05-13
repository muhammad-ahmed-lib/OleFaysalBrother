package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Balance {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("is_cleared")
    @Expose
    private Boolean isCleared;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("added_by")
    @Expose
    private AddedBy addedBy;
    @SerializedName("collections")
    @Expose
    private List<Collection> collections;
    @SerializedName("booking")
    @Expose
    private BasicBookingDetail booking;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Boolean getIsCleared() {
        return isCleared;
    }

    public void setIsCleared(Boolean isCleared) {
        this.isCleared = isCleared;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    public BasicBookingDetail getBooking() {
        return booking;
    }

    public void setBooking(BasicBookingDetail booking) {
        this.booking = booking;
    }

    public AddedBy getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(AddedBy addedBy) {
        this.addedBy = addedBy;
    }
}

