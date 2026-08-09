package domain;

public class SeasonalPrice {
    private String id;
    private String hotelId;
    private String roomTypeId;
    private long dateUtc;
    private long priceMinor;
    private long createdAt;

    public SeasonalPrice() {
    }

    public SeasonalPrice(String id, String hotelId, String roomTypeId, long dateUtc, long priceMinor, long createdAt) {
        this.id = id;
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.dateUtc = dateUtc;
        this.priceMinor = priceMinor;
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

    public String getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "SeasonalPrice{" +
                "id='" + id + '\'' +
                ", hotelId='" + hotelId + '\'' +
                ", roomTypeId='" + roomTypeId + '\'' +
                ", dateUtc=" + dateUtc +
                ", priceMinor=" + priceMinor +
                '}';
    }
}
