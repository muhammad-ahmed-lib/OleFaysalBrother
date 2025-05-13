package ae.oleapp.models;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Player {

    private Bitmap image;

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("shirt")
    @Expose
    private String shirt;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("owner_club")
    @Expose
    private String ownerClub;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("photo_url")
    @Expose
    private String photoUrl;
    @SerializedName("emoji_url")
    @Expose
    private String emojiUrl;
    @SerializedName("bib_url")
    @Expose
    private String bibUrl;
    @SerializedName("total_bookings")
    @Expose
    private String totalBookings;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getEmojiUrl() {
        return emojiUrl;
    }

    public void setEmpjiUrl(String emojiUrl) {
        this.emojiUrl = emojiUrl;
    }

    public String getBibUrl() {
        return bibUrl;
    }

    public void setBibUrl(String bibUrl) {
        this.bibUrl = bibUrl;
    }

    public String getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(String bookingsCount) {
        this.totalBookings = bookingsCount;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    // Add a getter method for the downloaded bitmap
    public Bitmap getImage() {
        return image;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getShirt() {
        return shirt;
    }

    public void setShirt(String shirt) {
        this.shirt = shirt;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOwnerClub() {
        return ownerClub;
    }

    public void setOwnerClub(String ownerClub) {
        this.ownerClub = ownerClub;
    }
}
