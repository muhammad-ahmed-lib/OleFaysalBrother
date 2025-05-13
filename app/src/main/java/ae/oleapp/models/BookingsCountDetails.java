package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookingsCountDetails {
    @SerializedName("completed")
    @Expose
    private Integer completed;
    @SerializedName("canceled")
    @Expose
    private Integer canceled;
    @SerializedName("pending")
    @Expose
    private Integer pending;
    @SerializedName("current_month")
    @Expose
    private Integer currentMonth;

    public Integer getCompleted() {
        return completed;
    }

    public void setCompleted(Integer completed) {
        this.completed = completed;
    }

    public Integer getCanceled() {
        return canceled;
    }

    public void setCanceled(Integer canceled) {
        this.canceled = canceled;
    }

    public Integer getPending() {
        return pending;
    }

    public void setPending(Integer pending) {
        this.pending = pending;
    }

    public Integer getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(Integer currentMonth) {
        this.currentMonth = currentMonth;
    }
}
