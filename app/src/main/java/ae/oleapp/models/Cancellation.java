package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cancellation {
    @SerializedName("cancellation_reason")
    @Expose
    private String cancellationReason;
    @SerializedName("cancelled_time_span")
    @Expose
    private Integer cancelledTimeSpan;
    @SerializedName("cancelled_at")
    @Expose
    private String cancelledAt;

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public Integer getCancelledTimeSpan() {
        return cancelledTimeSpan;
    }

    public void setCancelledTimeSpan(Integer cancelledTimeSpan) {
        this.cancelledTimeSpan = cancelledTimeSpan;
    }

    public String getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(String cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

}
