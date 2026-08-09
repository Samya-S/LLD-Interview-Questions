package domain;

import java.util.List;

public class RoomType {
    private String id;
    private String hotelId;
    private String name;
    private int capacity;
    private String bedType;
    private long basePrice;
    private List<String> amenities;
    private int totalRooms;
    private boolean isActive;
    private long createdAt;

    public RoomType() {
    }

    public RoomType(String id, String hotelId, String name, int capacity, String bedType,
            long basePrice, List<String> amenities, int totalRooms, boolean isActive, long createdAt) {
        this.id = id;
        this.hotelId = hotelId;
        this.name = name;
        this.capacity = capacity;
        this.bedType = bedType;
        this.basePrice = basePrice;
        this.amenities = amenities;
        this.totalRooms = totalRooms;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(long basePrice) {
        this.basePrice = basePrice;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "RoomType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", bedType='" + bedType + '\'' +
                ", basePrice=" + basePrice +
                ", totalRooms=" + totalRooms +
                '}';
    }
}
