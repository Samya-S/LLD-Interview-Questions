package domain;

import java.util.List;

public class Booking {
    private String id;
    private String userId;
    private String hotelId;
    private String roomTypeId;
    private long checkInDateUtc;
    private long checkOutDateUtc; // exclusive
    private List<NightlyPrice> nightlyPrices;
    private long totalAmountMinor;
    private BookingStatus bookingStatus;
    private TransactionStatus paymentStatus;
    private String allocatedRoomId; // nullable, assigned at check-in
    private long checkInTimeUtc; // nullable, set by admin at check-in
    private long checkOutTimeUtc; // nullable, set by admin at check-out
    private long holdExpiresAt;
    private long createdAt;

    public Booking() {
    }

    public Booking(String id, String userId, String hotelId, String roomTypeId,
            long checkInDateUtc, long checkOutDateUtc, List<NightlyPrice> nightlyPrices,
            long totalAmountMinor, BookingStatus bookingStatus, TransactionStatus paymentStatus,
            String allocatedRoomId, long checkInTimeUtc, long checkOutTimeUtc,
            long holdExpiresAt, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.checkInDateUtc = checkInDateUtc;
        this.checkOutDateUtc = checkOutDateUtc;
        this.nightlyPrices = nightlyPrices;
        this.totalAmountMinor = totalAmountMinor;
        this.bookingStatus = bookingStatus;
        this.paymentStatus = paymentStatus;
        this.allocatedRoomId = allocatedRoomId;
        this.checkInTimeUtc = checkInTimeUtc;
        this.checkOutTimeUtc = checkOutTimeUtc;
        this.holdExpiresAt = holdExpiresAt;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public long getCheckInDateUtc() {
        return checkInDateUtc;
    }

    public void setCheckInDateUtc(long checkInDateUtc) {
        this.checkInDateUtc = checkInDateUtc;
    }

    public long getCheckOutDateUtc() {
        return checkOutDateUtc;
    }

    public void setCheckOutDateUtc(long checkOutDateUtc) {
        this.checkOutDateUtc = checkOutDateUtc;
    }

    public List<NightlyPrice> getNightlyPrices() {
        return nightlyPrices;
    }

    public void setNightlyPrices(List<NightlyPrice> nightlyPrices) {
        this.nightlyPrices = nightlyPrices;
    }

    public long getTotalAmountMinor() {
        return totalAmountMinor;
    }

    public void setTotalAmountMinor(long totalAmountMinor) {
        this.totalAmountMinor = totalAmountMinor;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public TransactionStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(TransactionStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getAllocatedRoomId() {
        return allocatedRoomId;
    }

    public void setAllocatedRoomId(String allocatedRoomId) {
        this.allocatedRoomId = allocatedRoomId;
    }

    public long getCheckInTimeUtc() {
        return checkInTimeUtc;
    }

    public void setCheckInTimeUtc(long checkInTimeUtc) {
        this.checkInTimeUtc = checkInTimeUtc;
    }

    public long getCheckOutTimeUtc() {
        return checkOutTimeUtc;
    }

    public void setCheckOutTimeUtc(long checkOutTimeUtc) {
        this.checkOutTimeUtc = checkOutTimeUtc;
    }

    public long getHoldExpiresAt() {
        return holdExpiresAt;
    }

    public void setHoldExpiresAt(long holdExpiresAt) {
        this.holdExpiresAt = holdExpiresAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", hotelId='" + hotelId + '\'' +
                ", roomTypeId='" + roomTypeId + '\'' +
                ", bookingStatus=" + bookingStatus +
                ", paymentStatus=" + paymentStatus +
                ", totalAmountMinor=" + totalAmountMinor +
                '}';
    }
}
