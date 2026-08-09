package domain;

public class DateRange {
    private long checkInDateUtc;
    private long checkOutDateUtc; // exclusive

    public DateRange() {
    }

    public DateRange(long checkInDateUtc, long checkOutDateUtc) {
        this.checkInDateUtc = checkInDateUtc;
        this.checkOutDateUtc = checkOutDateUtc;
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
}
