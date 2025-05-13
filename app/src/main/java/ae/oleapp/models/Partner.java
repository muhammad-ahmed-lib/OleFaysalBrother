package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Partner {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("phone_country_id")
    @Expose
    private Integer phoneCountryId;
    @SerializedName("phone_country_code")
    @Expose
    private String phoneCountryCode;
    @SerializedName("profile_photo")
    @Expose
    private String profilePhoto;
    @SerializedName("club")
    @Expose
    private Club club;
    @SerializedName("shares")
    @Expose
    private Integer shares;
    @SerializedName("shares_type")
    @Expose
    private String sharesType;
    @SerializedName("net_profit")
    @Expose
    private NetProfit netProfit;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("documents")
    @Expose
    private List<Document> documents;
    @SerializedName("total_documents")
    @Expose
    private Integer totalDocuments;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getPhoneCountryId() {
        return phoneCountryId;
    }

    public void setPhoneCountryId(Integer phoneCountryId) {
        this.phoneCountryId = phoneCountryId;
    }

    public String getPhoneCountryCode() {
        return phoneCountryCode;
    }

    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Integer getShares() {
        return shares;
    }

    public void setShares(Integer shares) {
        this.shares = shares;
    }

    public String getSharesType() {
        return sharesType;
    }

    public void setSharesType(String sharesType) {
        this.sharesType = sharesType;
    }

    public NetProfit getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(NetProfit netProfit) {
        this.netProfit = netProfit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Integer getTotalDocuments() {
        return totalDocuments;
    }

    public void setTotalDocuments(Integer totalDocuments) {
        this.totalDocuments = totalDocuments;
    }


}
