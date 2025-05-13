package ae.oleapp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PromoCount {
    @SerializedName("promo_active")
    @Expose
    private Integer promoActive;
    @SerializedName("promo_expired")
    @Expose
    private Integer promoExpired;
    @SerializedName("offer_active")
    @Expose
    private Integer offerActive;
    @SerializedName("offer_expired")
    @Expose
    private Integer offerExpired;
    @SerializedName("gift_active")
    @Expose
    private Integer giftActive;
    @SerializedName("gift_expired")
    @Expose
    private Integer giftExpired;
    @SerializedName("loyalty_active")
    @Expose
    private Integer loyaltyActive;
    @SerializedName("loyalty_expired")
    @Expose
    private Integer loyaltyExpired;

    public Integer getPromoActive() {
        return promoActive;
    }

    public void setPromoActive(Integer promoActive) {
        this.promoActive = promoActive;
    }

    public Integer getPromoExpired() {
        return promoExpired;
    }

    public void setPromoExpired(Integer promoExpired) {
        this.promoExpired = promoExpired;
    }

    public Integer getOfferActive() {
        return offerActive;
    }

    public void setOfferActive(Integer offerActive) {
        this.offerActive = offerActive;
    }

    public Integer getOfferExpired() {
        return offerExpired;
    }

    public void setOfferExpired(Integer offerExpired) {
        this.offerExpired = offerExpired;
    }

    public Integer getGiftActive() {
        return giftActive;
    }

    public void setGiftActive(Integer giftActive) {
        this.giftActive = giftActive;
    }

    public Integer getGiftExpired() {
        return giftExpired;
    }

    public void setGiftExpired(Integer giftExpired) {
        this.giftExpired = giftExpired;
    }

    public Integer getLoyaltyActive() {
        return loyaltyActive;
    }

    public void setLoyaltyActive(Integer loyaltyActive) {
        this.loyaltyActive = loyaltyActive;
    }

    public Integer getLoyaltyExpired() {
        return loyaltyExpired;
    }

    public void setLoyaltyExpired(Integer loyaltyExpired) {
        this.loyaltyExpired = loyaltyExpired;
    }
}
