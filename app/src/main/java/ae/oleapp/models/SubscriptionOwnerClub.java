package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionOwnerClub {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("is_trial")
    @Expose
    private Boolean isTrial;
    @SerializedName("expiry_in")
    @Expose
    private String expiryIn;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsTrial() {
        return isTrial;
    }

    public void setIsTrial(Boolean isTrial) {
        this.isTrial = isTrial;
    }

    public String getExpiryIn() {
        return expiryIn;
    }

    public void setExpiryIn(String expiryIn) {
        this.expiryIn = expiryIn;
    }

}
