package ru.yesdo.model.data;


import ru.yesdo.model.TimeCost;

/**
 * Created by lameroot on 26.04.15.
 */
public class OfferIdentificator {

    private Long offerId;
    private Long productId;
    private Long merchantId;
    private TimeCost timeCost;


    public OfferIdentificator(Long offerId, Long productId, Long merchantId) {
        this.offerId = offerId;
        this.productId = productId;
        this.merchantId = merchantId;
    }
    public OfferIdentificator(Long offerId, Long productId, Long merchantId, TimeCost timeCost) {
        this(offerId, productId, merchantId);
        this.timeCost = timeCost;
    }

    public Long getOfferId() {
        return offerId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public TimeCost getTimeCost() {
        return timeCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OfferIdentificator that = (OfferIdentificator) o;

        if (!offerId.equals(that.offerId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return offerId.hashCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OfferIdentificator{");
        sb.append("offerId=").append(offerId);
        sb.append(", productId=").append(productId);
        sb.append(", merchantId=").append(merchantId);
        sb.append('}');
        return sb.toString();
    }
}
