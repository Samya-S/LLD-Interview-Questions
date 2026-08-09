package domain;

import java.util.List;

public class Availability {
    private boolean available;
    private long totalPrice;
    private double averagePricePerNight;
    private List<NightlyPrice> nightlyPrices;

    public Availability() {
    }

    public Availability(boolean available, long totalPrice, double averagePricePerNight,
            List<NightlyPrice> nightlyPrices) {
        this.available = available;
        this.totalPrice = totalPrice;
        this.averagePricePerNight = averagePricePerNight;
        this.nightlyPrices = nightlyPrices;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getAveragePricePerNight() {
        return averagePricePerNight;
    }

    public void setAveragePricePerNight(double averagePricePerNight) {
        this.averagePricePerNight = averagePricePerNight;
    }

    public List<NightlyPrice> getNightlyPrices() {
        return nightlyPrices;
    }

    public void setNightlyPrices(List<NightlyPrice> nightlyPrices) {
        this.nightlyPrices = nightlyPrices;
    }

    @Override
    public String toString() {
        return "Availability{" +
                "available=" + available +
                ", totalPrice=" + totalPrice +
                ", averagePricePerNight=" + averagePricePerNight +
                '}';
    }
}
