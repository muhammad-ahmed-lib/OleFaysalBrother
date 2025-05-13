package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancelledHistory {
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("cancelled_before_hours")
    @Expose
    private String cancelledBeforeHours;
    @SerializedName("cancelled_date")
    @Expose
    private String cancelledDate;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCancelledBeforeHours() {
        return cancelledBeforeHours;
    }

    public void setCancelledBeforeHours(String cancelledBeforeHours) {
        this.cancelledBeforeHours = cancelledBeforeHours;
    }

    public String getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(String cancelledDate) {
        this.cancelledDate = cancelledDate;
    }
}
