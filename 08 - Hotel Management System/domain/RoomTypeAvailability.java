package domain;

import java.util.List;

public class RoomTypeAvailability {
    private String roomTypeId;
    private String roomTypeName;
    private int capacity;
    private String bedType;
    private List<String> amenities;
    private boolean available;
    private long totalPrice;
    private double averagePricePerNight;
    private List<NightlyPrice> nightlyPrices;

    public RoomTypeAvailability() {
    }

    public RoomTypeAvailability(String roomTypeId, String roomTypeName, int capacity, String bedType,
            List<String> amenities, boolean available, long totalPrice,
            double averagePricePerNight, List<NightlyPrice> nightlyPrices) {
        this.roomTypeId = roomTypeId;
        this.roomTypeName = roomTypeName;
        this.capacity = capacity;
        this.bedType = bedType;
        this.amenities = amenities;
        this.available = available;
        this.totalPrice = totalPrice;
        this.averagePricePerNight = averagePricePerNight;
        this.nightlyPrices = nightlyPrices;
    }

    // Getters and Setters
    public String getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
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
        return "RoomTypeAvailability{" +
                "roomTypeId='" + roomTypeId + '\'' +
                ", roomTypeName='" + roomTypeName + '\'' +
                ", capacity=" + capacity +
                ", bedType='" + bedType + '\'' +
                ", available=" + available +
                ", totalPrice=" + totalPrice +
                ", averagePricePerNight=" + averagePricePerNight +
                '}';
    }
}
