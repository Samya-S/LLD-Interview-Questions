package domain;

public class NightlyPrice {
    private long dateUtc;
    private long priceMinor;

    public NightlyPrice() {
    }

    public NightlyPrice(long dateUtc, long priceMinor) {
        this.dateUtc = dateUtc;
        this.priceMinor = priceMinor;
    }

    public long getDateUtc() {
        return dateUtc;
    }

    public void setDateUtc(long dateUtc) {
        this.dateUtc = dateUtc;
    }

    public long getPriceMinor() {
        return priceMinor;
    }

    public void setPriceMinor(long priceMinor) {
        this.priceMinor = priceMinor;
    }

    @Override
    public String toString() {
        return "NightlyPrice{" +
                "dateUtc=" + dateUtc +
                ", priceMinor=" + priceMinor +
                '}';
    }
}
