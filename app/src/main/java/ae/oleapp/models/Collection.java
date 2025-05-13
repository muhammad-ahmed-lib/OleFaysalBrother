package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Collection {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("notes")
    @Expose
    private Object notes;
    @SerializedName("receipt")
    @Expose
    private String receipt;
    @SerializedName("collected_at")
    @Expose
    private String collectedAt;
    @SerializedName("collected_by")
    @Expose
    private CollectedBy collectedBy;
    @SerializedName("currency")
    @Expose
    private String currency;

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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getNotes() {
        return notes;
    }

    public void setNotes(Object notes) {
        this.notes = notes;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getCollectedAt() {
        return collectedAt;
    }

    public void setCollectedAt(String collectedAt) {
        this.collectedAt = collectedAt;
    }

    public CollectedBy getCollectedBy() {
        return collectedBy;
    }

    public void setCollectedBy(CollectedBy collectedBy) {
        this.collectedBy = collectedBy;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
