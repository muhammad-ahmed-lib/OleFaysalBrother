package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Discount {
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("discount_by")
    @Expose
    private String discountBy;
    @SerializedName("promo_code")
    @Expose
    private Object promoCode;
    @SerializedName("field_offer")
    @Expose
    private Object fieldOffer;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDiscountBy() {
        return discountBy;
    }

    public void setDiscountBy(String discountBy) {
        this.discountBy = discountBy;
    }

    public Object getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(Object promoCode) {
        this.promoCode = promoCode;
    }

    public Object getFieldOffer() {
        return fieldOffer;
    }

    public void setFieldOffer(Object fieldOffer) {
        this.fieldOffer = fieldOffer;
    }
}
